package de.eferu.helix.interaction

import de.eferu.helix.statistics.StatisticsManager
import de.eferu.helix.world.ForagingTarget
import de.eferu.helix.world.WorldScanner
import net.minecraft.client.Minecraft
import net.minecraft.world.level.block.state.BlockState

class HarvestController(
    private val scanner: WorldScanner,
) {
    private var breakingTicks = 0
    private var lastState: BlockState? = null

    fun begin(target: ForagingTarget, client: Minecraft): Boolean {
        val level = client.level ?: return false
        val player = client.player ?: return false
        val state = level.getBlockState(target.position)
        if (state.isAir) return false
        lastState = state
        breakingTicks = 0
        client.gameMode?.startDestroyBlock(target.position, net.minecraft.core.Direction.UP)
        return true
    }

    fun tick(target: ForagingTarget, client: Minecraft): HarvestResult {
        val level = client.level ?: return HarvestResult.FAILED
        val state = level.getBlockState(target.position)
        breakingTicks++

        if (state.isAir || state != lastState) {
            scanner.markCompleted(target.position)
            StatisticsManager.recordHarvest(success = true, distance = target.distance)
            lastState = null
            return HarvestResult.COMPLETE
        }

        client.gameMode?.continueDestroyBlock(target.position, net.minecraft.core.Direction.UP)
        if (breakingTicks > 200) {
            StatisticsManager.recordHarvest(success = false, distance = target.distance)
            return HarvestResult.FAILED
        }
        return HarvestResult.WORKING
    }

    fun stop(client: Minecraft) {
        client.gameMode?.stopDestroyBlock()
        lastState = null
        breakingTicks = 0
    }

    enum class HarvestResult {
        WORKING,
        COMPLETE,
        FAILED,
    }
}
