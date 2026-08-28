package de.eferu.helix.world

import de.eferu.helix.config.ConfigManager
import de.eferu.helix.route.HelixRouteManager
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import kotlin.math.abs

class TargetManager {
    private var lastSelected: BlockPos? = null

    fun select(client: Minecraft, candidates: List<ForagingTarget>): ForagingTarget? {
        if (candidates.isEmpty()) return null
        val player = client.player ?: return null
        val routePoint = HelixRouteManager.route?.currentPoint()
        val config = ConfigManager.scanner

        candidates.forEach { target ->
            val verticalDiff = abs(target.position.y - player.y)
            target.routeDistance = routePoint?.let {
                target.position.center.distanceTo(it.x, it.y, it.z)
            } ?: target.distance
            val freshness = (System.currentTimeMillis() - target.lastSeen).coerceAtMost(5000) / 5000.0
            target.score =
                config.distanceWeight * target.distance +
                config.routeWeight * target.routeDistance +
                config.verticalWeight * verticalDiff -
                config.freshnessWeight * freshness
            if (target.position == lastSelected) target.score += 1000.0
        }

        return candidates.minByOrNull { it.score }?.also {
            lastSelected = it.position
        }
    }

    fun clear() {
        lastSelected = null
    }
}

private fun net.minecraft.world.phys.Vec3.distanceTo(x: Double, y: Double, z: Double): Double {
    val dx = this.x - x
    val dy = this.y - y
    val dz = this.z - z
    return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
}
