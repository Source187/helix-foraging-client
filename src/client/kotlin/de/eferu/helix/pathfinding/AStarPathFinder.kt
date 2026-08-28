package de.eferu.helix.pathfinding

import de.eferu.helix.config.ConfigManager
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import java.util.PriorityQueue
import kotlin.math.abs

class AStarPathFinder(private val client: Minecraft) : PathFinder {
    private val directions = listOf(
        BlockPos(1, 0, 0), BlockPos(-1, 0, 0), BlockPos(0, 0, 1), BlockPos(0, 0, -1),
        BlockPos(1, 0, 1), BlockPos(1, 0, -1), BlockPos(-1, 0, 1), BlockPos(-1, 0, -1),
    )

    var lastSearchNanos: Long = 0
        private set

    override fun findPath(start: BlockPos, goal: BlockPos): Path? {
        val startTime = System.nanoTime()
        val level = client.level ?: return null
        val config = ConfigManager.pathfinding
        if (start.distManhattan(goal) > config.searchRadius) return null

        val open = PriorityQueue<PathNode>(compareBy { it.f })
        val closed = HashSet<BlockPos>()
        val bestG = HashMap<BlockPos, Double>()

        val startNode = PathNode(start, 0.0, heuristic(start, goal), null)
        open.add(startNode)
        bestG[start] = 0.0

        var expanded = 0
        while (open.isNotEmpty() && expanded < config.maxNodes) {
            val current = open.poll()
            if (current.pos == goal) {
                lastSearchNanos = System.nanoTime() - startTime
                return Path(reconstruct(current))
            }
            if (!closed.add(current.pos)) continue
            expanded++

            for (offset in directions) {
                val nextPos = current.pos.offset(offset)
                if (!isWalkable(nextPos)) continue
                val tentativeG = current.g + stepCost(offset)
                if (bestG[nextPos]?.let { tentativeG >= it } == true) continue
                bestG[nextPos] = tentativeG
                open.add(PathNode(nextPos, tentativeG, heuristic(nextPos, goal), current))
            }

            if (config.allowJumping) {
                val jumpPos = current.pos.offset(0, 1, 0)
                if (isWalkable(jumpPos) && isWalkable(jumpPos.offset(0, 1, 0)).not()) {
                    val tentativeG = current.g + 1.5
                    if (bestG[jumpPos]?.let { tentativeG >= it } != true) {
                        bestG[jumpPos] = tentativeG
                        open.add(PathNode(jumpPos, tentativeG, heuristic(jumpPos, goal), current))
                    }
                }
            }

            if (config.allowFalling) {
                val fallPos = current.pos.offset(0, -1, 0)
                if (isWalkable(fallPos)) {
                    val tentativeG = current.g + 0.8
                    if (bestG[fallPos]?.let { tentativeG >= it } != true) {
                        bestG[fallPos] = tentativeG
                        open.add(PathNode(fallPos, tentativeG, heuristic(fallPos, goal), current))
                    }
                }
            }
        }

        lastSearchNanos = System.nanoTime() - startTime
        return null
    }

    private fun reconstruct(node: PathNode): List<BlockPos> {
        val nodes = mutableListOf<BlockPos>()
        var current: PathNode? = node
        while (current != null) {
            nodes += current.pos
            current = current.parent
        }
        return smooth(nodes.reversed())
    }

    private fun smooth(path: List<BlockPos>): List<BlockPos> {
        if (path.size <= 2) return path
        val smoothed = mutableListOf(path.first())
        for (i in 1 until path.lastIndex) {
            if (i % 2 == 0) smoothed += path[i]
        }
        smoothed += path.last()
        return smoothed
    }

    private fun heuristic(a: BlockPos, b: BlockPos): Double {
        return (abs(a.x - b.x) + abs(a.y - b.y) + abs(a.z - b.z)).toDouble()
    }

    private fun stepCost(offset: BlockPos): Double {
        return if (offset.y != 0) 1.2 else if (offset.x != 0 && offset.z != 0) 1.4 else 1.0
    }

    private fun isWalkable(pos: BlockPos): Boolean {
        val level = client.level ?: return false
        val feet: BlockState = level.getBlockState(pos)
        val head: BlockState = level.getBlockState(pos.above())
        val ground: BlockState = level.getBlockState(pos.below())
        return feet.isAir && head.isAir && ground.blocksMotion()
    }
}
