package de.eferu.helix

import de.eferu.helix.config.ConfigManager
import de.eferu.helix.core.ClientContext
import de.eferu.helix.core.ModuleManager
import de.eferu.helix.debug.DebugOverlay
import de.eferu.helix.event.HelixEvents
import de.eferu.helix.gui.ClickGuiScreen
import de.eferu.helix.hud.HudManager
import de.eferu.helix.input.KeybindManager
import de.eferu.helix.macro.MacroManager
import de.eferu.helix.notification.NotificationManager
import de.eferu.helix.statistics.StatisticsManager
import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory

object HelixClient : ClientModInitializer {
    val logger = LoggerFactory.getLogger("helixforaging")

    lateinit var context: ClientContext
        private set

    override fun onInitializeClient() {
        context = ClientContext()

        ConfigManager.initialize()
        ModuleManager.initialize()
        StatisticsManager.initialize()
        HudManager.initialize()
        KeybindManager.initialize()
        MacroManager.initialize()
        DebugOverlay.initialize()
        HelixEvents.register()
        NotificationManager.initialize()

        logger.info("Helix Client initialized successfully")
    }

    fun openClickGui() {
        val client = context.minecraft
        client.setScreen(ClickGuiScreen())
    }
}
