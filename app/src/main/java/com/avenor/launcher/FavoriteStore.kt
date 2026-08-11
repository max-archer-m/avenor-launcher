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
    data class Readable(val identities: List<LaunchableIdentity>) : FavoriteReadState
    data object ReadFailure : FavoriteReadState
}

internal interface FavoriteStore {
    val state: StateFlow<FavoriteReadState>
    suspend fun load()
    suspend fun add(identity: LaunchableIdentity): Boolean
}

internal class AtomicFileFavoriteStore(context: Context) : FavoriteStore {
    private val atomicFile = AtomicFile(context.filesDir.resolve(FILE_NAME))
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
                    FavoriteReadState.Readable(readDocument())
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

        val updated = readable.identities + identity
        val writeSucceeded = withContext(Dispatchers.IO) {
            try {
                writeDocument(updated)
                true
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                false
            }
        }
        if (writeSucceeded) mutableState.value = FavoriteReadState.Readable(updated)
        writeSucceeded
    }

    private fun readDocument(): List<LaunchableIdentity> =
        DataInputStream(BufferedInputStream(atomicFile.openRead())).use { input ->
            require(input.readInt() == MAGIC) { "Unrecognized favorites document" }
            require(input.readInt() == SCHEMA_VERSION) { "Unsupported favorites schema" }
            val count = input.readInt()
            require(count in 0..MAX_FAVORITES) { "Invalid favorite count" }
            val identities = buildList(count) {
                repeat(count) {
                    val serial = input.readLong()
                    val flattenedComponent = input.readUTF()
                    val component = ComponentName.unflattenFromString(flattenedComponent)
                        ?: throw IllegalArgumentException("Invalid launchable component")
                    add(LaunchableIdentity(serial, component))
                }
            }
            require(identities.distinct().size == identities.size) {
                "Duplicate favorite identity"
            }
            if (input.read() != -1) throw IllegalArgumentException("Trailing favorite data")
            identities
        }

    private fun writeDocument(identities: List<LaunchableIdentity>) {
        var output: FileOutputStream? = atomicFile.startWrite()
        try {
            val data = DataOutputStream(BufferedOutputStream(checkNotNull(output)))
            data.writeInt(MAGIC)
            data.writeInt(SCHEMA_VERSION)
            data.writeInt(identities.size)
            identities.forEach { identity ->
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
        const val SCHEMA_VERSION = 1
        const val MAX_FAVORITES = 10_000
    }
}
