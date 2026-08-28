package de.eferu.helix.pathfinding

import net.minecraft.core.BlockPos
import kotlin.math.abs

data class PathNode(
    val pos: BlockPos,
    val g: Double,
    val h: Double,
    val parent: PathNode?,
) {
    val f: Double get() = g + h
}

class Path(val nodes: List<BlockPos>) {
    val length: Int get() = nodes.size
    fun destination(): BlockPos? = nodes.lastOrNull()
}

interface PathFinder {
    fun findPath(start: BlockPos, goal: BlockPos): Path?
}
