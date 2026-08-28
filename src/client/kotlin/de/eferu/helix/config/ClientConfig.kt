package de.eferu.helix.config

data class ClientConfig(
    var allowedEnvironment: String = "singleplayer",
    var automationEnabled: Boolean = false,
    var themePrimary: Int = 0xFF9B59FF.toInt(),
    var themeSecondary: Int = 0xFFE056FD.toInt(),
    var themeBackground: Int = 0xAA101018.toInt(),
    var themeText: Int = 0xFFECECF4.toInt(),
    var themeSuccess: Int = 0xFF4ADE80.toInt(),
    var themeWarning: Int = 0xFFFACC15.toInt(),
    var themeError: Int = 0xFFF87171.toInt(),
)
