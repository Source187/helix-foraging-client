package de.eferu.helix.pathfinding

import de.eferu.helix.config.ConfigManager
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import kotlin.math.atan2
import kotlin.math.sqrt

class MovementController {
    var activePath: Path? = null
        private set

    private var pathIndex = 0
    private var ticksOnNode = 0
    private var lastPosition: Vec3? = null
    private var stuckTicks = 0

    var forward = false
        private set
    var backward = false
        private set
    var left = false
        private set
    var right = false
        private set
    var jump = false
        private set

    fun setPath(path: Path?) {
        activePath = path
        pathIndex = 0
        ticksOnNode = 0
        stuckTicks = 0
        lastPosition = null
        clearInput()
    }

    fun tick(client: Minecraft): MovementResult {
        val player = client.player ?: return MovementResult.NO_PLAYER
        val path = activePath ?: return MovementResult.NO_PATH
        val nodes = path.nodes
        if (nodes.isEmpty()) return MovementResult.DONE

        val config = ConfigManager.pathfinding
        ticksOnNode++
        if (ticksOnNode > config.movementTimeoutTicks) {
            return MovementResult.TIMEOUT
        }

        val currentPos = player.position()
        lastPosition?.let { previous ->
            if (currentPos.distanceTo(previous) < 0.02) stuckTicks++ else stuckTicks = 0
        }
        lastPosition = currentPos
        if (stuckTicks > config.stuckThresholdTicks) return MovementResult.STUCK

        if (pathIndex >= nodes.lastIndex) {
            clearInput()
            return MovementResult.DONE
        }

        val targetNode = nodes[pathIndex + 1]
        val targetVec = Vec3(targetNode.x + 0.5, targetNode.y.toDouble(), targetNode.z + 0.5)
        val delta = targetVec.subtract(currentPos)
        val horizontalDistance = sqrt(delta.x * delta.x + delta.z * delta.z)

        if (horizontalDistance < 0.35 && abs(player.y - targetNode.y) < 1.5) {
            pathIndex++
            ticksOnNode = 0
            if (pathIndex >= nodes.lastIndex) {
                clearInput()
                return MovementResult.DONE
            }
        }

        val yawRad = Math.toRadians(player.yRot.toDouble())
        val forwardAxis = -delta.x * sin(yawRad) + delta.z * cos(yawRad)
        val strafeAxis = delta.x * cos(yawRad) + delta.z * sin(yawRad)

        forward = forwardAxis > 0.15
        backward = forwardAxis < -0.15
        right = strafeAxis > 0.15
        left = strafeAxis < -0.15
        jump = delta.y > 0.6 && player.onGround()

        return MovementResult.MOVING
    }

    fun clearInput() {
        forward = false
        backward = false
        left = false
        right = false
        jump = false
    }

    enum class MovementResult {
        MOVING,
        DONE,
        TIMEOUT,
        STUCK,
        NO_PATH,
        NO_PLAYER,
    }
}

private fun sin(value: Double) = kotlin.math.sin(value)
private fun cos(value: Double) = kotlin.math.cos(value)
private fun abs(value: Double) = kotlin.math.abs(value)
