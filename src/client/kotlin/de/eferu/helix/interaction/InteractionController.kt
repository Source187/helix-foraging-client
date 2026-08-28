package de.eferu.helix.interaction

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos

object InteractionController {
    fun hasLineOfSight(client: Minecraft, target: BlockPos): Boolean {
        val player = client.player ?: return false
        val level = client.level ?: return false
        val start = player.eyePosition
        val end = target.center
        val hit = level.clip(
            net.minecraft.world.level.ClipContext(
                start,
                end,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                player,
            ),
        )
        return hit.blockPos == target || hit.blockPos.closerThan(target, 1.5)
    }
}
