package de.eferu.helix.config

data class PathfindingConfig(
    var searchRadius: Int = 32,
    var maxNodes: Int = 8000,
    var allowJumping: Boolean = true,
    var allowFalling: Boolean = true,
    var movementTimeoutTicks: Int = 200,
    var stuckThresholdTicks: Int = 40,
)

data class RotationConfig(
    var rotationSpeed: Float = 8f,
    var rotationAcceleration: Float = 1.2f,
    var maxYawSpeed: Float = 12f,
    var maxPitchSpeed: Float = 10f,
    var rotationThreshold: Float = 2.5f,
)

data class StatisticsConfig(
    var persistSessions: Boolean = true,
    var maxHistoryEntries: Int = 50,
)

data class ScannerConfig(
    var scanRadius: Int = 24,
    var verticalRange: Int = 16,
    var scanIntervalTicks: Int = 10,
    var maxTargets: Int = 128,
    var allowedBlockTypes: List<String> = defaultWoodBlocks(),
    var distanceWeight: Double = 1.0,
    var routeWeight: Double = 0.6,
    var verticalWeight: Double = 0.4,
    var freshnessWeight: Double = 0.2,
) {
    companion object {
        fun defaultWoodBlocks(): List<String> = listOf(
            "minecraft:oak_log",
            "minecraft:spruce_log",
            "minecraft:birch_log",
            "minecraft:jungle_log",
            "minecraft:acacia_log",
            "minecraft:dark_oak_log",
            "minecraft:mangrove_log",
            "minecraft:cherry_log",
        )
    }
}

data class TimingConfig(
    var seed: Long = 12345L,
    var minimumIntervalMs: Long = 80,
    var maximumIntervalMs: Long = 180,
    var averageIntervalMs: Long = 120,
    var variance: Double = 0.25,
    var occasionalPauseProbability: Double = 0.05,
    var pauseDurationMinMs: Long = 300,
    var pauseDurationMaxMs: Long = 900,
)
