package com.avenor.launcher

import android.content.ComponentName
import android.content.Context
import android.util.AtomicFile
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal sealed interface FavoriteReadState {
    data object Loading : FavoriteReadState
    data class Readable(
        val primaryIdentities: List<LaunchableIdentity>,
        val companionIdentities: List<LaunchableIdentity> = emptyList(),
    ) : FavoriteReadState {
        val identities: List<LaunchableIdentity>
            get() = primaryIdentities + companionIdentities
    }
    data object ReadFailure : FavoriteReadState
}

internal interface FavoriteStore {
    val state: StateFlow<FavoriteReadState>
    suspend fun load()
    suspend fun add(identity: LaunchableIdentity): Boolean
    suspend fun remove(identity: LaunchableIdentity): Boolean
    suspend fun removeAll(identities: Set<LaunchableIdentity>): Boolean
    suspend fun replaceOrder(identities: List<LaunchableIdentity>): Boolean
    suspend fun replaceComposition(
        primaryIdentities: List<LaunchableIdentity>,
        companionIdentities: List<LaunchableIdentity>,
    ): Boolean
}

internal class AtomicFileFavoriteStore private constructor(
    private val atomicFile: AtomicFile,
) : FavoriteStore {
    constructor(context: Context) : this(AtomicFile(context.filesDir.resolve(FILE_NAME)))
    internal constructor(file: java.io.File) : this(AtomicFile(file))
    private val mutationMutex = Mutex()
    private val mutableState = MutableStateFlow<FavoriteReadState>(FavoriteReadState.Loading)

    override val state: StateFlow<FavoriteReadState> = mutableState

    override suspend fun load() = mutationMutex.withLock {
        mutableState.value = FavoriteReadState.Loading
        mutableState.value = withContext(Dispatchers.IO) {
            if (!atomicFile.baseFile.exists()) {
                FavoriteReadState.Readable(emptyList())
            } else {
                try {
                    val document = readDocument()
                    val state = FavoriteReadState.Readable(
                        document.primaryIdentities,
                        document.companionIdentities,
                    )
                    if (document.schemaVersion == LEGACY_SCHEMA_VERSION) {
                        // Favorites that were read successfully stay readable even when the
                        // upgrade write fails; the legacy document remains on disk and the next
                        // load retries the migration.
                        try {
                            writeDocument(state.primaryIdentities, state.companionIdentities)
                        } catch (cancellation: CancellationException) {
                            throw cancellation
                        } catch (_: Exception) {
                            // Keep the readable state read from the legacy document.
                        }
                    }
                    state
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: Exception) {
                    FavoriteReadState.ReadFailure
                }
            }
        }
    }

    override suspend fun add(identity: LaunchableIdentity): Boolean = mutationMutex.withLock {
        val readable = mutableState.value as? FavoriteReadState.Readable ?: return false
        if (identity in readable.identities) return true
        val updated = readable.primaryIdentities + identity
        val writeSucceeded = withContext(Dispatchers.IO) {
            try {
                writeDocument(updated, readable.companionIdentities)
                true
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                false
            }
        }
        if (writeSucceeded) {
            mutableState.value = FavoriteReadState.Readable(
                updated,
                readable.companionIdentities,
            )
        }
        writeSucceeded
    }

    override suspend fun remove(identity: LaunchableIdentity): Boolean = mutationMutex.withLock {
        val readable = mutableState.value as? FavoriteReadState.Readable ?: return false
        if (identity !in readable.identities) return true

        val updatedPrimary = readable.primaryIdentities - identity
        val updatedCompanion = readable.companionIdentities - identity
        val writeSucceeded = withContext(Dispatchers.IO) {
            try {
                writeDocument(updatedPrimary, updatedCompanion)
                true
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                false
            }
        }
        if (writeSucceeded) {
            mutableState.value = FavoriteReadState.Readable(updatedPrimary, updatedCompanion)
        }
        writeSucceeded
    }

    override suspend fun removeAll(identities: Set<LaunchableIdentity>): Boolean =
        mutationMutex.withLock {
            val readable = mutableState.value as? FavoriteReadState.Readable ?: return false
            val updatedPrimary = readable.primaryIdentities.filterNot(identities::contains)
            val updatedCompanion = readable.companionIdentities.filterNot(identities::contains)
            if (updatedPrimary.size == readable.primaryIdentities.size &&
                updatedCompanion.size == readable.companionIdentities.size
            ) return true

            val writeSucceeded = withContext(Dispatchers.IO) {
                try {
                    writeDocument(updatedPrimary, updatedCompanion)
                    true
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: Exception) {
                    false
                }
            }
            if (writeSucceeded) {
                mutableState.value = FavoriteReadState.Readable(
                    updatedPrimary,
                    updatedCompanion,
                )
            }
            writeSucceeded
        }

    override suspend fun replaceOrder(identities: List<LaunchableIdentity>): Boolean =
        mutationMutex.withLock {
            val readable = mutableState.value as? FavoriteReadState.Readable ?: return false
            if (!isValidReplacement(readable.primaryIdentities, identities)) return false
            if (identities == readable.primaryIdentities) return true

            val writeSucceeded = withContext(Dispatchers.IO) {
                try {
                    writeDocument(identities, readable.companionIdentities)
                    true
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: Exception) {
                    false
                }
            }
            if (writeSucceeded) {
                mutableState.value = FavoriteReadState.Readable(
                    identities,
                    readable.companionIdentities,
                )
            }
            writeSucceeded
        }

    override suspend fun replaceComposition(
        primaryIdentities: List<LaunchableIdentity>,
        companionIdentities: List<LaunchableIdentity>,
    ): Boolean = mutationMutex.withLock {
        val readable = mutableState.value as? FavoriteReadState.Readable ?: return false
        val replacement = primaryIdentities + companionIdentities
        if (!isValidReplacement(readable.identities, replacement)) return false
        if (primaryIdentities == readable.primaryIdentities &&
            companionIdentities == readable.companionIdentities
        ) return true

        // Publish the new composition before the write so readers never fall back to the previous
        // composition while the file is being written; restore it when the write does not succeed.
        mutableState.value = FavoriteReadState.Readable(primaryIdentities, companionIdentities)
        val writeSucceeded = try {
            withContext(Dispatchers.IO) {
                try {
                    writeDocument(primaryIdentities, companionIdentities)
                    true
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: Exception) {
                    false
                }
            }
        } catch (cancellation: CancellationException) {
            mutableState.value = readable
            throw cancellation
        }
        if (!writeSucceeded) {
            mutableState.value = readable
        }
        writeSucceeded
    }

    private data class FavoriteDocument(
        val schemaVersion: Int,
        val primaryIdentities: List<LaunchableIdentity>,
        val companionIdentities: List<LaunchableIdentity>,
    )

    private fun readDocument(): FavoriteDocument =
        DataInputStream(BufferedInputStream(atomicFile.openRead())).use { input ->
            require(input.readInt() == MAGIC) { "Unrecognized favorites document" }
            val schemaVersion = input.readInt()
            require(
                schemaVersion == LEGACY_SCHEMA_VERSION || schemaVersion == SCHEMA_VERSION,
            ) { "Unsupported favorites schema" }
            val count = input.readInt()
            require(count >= 0) { "Invalid favorite count" }
            fun readIdentities(count: Int): List<LaunchableIdentity> = buildList {
                repeat(count) {
                    val serial = input.readLong()
                    val flattenedComponent = input.readUTF()
                    val component = ComponentName.unflattenFromString(flattenedComponent)
                        ?: throw IllegalArgumentException("Invalid launchable component")
                    add(LaunchableIdentity(serial, component))
                }
            }
            val primaryIdentities = readIdentities(count)
            val companionIdentities = if (schemaVersion == SCHEMA_VERSION) {
                val companionCount = input.readInt()
                require(companionCount >= 0) { "Invalid companion favorite count" }
                readIdentities(companionCount)
            } else {
                emptyList()
            }
            val identities = primaryIdentities + companionIdentities
            require(identities.distinct().size == identities.size) {
                "Duplicate favorite identity"
            }
            if (input.read() != -1) throw IllegalArgumentException("Trailing favorite data")
            FavoriteDocument(schemaVersion, primaryIdentities, companionIdentities)
        }

    private fun writeDocument(
        primaryIdentities: List<LaunchableIdentity>,
        companionIdentities: List<LaunchableIdentity>,
    ) {
        var output: FileOutputStream? = atomicFile.startWrite()
        try {
            val data = DataOutputStream(BufferedOutputStream(checkNotNull(output)))
            data.writeInt(MAGIC)
            data.writeInt(SCHEMA_VERSION)
            data.writeInt(primaryIdentities.size)
            primaryIdentities.forEach { identity ->
                data.writeLong(identity.profileSerialNumber)
                data.writeUTF(identity.componentName.flattenToString())
            }
            data.writeInt(companionIdentities.size)
            companionIdentities.forEach { identity ->
                data.writeLong(identity.profileSerialNumber)
                data.writeUTF(identity.componentName.flattenToString())
            }
            data.flush()
            atomicFile.finishWrite(output)
            output = null
        } catch (failure: Exception) {
            if (output != null) atomicFile.failWrite(output)
            throw failure
        }
    }

    private companion object {
        const val FILE_NAME = "favorites.bin"
        const val MAGIC = 0x4156454E
        const val LEGACY_SCHEMA_VERSION = 1
        const val SCHEMA_VERSION = 2
    }
}

internal fun isValidReplacement(
    current: List<LaunchableIdentity>,
    replacement: List<LaunchableIdentity>,
): Boolean = replacement.size == current.size &&
    replacement.distinct().size == replacement.size &&
    replacement.toSet() == current.toSet()

internal fun <T> List<T>.moved(fromIndex: Int, toIndex: Int): List<T> {
    if (fromIndex !in indices || toIndex !in indices || fromIndex == toIndex) return this
    return toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
}
