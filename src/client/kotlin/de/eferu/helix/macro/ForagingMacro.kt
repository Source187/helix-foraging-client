package de.eferu.helix.macro

import de.eferu.helix.HelixClient
import de.eferu.helix.core.StateMachine
import de.eferu.helix.interaction.HarvestController
import de.eferu.helix.interaction.InteractionController
import de.eferu.helix.notification.NotificationManager
import de.eferu.helix.pathfinding.AStarPathFinder
import de.eferu.helix.pathfinding.MovementController
import de.eferu.helix.recovery.RecoveryManager
import de.eferu.helix.recovery.RecoveryReason
import de.eferu.helix.route.HelixRouteManager
import de.eferu.helix.rotation.RotationController
import de.eferu.helix.safety.EnvironmentGuard
import de.eferu.helix.statistics.StatisticsManager
import de.eferu.helix.timing.InputTimingSimulator
import de.eferu.helix.world.ForagingTarget
import de.eferu.helix.world.TargetManager
import de.eferu.helix.world.WorldScanner
import net.minecraft.client.Minecraft

class ForagingMacro(
    private val scanner: WorldScanner,
    private val targetManager: TargetManager,
) : StateMachine<MacroState>("ForagingMacro", MacroState.IDLE) {
    private val pathFinder = AStarPathFinder(Minecraft.getInstance())
    private val movement = MovementController()
    private val rotation = RotationController()
    private val harvest = HarvestController(scanner)
    private val recovery = RecoveryManager()
    private val timing = InputTimingSimulator()

    private var candidates: List<ForagingTarget> = emptyList()
    private var activeTarget: ForagingTarget? = null
    private var nextActionAt = 0L

    var running = false
        private set
    var paused = false
        private set

    fun start() {
        if (!EnvironmentGuard.requireAllowed()) return
        if (HelixRouteManager.route == null) HelixRouteManager.rebuild()
        running = true
        paused = false
        recovery.reset()
        StatisticsManager.startSession(
            HelixRouteManager.route?.configuration?.let { "HELIX" } ?: "HELIX",
            HelixRouteManager.pointCount(),
        )
        NotificationManager.show("Helix started")
        transition(MacroState.SCANNING)
    }

    fun pause() {
        paused = true
        transition(MacroState.PAUSED)
        movement.clearInput()
        NotificationManager.show("Automation paused")
    }

    fun resume() {
        if (!running) return
        paused = false
        transition(MacroState.SCANNING)
    }

    fun stop() {
        running = false
        paused = false
        movement.setPath(null)
        harvest.stop(Minecraft.getInstance())
        activeTarget = null
        transition(MacroState.STOPPED)
        StatisticsManager.endSession()
        NotificationManager.show("Automation stopped")
    }

    fun emergencyStop() = stop()

    fun tick(client: Minecraft) {
        if (!running || paused) return
        if (System.currentTimeMillis() < nextActionAt) return
        nextActionAt = System.currentTimeMillis() + timing.nextDelayMs()
        tick()
    }

    override fun enter(state: MacroState) {
        when (state) {
            MacroState.RECOVERY -> {
                movement.setPath(null)
                harvest.stop(Minecraft.getInstance())
                activeTarget = null
            }
            MacroState.STOPPED -> movement.clearInput()
            else -> Unit
        }
    }

    override fun update(state: MacroState) {
        val client = Minecraft.getInstance()
        when (state) {
            MacroState.SCANNING -> {
                candidates = scanner.tick(client)
                transition(if (candidates.isEmpty()) MacroState.RECOVERY else MacroState.SELECTING_TARGET)
            }
            MacroState.SELECTING_TARGET -> {
                activeTarget = targetManager.select(client, candidates)
                transition(if (activeTarget == null) MacroState.RECOVERY else MacroState.CALCULATING_ROUTE)
            }
            MacroState.CALCULATING_ROUTE -> {
                val target = activeTarget ?: return transition(MacroState.RECOVERY)
                val player = client.player ?: return
                val path = pathFinder.findPath(player.blockPosition(), target.position)
                if (path == null) {
                    transition(recovery.trigger(RecoveryReason.PATH_INVALID))
                } else {
                    movement.setPath(path)
                    transition(MacroState.MOVING)
                }
            }
            MacroState.MOVING -> when (movement.tick(client)) {
                MovementController.MovementResult.DONE -> transition(MacroState.ROTATING)
                MovementController.MovementResult.STUCK,
                MovementController.MovementResult.TIMEOUT,
                -> transition(recovery.trigger(RecoveryReason.PLAYER_STUCK))
                MovementController.MovementResult.NO_PATH -> transition(recovery.trigger(RecoveryReason.PATH_INVALID))
                else -> Unit
            }
            MacroState.ROTATING -> {
                val target = activeTarget ?: return transition(MacroState.RECOVERY)
                rotation.setTarget(target.position)
                if (rotation.tick(client)) {
                    transition(MacroState.HARVESTING)
                }
            }
            MacroState.HARVESTING -> {
                val target = activeTarget ?: return transition(MacroState.RECOVERY)
                if (!InteractionController.hasLineOfSight(client, target.position)) {
                    transition(recovery.trigger(RecoveryReason.COLLISION))
                    return
                }
                if (harvest.begin(target, client)) {
                    transition(MacroState.VERIFYING)
                } else {
                    transition(recovery.trigger(RecoveryReason.HARVEST_FAILED))
                }
            }
            MacroState.VERIFYING -> {
                val target = activeTarget ?: return transition(MacroState.RECOVERY)
                when (harvest.tick(target, client)) {
                    HarvestController.HarvestResult.COMPLETE -> {
                        NotificationManager.show("Target acquired")
                        transition(MacroState.ADVANCING_ROUTE)
                    }
                    HarvestController.HarvestResult.FAILED -> transition(recovery.trigger(RecoveryReason.HARVEST_FAILED))
                    HarvestController.HarvestResult.WORKING -> Unit
                }
            }
            MacroState.ADVANCING_ROUTE -> {
                HelixRouteManager.route?.advance()
                StatisticsManager.updateRoute(HelixRouteManager.currentIndex(), HelixRouteManager.pointCount())
                activeTarget = null
                transition(MacroState.SCANNING)
            }
            MacroState.RECOVERY -> {
                scanner.clear()
                targetManager.clear()
                recovery.reset()
                transition(MacroState.SCANNING)
            }
            MacroState.PAUSED, MacroState.STOPPED, MacroState.IDLE -> Unit
        }
    }

    override fun exit(state: MacroState) = Unit

    fun movementController(): MovementController = movement
}
