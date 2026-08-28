package de.eferu.helix.timing

import de.eferu.helix.config.ConfigManager
import kotlin.random.Random

class InputTimingSimulator {
    private val random: Random = Random(ConfigManager.timing.seed)

    fun nextDelayMs(): Long {
        val config = ConfigManager.timing
        if (random.nextDouble() < config.occasionalPauseProbability) {
            return random.nextLong(config.pauseDurationMinMs, config.pauseDurationMaxMs + 1)
        }
        val spread = ((config.maximumIntervalMs - config.minimumIntervalMs) * config.variance).toLong()
        val center = config.averageIntervalMs
        return (center + random.nextLong(-spread, spread + 1)).coerceIn(config.minimumIntervalMs, config.maximumIntervalMs)
    }
}
