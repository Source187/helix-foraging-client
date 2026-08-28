package de.eferu.helix.rotation

import de.eferu.helix.config.ConfigManager
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class RotationController {
    private var target: BlockPos? = null

    fun setTarget(pos: BlockPos?) {
        target = pos
    }

    fun tick(client: Minecraft): Boolean {
        val player = client.player ?: return false
        val pos = target ?: return true
        val config = ConfigManager.rotation

        val eye = player.eyePosition
        val blockCenter = Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
        val delta = blockCenter.subtract(eye)

        val targetYaw = Math.toDegrees(atan2(-delta.x, delta.z)).toFloat()
        val horizontal = sqrt(delta.x * delta.x + delta.z * delta.z)
        val targetPitch = Math.toDegrees(-atan2(delta.y, horizontal)).toFloat().coerceIn(-90f, 90f)

        val yawDiff = wrapDegrees(targetYaw - player.yRot)
        val pitchDiff = targetPitch - player.xRot

        if (abs(yawDiff) < config.rotationThreshold && abs(pitchDiff) < config.rotationThreshold) {
            return true
        }

        val yawStep = (yawDiff / config.rotationSpeed * config.rotationAcceleration).coerceIn(
            -config.maxYawSpeed,
            config.maxYawSpeed,
        )
        val pitchStep = (pitchDiff / config.rotationSpeed * config.rotationAcceleration).coerceIn(
            -config.maxPitchSpeed,
            config.maxPitchSpeed,
        )

        player.yRot += yawStep
        player.xRot += pitchStep
        return false
    }

    private fun wrapDegrees(value: Float): Float {
        var result = value % 360f
        if (result >= 180f) result -= 360f
        if (result < -180f) result += 360f
        return result
    }
}
