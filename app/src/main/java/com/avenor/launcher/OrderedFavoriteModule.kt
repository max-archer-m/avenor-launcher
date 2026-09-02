package com.avenor.launcher

import android.content.ComponentName
import android.content.Context
import android.util.AtomicFile
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal enum class OrderedFavoriteModuleType {
    Vertical,
    Ribbon,
}

internal data class OrderedFavoriteModule(
    val id: String,
    val type: OrderedFavoriteModuleType,
    val identities: List<LaunchableIdentity>,
    val applicationSize: FavoriteListSize = FavoriteListSize.Medium,
    val namePlacement: FavoriteNamePlacement = FavoriteNamePlacement.Right,
    val itemsPerRow: Int = 1,
) {
    init {
        require(id.isNotBlank())
        require(identities.isNotEmpty())
    }
}

internal data class OrderedFavoriteAggregate(
    val modules: List<OrderedFavoriteModule> = emptyList(),
) {
    val identities: List<LaunchableIdentity>
        get() = modules.flatMap(OrderedFavoriteModule::identities)
}

internal sealed interface OrderedFavoriteReadState {
    data object Loading : OrderedFavoriteReadState
    data class Readable(
        val aggregate: OrderedFavoriteAggregate,
    ) : OrderedFavoriteReadState
    data object ReadFailure : OrderedFavoriteReadState
}

