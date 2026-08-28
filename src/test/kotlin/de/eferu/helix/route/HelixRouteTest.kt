package de.eferu.helix.route

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HelixRouteTest {
    @Test
    fun `generates ascending helix points`() {
        val route = HelixRoute.generate(
            HelixConfiguration(
                centerX = 0.0,
                centerY = 64.0,
                centerZ = 0.0,
                radius = 8.0,
                minimumRadius = 8.0,
                maximumRadius = 8.0,
                verticalStep = 0.5,
                angularStep = 0.5,
                rotationCount = 1.0,
                clockwise = true,
                startingAngle = 0.0,
                pointSpacing = 1.0,
                routeHeight = 12.0,
            ),
        )

        assertTrue(route.points.size > 3)
        assertTrue(route.points.last().y > route.points.first().y)
    }

    @Test
    fun `progress advances with completed points`() {
        val route = HelixRoute.generate(
            HelixConfiguration(
                centerX = 0.0,
                centerY = 64.0,
                centerZ = 0.0,
                radius = 5.0,
                minimumRadius = 5.0,
                maximumRadius = 5.0,
                verticalStep = 0.25,
                angularStep = 1.0,
                rotationCount = 0.5,
                clockwise = true,
                startingAngle = 0.0,
                pointSpacing = 0.5,
                routeHeight = 8.0,
            ),
        )
        val total = route.points.size
        route.advance()
        assertEquals(1f / total, route.progress, 0.001f)
    }
}
