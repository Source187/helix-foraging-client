package de.eferu.helix.route

data class RouteProfile(
    val name: String,
    val centerX: Double,
    val centerY: Double,
    val centerZ: Double,
    val radius: Double,
    val height: Double,
    val rotationCount: Double,
    val clockwise: Boolean,
    val angularStep: Double,
    val verticalStep: Double,
) {
  companion object {
    fun torrhusStyle(): RouteProfile = RouteProfile(
      name = "Torrhus Style Helix",
      centerX = 0.0,
      centerY = 64.0,
      centerZ = 0.0,
      radius = 9.5,
      height = 28.0,
      rotationCount = 2.5,
      clockwise = true,
      angularStep = 0.22,
      verticalStep = 0.32,
    )
  }

  fun applyTo(config: de.eferu.helix.config.HelixConfig) {
    config.centerX = centerX
    config.centerY = centerY
    config.centerZ = centerZ
    config.radius = radius
    config.routeHeight = height
    config.rotationCount = rotationCount
    config.clockwise = clockwise
    config.angularStep = angularStep
    config.verticalStep = verticalStep
    config.activeProfile = name
  }
}