internal class OrderedFavoriteModuleStore private constructor(
    private val atomicFile: AtomicFile,
    private val legacyAtomicFile: AtomicFile?,
) {
    constructor(context: Context) : this(
        atomicFile = AtomicFile(context.filesDir.resolve(FILE_NAME)),
        legacyAtomicFile = AtomicFile(context.filesDir.resolve(LEGACY_FILE_NAME)),
    )

    internal constructor(file: File, legacyFile: File? = null) : this(
        atomicFile = AtomicFile(file),
        legacyAtomicFile = legacyFile?.let(::AtomicFile),
    )

    private val mutationMutex = Mutex()
    private val mutableState =
        MutableStateFlow<OrderedFavoriteReadState>(OrderedFavoriteReadState.Loading)

    val state: StateFlow<OrderedFavoriteReadState> = mutableState

    internal suspend fun replaceAggregate(aggregate: OrderedFavoriteAggregate): Boolean =
        mutationMutex.withLock {
            if (!isValidOrderedFavoriteAggregate(aggregate)) return false
            val current = (mutableState.value as? OrderedFavoriteReadState.Readable)
                ?.aggregate
                ?: return false
            if (current == aggregate) return true
            val succeeded = try {
                withContext(Dispatchers.IO) {
                    writeDocument(aggregate)
                    true
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                false
            }
            if (succeeded) mutableState.value = OrderedFavoriteReadState.Readable(aggregate)
            succeeded
        }

    suspend fun load() = mutationMutex.withLock {
        mutableState.value = OrderedFavoriteReadState.Loading
        mutableState.value = withContext(Dispatchers.IO) {
            if (atomicFile.hasReadableSource()) {
                try {
                    OrderedFavoriteReadState.Readable(readDocument())
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: Exception) {
                    OrderedFavoriteReadState.ReadFailure
                }
            } else {
                adoptLegacyFavorites()
            }
        }
    }

    private suspend fun adoptLegacyFavorites(): OrderedFavoriteReadState {
        val legacy = legacyAtomicFile
        if (legacy != null && legacy.hasReadableSource()) {
            return try {
                require(isReadableLegacyDocument(legacy))
                writeDocument(OrderedFavoriteAggregate())
                OrderedFavoriteReadState.Readable(OrderedFavoriteAggregate())
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                OrderedFavoriteReadState.ReadFailure
            }
        }
        return try {
            writeDocument(OrderedFavoriteAggregate())
            OrderedFavoriteReadState.Readable(OrderedFavoriteAggregate())
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            OrderedFavoriteReadState.ReadFailure
        }
    }

    private fun isReadableLegacyDocument(file: AtomicFile): Boolean =
        DataInputStream(BufferedInputStream(file.openRead())).use { input ->
            require(input.readInt() == LEGACY_MAGIC)
            fun readIdentities(count: Int): List<LaunchableIdentity> = buildList {
                require(count >= 0)
                repeat(count) { add(readLegacyIdentity(input)) }
            }
            val aggregate = when (val schemaVersion = input.readInt()) {
                LEGACY_SCHEMA_VERSION -> {
                    FavoriteAggregate(
                        verticalLists = legacyAdoptionLists(
                            primary = readIdentities(input.readInt()),
                            companion = emptyList(),
                        ),
                    )
                }
                LEGACY_COMPOSITION_SCHEMA_VERSION -> {
                    FavoriteAggregate(
                        verticalLists = legacyAdoptionLists(
                            primary = readIdentities(input.readInt()),
                            companion = readIdentities(input.readInt()),
                        ),
                    )
                }
                LEGACY_AGGREGATE_SCHEMA_VERSION -> {
                    val containerCount = input.readInt()
                    require(containerCount >= 0)
                    buildList {
                        repeat(containerCount) {
                            val id = input.readUTF()
                            val type = FavoriteContainerType.values().getOrNull(input.readInt())
                                ?: error("Invalid legacy favorite container type")
                            val listSize = FavoriteListSize.values().getOrNull(input.readInt())
                                ?: error("Invalid legacy favorite list size")
                            val identities = readIdentities(input.readInt())
                            require(identities.isNotEmpty())
                            add(FavoriteContainer(id, type, identities, listSize))
                        }
                    }.let { containers ->
                        FavoriteAggregate(
                            verticalLists = containers.filter {
                                it.type == FavoriteContainerType.VerticalList
                            },
                            favoriteBars = containers.filter {
                                it.type == FavoriteContainerType.FavoriteBar
                            },
                        )
                    }
                }
                else -> error("Unsupported legacy favorites schema $schemaVersion")
            }
            require(input.read() == -1)
            require(isValidAggregate(aggregate))
            true
        }

    private fun AtomicFile.hasReadableSource(): Boolean =
        baseFile.exists() || File(baseFile.path + ".bak").exists()

    private fun readLegacyIdentity(input: DataInputStream): LaunchableIdentity {
        val serial = input.readLong()
        val component = ComponentName.unflattenFromString(input.readUTF())
            ?: error("Invalid legacy launchable component")
        return LaunchableIdentity(serial, component)
    }

    private fun readDocument(): OrderedFavoriteAggregate =
        DataInputStream(BufferedInputStream(atomicFile.openRead())).use { input ->
            require(input.readInt() == MAGIC) { "Unrecognized ordered favorites document" }
            val schemaVersion = input.readInt()
            require(schemaVersion in MIN_READABLE_SCHEMA_VERSION..SCHEMA_VERSION) {
                "Unsupported ordered favorites schema"
            }
            val moduleCount = input.readInt()
            require(moduleCount >= 0) { "Invalid ordered favorite module count" }
            val modules = buildList {
                repeat(moduleCount) {
                    val id = input.readUTF()
                    val type = OrderedFavoriteModuleType.values().getOrNull(input.readInt())
                        ?: throw IllegalArgumentException("Invalid ordered favorite module type")
                    val applicationSize = if (schemaVersion >= STYLE_SCHEMA_VERSION) {
                        FavoriteListSize.values().getOrNull(input.readInt())
                            ?: throw IllegalArgumentException("Invalid application size")
                    } else {
                        FavoriteListSize.Medium
                    }
                    val namePlacement = if (schemaVersion >= STYLE_SCHEMA_VERSION) {
                        FavoriteNamePlacement.values().getOrNull(input.readInt())
                            ?: throw IllegalArgumentException("Invalid name placement")
                    } else {
                        FavoriteNamePlacement.Right
                    }
                    val itemsPerRow = if (schemaVersion >= STYLE_SCHEMA_VERSION) {
                        input.readInt()
                    } else {
                        1
                    }
                    val identityCount = input.readInt()
                    require(identityCount > 0) { "Empty ordered favorite module" }
                    add(
                        OrderedFavoriteModule(
                            id = id,
                            type = type,
                            applicationSize = applicationSize,
                            namePlacement = namePlacement,
                            itemsPerRow = itemsPerRow,
                            identities = buildList {
                                repeat(identityCount) {
                                    val serial = input.readLong()
                                    val flattenedComponent = input.readUTF()
                                    val component =
                                        ComponentName.unflattenFromString(flattenedComponent)
                                            ?: throw IllegalArgumentException(
                                                "Invalid launchable component",
                                            )
                                    add(LaunchableIdentity(serial, component))
                                }
                            },
                        ),
                    )
                }
            }
            require(input.read() == -1) { "Trailing ordered favorite data" }
            val aggregate = OrderedFavoriteAggregate(modules)
            require(isValidOrderedFavoriteAggregate(aggregate))
            aggregate
        }

    private fun writeDocument(aggregate: OrderedFavoriteAggregate) {
        require(isValidOrderedFavoriteAggregate(aggregate))
        var output: FileOutputStream? = atomicFile.startWrite()
        try {
            val data = DataOutputStream(BufferedOutputStream(checkNotNull(output)))
            data.writeInt(MAGIC)
            data.writeInt(SCHEMA_VERSION)
            data.writeInt(aggregate.modules.size)
            aggregate.modules.forEach { module ->
                data.writeUTF(module.id)
                data.writeInt(module.type.ordinal)
                data.writeInt(module.applicationSize.ordinal)
                data.writeInt(module.namePlacement.ordinal)
                data.writeInt(module.itemsPerRow)
                data.writeInt(module.identities.size)
                module.identities.forEach { identity ->
                    data.writeLong(identity.profileSerialNumber)
                    data.writeUTF(identity.componentName.flattenToString())
                }
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
        const val FILE_NAME = "ordered_favorite_modules.bin"
        const val LEGACY_FILE_NAME = "favorites.bin"
        const val MAGIC = 0x41464D31
        const val MIN_READABLE_SCHEMA_VERSION = 1
        const val STYLE_SCHEMA_VERSION = 2
        const val SCHEMA_VERSION = STYLE_SCHEMA_VERSION
        const val LEGACY_MAGIC = 0x4156454E
        const val LEGACY_SCHEMA_VERSION = 1
        const val LEGACY_COMPOSITION_SCHEMA_VERSION = 2
        const val LEGACY_AGGREGATE_SCHEMA_VERSION = 3
    }
}

internal class OrderedFavoriteStoreAdapter private constructor(
    private val store: OrderedFavoriteModuleStore,
) : FavoriteStore {
    constructor(context: Context) : this(OrderedFavoriteModuleStore(context))
    internal constructor(file: File, legacyFile: File? = null) : this(
        OrderedFavoriteModuleStore(file, legacyFile),
    )

    private val mutationMutex = Mutex()
    private val mutableState = MutableStateFlow<FavoriteReadState>(FavoriteReadState.Loading)

    override val state: StateFlow<FavoriteReadState> = mutableState

    override suspend fun load() = mutationMutex.withLock {
        mutableState.value = FavoriteReadState.Loading
        store.load()
        publish()
    }

    override suspend fun add(identity: LaunchableIdentity): Boolean =
        mutationMutex.withLock {
            update { aggregate ->
                val modules = aggregate.modules.toMutableList()
                val first = modules.firstOrNull()
                if (identity in aggregate.identities) {
                    aggregate
                } else if (first?.type == OrderedFavoriteModuleType.Vertical) {
                    modules[0] = first.copy(identities = first.identities + identity)
                    OrderedFavoriteAggregate(modules)
                } else {
                    OrderedFavoriteAggregate(
                        modules +
                            OrderedFavoriteModule(
                                id = nextOrderedModuleId("vertical-list", modules),
                                type = OrderedFavoriteModuleType.Vertical,
                                identities = listOf(identity),
                            ),
                    )
                }
            } != null
        }

    override suspend fun remove(identity: LaunchableIdentity): Boolean =
        mutationMutex.withLock {
            update { aggregate ->
                OrderedFavoriteAggregate(
                    aggregate.modules.mapNotNull { module ->
                        module.copy(identities = module.identities - identity)
                            .takeIf { it.identities.isNotEmpty() }
                    },
                )
            } != null
        }

    override suspend fun removeAll(identities: Set<LaunchableIdentity>): Boolean =
        mutationMutex.withLock {
            update { aggregate ->
                OrderedFavoriteAggregate(
                    aggregate.modules.mapNotNull { module ->
                        module.copy(identities = module.identities.filterNot(identities::contains))
                            .takeIf { it.identities.isNotEmpty() }
                    },
                )
            } != null
        }

    override suspend fun replaceOrder(identities: List<LaunchableIdentity>): Boolean =
        updateAggregate { aggregate ->
            aggregate.replaceVerticalList(
                aggregate.verticalLists.firstOrNull()?.id ?: PRIMARY_LIST_ID,
                identities,
            )
        } != null

    override suspend fun replaceComposition(
        primaryIdentities: List<LaunchableIdentity>,
        companionIdentities: List<LaunchableIdentity>,
    ): Boolean = updateAggregate { aggregate ->
        aggregate.replaceVerticalComposition(primaryIdentities, companionIdentities)
    } != null

    override suspend fun replaceAggregate(aggregate: FavoriteAggregate): Boolean =
        mutationMutex.withLock {
            store.replaceAggregate(
                currentOrderedAggregate().replaceWithLegacyAggregate(aggregate),
            ).also {
                if (it) publish()
            }
        }

    override suspend fun updateAggregate(
        transform: (FavoriteAggregate) -> FavoriteAggregate,
    ): FavoriteAggregate? = mutationMutex.withLock {
        val current = (mutableState.value as? FavoriteReadState.Readable)
            ?: return@withLock null
        val updated = transform(current.aggregate)
        if (!store.replaceAggregate(
                currentOrderedAggregate().replaceWithLegacyAggregate(updated),
            )
        ) {
            return@withLock null
        }
        publish()
        updated
    }

    private suspend fun update(
        transform: (OrderedFavoriteAggregate) -> OrderedFavoriteAggregate,
    ): OrderedFavoriteAggregate? {
        val current = (store.state.value as? OrderedFavoriteReadState.Readable)
            ?: return null
        val updated = transform(current.aggregate)
        if (!store.replaceAggregate(updated)) return null
        publish()
        return updated
    }

    private fun currentOrderedAggregate(): OrderedFavoriteAggregate =
        (store.state.value as? OrderedFavoriteReadState.Readable)?.aggregate
            ?: OrderedFavoriteAggregate()

    private fun publish() {
        mutableState.value = when (val current = store.state.value) {
            OrderedFavoriteReadState.Loading -> FavoriteReadState.Loading
            OrderedFavoriteReadState.ReadFailure -> FavoriteReadState.ReadFailure
            is OrderedFavoriteReadState.Readable ->
                FavoriteReadState.Readable(
                    aggregate = current.aggregate.toLegacyAggregate(),
                    orderedModules = current.aggregate.modules,
                )
        }
    }
}

private fun OrderedFavoriteAggregate.toLegacyAggregate(): FavoriteAggregate {
    val vertical = modules.filter { it.type == OrderedFavoriteModuleType.Vertical }
        .map { module ->
            FavoriteContainer(
                id = module.id,
                type = FavoriteContainerType.VerticalList,
                identities = module.identities,
                listSize = module.applicationSize,
                namePlacement = module.namePlacement,
                itemsPerRow = module.itemsPerRow,
            )
        }
    val bars = modules.filter { it.type == OrderedFavoriteModuleType.Ribbon }
        .map { module ->
            FavoriteContainer(
                id = module.id,
                type = FavoriteContainerType.FavoriteBar,
                identities = module.identities,
            )
        }
    return FavoriteAggregate(verticalLists = vertical, favoriteBars = bars)
}

private fun OrderedFavoriteAggregate.replaceWithLegacyAggregate(
    aggregate: FavoriteAggregate,
): OrderedFavoriteAggregate {
    val containersById = (aggregate.verticalLists + aggregate.favoriteBars)
        .associateBy(FavoriteContainer::id)
    val updatedModules = modules.mapNotNull { module ->
        containersById[module.id]
            ?.takeIf { it.identities.isNotEmpty() }
            ?.let { container ->
            val type = container.type.toOrderedFavoriteModuleType()
            if (module.type == type &&
                module.identities == container.identities &&
                module.applicationSize == container.listSize &&
                module.namePlacement == container.namePlacement &&
                module.itemsPerRow == container.itemsPerRow
            ) {
                module
            } else {
                module.copy(
                    type = type,
                    identities = container.identities,
                    applicationSize = container.listSize,
                    namePlacement = container.namePlacement,
                    itemsPerRow = container.itemsPerRow,
                )
            }
        }
    }
    val existingIds = updatedModules.map(OrderedFavoriteModule::id).toSet()
    val appendedModules = (aggregate.verticalLists + aggregate.favoriteBars)
        .asSequence()
        .filterNot { it.id in existingIds }
        .filter { it.identities.isNotEmpty() }
        .map { container ->
            OrderedFavoriteModule(
                id = container.id,
                type = container.type.toOrderedFavoriteModuleType(),
                identities = container.identities,
                applicationSize = container.listSize,
                namePlacement = container.namePlacement,
                itemsPerRow = container.itemsPerRow,
            )
        }
        .toList()
    return OrderedFavoriteAggregate(updatedModules + appendedModules)
}

private fun FavoriteContainerType.toOrderedFavoriteModuleType(): OrderedFavoriteModuleType =
    when (this) {
        FavoriteContainerType.VerticalList -> OrderedFavoriteModuleType.Vertical
        FavoriteContainerType.FavoriteBar -> OrderedFavoriteModuleType.Ribbon
    }

internal fun isValidOrderedFavoriteAggregate(
    aggregate: OrderedFavoriteAggregate,
): Boolean {
    val modules = aggregate.modules
    if (modules.map(OrderedFavoriteModule::id).distinct().size != modules.size) return false
    val identities = modules.flatMap(OrderedFavoriteModule::identities)
    return identities.distinct().size == identities.size && modules.all { module ->
        module.id.isNotBlank() &&
            module.identities.isNotEmpty() &&
            when (module.type) {
                OrderedFavoriteModuleType.Vertical ->
                    module.itemsPerRow in when (module.namePlacement) {
                        FavoriteNamePlacement.Right -> 1..2
                        FavoriteNamePlacement.Below -> 1..4
                    }
                OrderedFavoriteModuleType.Ribbon ->
                    module.applicationSize == FavoriteListSize.Medium &&
                        module.namePlacement == FavoriteNamePlacement.Right &&
                        module.itemsPerRow == 1
            }
    }
}

private fun legacyAdoptionLists(
    primary: List<LaunchableIdentity>,
    companion: List<LaunchableIdentity>,
): List<FavoriteContainer> = buildList {
    if (primary.isNotEmpty()) {
        add(FavoriteContainer(PRIMARY_LIST_ID, FavoriteContainerType.VerticalList, primary))
    }
    if (companion.isNotEmpty()) {
        add(
            FavoriteContainer(
                "vertical-list-2",
                FavoriteContainerType.VerticalList,
                companion,
            ),
        )
    }
}

private fun nextOrderedModuleId(
    prefix: String,
    modules: List<OrderedFavoriteModule>,
): String {
    val existingIds = modules.mapTo(mutableSetOf(), OrderedFavoriteModule::id)
    var suffix = 1
    while ("$prefix-$suffix" in existingIds) suffix += 1
    return "$prefix-$suffix"
}
