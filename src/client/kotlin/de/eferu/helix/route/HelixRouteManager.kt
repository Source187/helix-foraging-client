package de.eferu.helix.route

import de.eferu.helix.config.ConfigManager

object HelixRouteManager {
    var route: HelixRoute? = null
        private set

    fun rebuild() {
        val config = ConfigManager.helix
        route = HelixRoute.generate(HelixConfiguration.fromConfig(config))
    }

    fun applyProfile(profileName: String) {
        val profile = ConfigManager.routeProfiles.find { it.name == profileName } ?: return
        profile.applyTo(ConfigManager.helix)
        ConfigManager.saveAll()
        rebuild()
    }

    fun currentIndex(): Int = route?.currentIndex ?: 0
    fun pointCount(): Int = route?.points?.size ?: 0
    fun progress(): Float = route?.progress ?: 0f
}
