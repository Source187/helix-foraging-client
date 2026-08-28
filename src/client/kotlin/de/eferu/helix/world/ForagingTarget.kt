package de.eferu.helix.world

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

data class ForagingTarget(
    val position: BlockPos,
    var blockState: BlockState,
    var distance: Double = 0.0,
    var routeDistance: Double = 0.0,
    var reachable: Boolean = true,
    var completed: Boolean = false,
    var lastSeen: Long = System.currentTimeMillis(),
    var score: Double = Double.MAX_VALUE,
)
