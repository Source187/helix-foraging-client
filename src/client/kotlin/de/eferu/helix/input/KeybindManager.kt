package de.eferu.helix.input

import com.mojang.blaze3d.platform.InputConstants
import de.eferu.helix.HelixClient
import de.eferu.helix.core.ModuleManager
import de.eferu.helix.macro.MacroManager
import de.eferu.helix.modules.DebugModule
import de.eferu.helix.modules.HudModule
import de.eferu.helix.modules.RouteVisualsModule
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

object KeybindManager {
    lateinit var toggleAutomation: KeyMapping
    lateinit var pauseAutomation: KeyMapping
    lateinit var emergencyStop: KeyMapping
    lateinit var openClickGui: KeyMapping
    lateinit var toggleHud: KeyMapping
    lateinit var toggleRoute: KeyMapping
    lateinit var toggleDebug: KeyMapping

    fun initialize() {
        toggleAutomation = register("toggle_automation", GLFW.GLFW_KEY_G)
        pauseAutomation = register("pause_automation", GLFW.GLFW_KEY_H)
        emergencyStop = register("emergency_stop", GLFW.GLFW_KEY_J)
        openClickGui = register("open_click_gui", GLFW.GLFW_KEY_RIGHT_SHIFT)
        toggleHud = register("toggle_hud", GLFW.GLFW_KEY_U)
        toggleRoute = register("toggle_route", GLFW.GLFW_KEY_Y)
        toggleDebug = register("toggle_debug", GLFW.GLFW_KEY_K)
    }

    private fun register(name: String, key: Int): KeyMapping {
        val binding = KeyMapping(
            "key.helixforaging.$name",
            InputConstants.Type.KEYSYM,
            key,
            HelixKeys.CATEGORY,
        )
        return KeyMappingHelper.registerKeyMapping(binding)
    }

    fun handle(client: net.minecraft.client.Minecraft) {
        if (toggleAutomation.consumeClick()) {
            if (MacroManager.macro.running) MacroManager.macro.stop() else MacroManager.macro.start()
        }
        if (pauseAutomation.consumeClick()) {
            if (MacroManager.macro.paused) MacroManager.macro.resume() else MacroManager.macro.pause()
        }
        if (emergencyStop.consumeClick()) MacroManager.macro.emergencyStop()
        if (openClickGui.consumeClick()) HelixClient.openClickGui()
        if (toggleHud.consumeClick()) HudModule.visible = !HudModule.visible
        if (toggleRoute.consumeClick()) RouteVisualsModule.routeVisible = !RouteVisualsModule.routeVisible
        if (toggleDebug.consumeClick()) DebugModule.debugEnabled = !DebugModule.debugEnabled
        ModuleManager.onTick()
    }
}
