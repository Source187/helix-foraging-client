package de.eferu.helix.world

import de.eferu.helix.config.ConfigManager
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block

class WorldScanner {
    private var tickCounter = 0
    private val cache = LinkedHashMap<BlockPos, ForagingTarget>()
    var lastScanNanos: Long = 0
        private set

    fun tick(client: Minecraft): List<ForagingTarget> {
        tickCounter++
        if (tickCounter % ConfigManager.scanner.scanIntervalTicks != 0) {
            return cache.values.filterNot { it.completed }
        }

        val start = System.nanoTime()
        val player = client.player ?: return emptyList()
        val level = client.level ?: return emptyList()
        val origin = player.blockPosition()
        val radius = ConfigManager.scanner.scanRadius
        val vertical = ConfigManager.scanner.verticalRange
        val allowed = allowedBlocks()

        val found = mutableListOf<ForagingTarget>()
        val minX = origin.x - radius
        val maxX = origin.x + radius
        val minY = (origin.y - vertical).coerceAtLeast(level.minY)
        val maxY = (origin.y + vertical).coerceAtMost(level.maxY)
        val minZ = origin.z - radius
        val maxZ = origin.z + radius

        var scanned = 0
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    if (scanned++ > radius * radius * vertical) break
                    val pos = BlockPos(x, y, z)
                    val state = level.getBlockState(pos)
                    if (!allowed.contains(state.block)) continue
                    val target = cache.getOrPut(pos) {
                        ForagingTarget(pos, state)
                    }
                    target.blockState = state
                    target.lastSeen = System.currentTimeMillis()
                    target.distance = player.position().distanceTo(pos.center)
                    found += target
                    if (found.size >= ConfigManager.scanner.maxTargets) break
                }
            }
        }

        cache.entries.removeIf { (_, target) ->
            System.currentTimeMillis() - target.lastSeen > 10_000
        }

        lastScanNanos = System.nanoTime() - start
        return found.filterNot { it.completed }
    }

    fun markCompleted(pos: BlockPos) {
        cache[pos]?.completed = true
    }

    fun clear() = cache.clear()

    private fun allowedBlocks(): Set<Block> {
        return ConfigManager.scanner.allowedBlockTypes.mapNotNull { id ->
            BuiltInRegistries.BLOCK.getOptional(Identifier.parse(id)).orElse(null)
        }.toSet()
    }
}
