package de.eferu.helix.route

import kotlin.math.cos
import kotlin.math.sin

data class HelixPoint(
    val index: Int,
    val x: Double,
    val y: Double,
    val z: Double,
    val theta: Double,
    val radius: Double,
    var completed: Boolean = false,
)

data class HelixConfiguration(
    val centerX: Double,
    val centerY: Double,
    val centerZ: Double,
    val radius: Double,
    val minimumRadius: Double,
    val maximumRadius: Double,
    val verticalStep: Double,
    val angularStep: Double,
    val rotationCount: Double,
    val clockwise: Boolean,
    val startingAngle: Double,
    val pointSpacing: Double,
    val routeHeight: Double,
) {
    companion object {
        fun fromConfig(config: de.eferu.helix.config.HelixConfig): HelixConfiguration = HelixConfiguration(
            centerX = config.centerX,
            centerY = config.centerY,
            centerZ = config.centerZ,
            radius = config.radius,
            minimumRadius = config.minimumRadius,
            maximumRadius = config.maximumRadius,
            verticalStep = config.verticalStep,
            angularStep = config.angularStep,
            rotationCount = config.rotationCount,
            clockwise = config.clockwise,
            startingAngle = config.startingAngle,
            pointSpacing = config.pointSpacing,
            routeHeight = config.routeHeight,
        )
    }
}

class HelixRoute private constructor(
    val configuration: HelixConfiguration,
    val points: List<HelixPoint>,
) {
    var currentIndex: Int = 0
        private set

    val progress: Float
        get() = if (points.isEmpty()) 0f else points.count { it.completed }.toFloat() / points.size

    fun currentPoint(): HelixPoint? = points.getOrNull(currentIndex)
    fun nextPoint(): HelixPoint? = points.getOrNull(currentIndex + 1)

    fun advance() {
        points.getOrNull(currentIndex)?.completed = true
        if (currentIndex < points.lastIndex) currentIndex++
    }

    companion object {
        fun generate(configuration: HelixConfiguration): HelixRoute {
            val totalTheta = configuration.rotationCount * 2.0 * Math.PI
            val direction = if (configuration.clockwise) 1.0 else -1.0
            val generated = mutableListOf<HelixPoint>()
            var theta = configuration.startingAngle
            var index = 0
            var lastX = Double.NaN
            var lastY = Double.NaN
            var lastZ = Double.NaN

            while (theta <= configuration.startingAngle + totalTheta + 1e-6) {
                val t = (theta - configuration.startingAngle) / totalTheta
                val radius = interpolateRadius(configuration, t)
                val x = configuration.centerX + radius * cos(theta)
                val z = configuration.centerZ + radius * sin(theta) * direction
                val y = configuration.centerY + configuration.verticalStep * (theta - configuration.startingAngle)

                if (y > configuration.centerY + configuration.routeHeight) break

                val shouldAdd = generated.isEmpty() ||
                    distance(lastX, lastY, lastZ, x, y, z) >= configuration.pointSpacing

                if (shouldAdd) {
                    generated += HelixPoint(index++, x, y, z, theta, radius)
                    lastX = x
                    lastY = y
                    lastZ = z
                }

                theta += configuration.angularStep
            }

            return HelixRoute(configuration, generated)
        }

        private fun interpolateRadius(configuration: HelixConfiguration, t: Double): Double {
            return when {
                configuration.minimumRadius == configuration.maximumRadius -> configuration.radius
                configuration.minimumRadius < configuration.maximumRadius ->
                    configuration.minimumRadius + (configuration.maximumRadius - configuration.minimumRadius) * t
                else ->
                    configuration.maximumRadius + (configuration.minimumRadius - configuration.maximumRadius) * t
            }
        }

        private fun distance(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
            val dx = x2 - x1
            val dy = y2 - y1
            val dz = z2 - z1
            return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
        }
    }
}
