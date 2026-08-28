package de.eferu.helix.recovery

import de.eferu.helix.macro.MacroState
import de.eferu.helix.notification.NotificationManager
import de.eferu.helix.statistics.StatisticsManager

class RecoveryManager {
    private var failureCount = 0
    var lastReason: RecoveryReason? = null
        private set

    fun trigger(reason: RecoveryReason): MacroState {
        failureCount++
        lastReason = reason
        StatisticsManager.recordRecovery()
        NotificationManager.show("Recovery triggered", reason.name)
        return if (failureCount >= 5) MacroState.STOPPED else MacroState.RECOVERY
    }

    fun reset() {
        failureCount = 0
        lastReason = null
    }
}
