package de.eferu.helix.statistics

data class SessionStatistics(
    val startedAt: Long = System.currentTimeMillis(),
    var blocksHarvested: Int = 0,
    var successfulHarvests: Int = 0,
    var failedHarvests: Int = 0,
    var recoveries: Int = 0,
    var xpGained: Double = 0.0,
    var totalTargetDistance: Double = 0.0,
    var routeName: String = "HELIX",
    var routePoint: Int = 0,
    var routePointCount: Int = 0,
) {
    fun sessionDurationMs(): Long = System.currentTimeMillis() - startedAt

    fun blocksPerHour(): Double {
        val hours = sessionDurationMs().coerceAtLeast(1) / 3_600_000.0
        return blocksHarvested / hours
    }

    fun xpPerHour(): Double {
        val hours = sessionDurationMs().coerceAtLeast(1) / 3_600_000.0
        return xpGained / hours
    }

    fun averageTargetDistance(): Double {
        if (successfulHarvests == 0) return 0.0
        return totalTargetDistance / successfulHarvests
    }
}
