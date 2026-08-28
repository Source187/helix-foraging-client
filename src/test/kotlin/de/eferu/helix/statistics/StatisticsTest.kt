package de.eferu.helix.statistics

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StatisticsTest {
    @Test
    fun `average target distance uses successful harvests`() {
        val stats = SessionStatistics()
        stats.successfulHarvests = 2
        stats.totalTargetDistance = 10.0
        assertEquals(5.0, stats.averageTargetDistance(), 0.001)
    }
}
