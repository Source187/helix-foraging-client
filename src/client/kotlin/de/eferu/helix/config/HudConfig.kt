package de.eferu.helix.config

data class HudConfig(
    var leftPanelX: Float = 12f,
    var leftPanelY: Float = 40f,
    var leftPanelScale: Float = 1f,
    var leftPanelOpacity: Float = 0.85f,
    var centerPanelOpacity: Float = 0.9f,
    var rightPanelVisible: Boolean = true,
    var rightPanelX: Float = -220f,
    var rightPanelY: Float = 40f,
    var bottomPanelVisible: Boolean = true,
    var fontSize: Float = 1f,
    var borderOpacity: Float = 0.6f,
    var backgroundOpacity: Float = 0.75f,
)
