package de.eferu.helix.safety

import de.eferu.helix.HelixClient
import de.eferu.helix.config.ClientConfig
import de.eferu.helix.config.ConfigManager
import de.eferu.helix.notification.NotificationManager
import net.minecraft.client.Minecraft

object EnvironmentGuard {
    private var lastDenyReason: String = "Automation disabled"

    fun isAllowed(): Boolean {
        val config = ConfigManager.client
        if (!config.automationEnabled) {
            lastDenyReason = "Automation is disabled in settings"
            return false
        }

        val client = Minecraft.getInstance()
        val serverAddress = client.currentServer?.ip ?: "singleplayer"
        val allowed = config.allowedEnvironment

        if (allowed.isBlank()) {
            lastDenyReason = "No allowed environment configured"
            return false
        }

        val matches = serverAddress.contains(allowed, ignoreCase = true) ||
            (allowed.equals("singleplayer", true) && client.isLocalServer)

        if (!matches) {
            lastDenyReason = "Environment '$serverAddress' is not in allowed list ('$allowed')"
            HelixClient.logger.warn(lastDenyReason)
            return false
        }

        return true
    }

    fun denyReason(): String = lastDenyReason

    fun requireAllowed(): Boolean {
        if (isAllowed()) return true
        NotificationManager.show("Automation blocked", denyReason())
        return false
    }
}
