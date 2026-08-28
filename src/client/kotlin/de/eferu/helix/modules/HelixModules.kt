package de.eferu.helix.modules

import de.eferu.helix.core.Module
import de.eferu.helix.core.ModuleCategory
import de.eferu.helix.macro.MacroManager
import org.lwjgl.glfw.GLFW

class ForagingModule : Module(
    name = "foraging",
    description = "Helix foraging automation framework for private test environments.",
    category = ModuleCategory.FORAGING,
) {
    override fun onTick() {
        MacroManager.onTick(de.eferu.helix.HelixClient.context.minecraft)
    }
}

class HudModule : Module(
    name = "hud",
    description = "SkyBlock-inspired statistics HUD.",
    category = ModuleCategory.HUD,
    defaultEnabled = true,
    defaultKey = GLFW.GLFW_KEY_UNKNOWN,
) {
    companion object {
        var visible: Boolean = true
    }
}

class RouteVisualsModule : Module(
    name = "route_visuals",
    description = "Render helix route overlays in the world.",
    category = ModuleCategory.VISUALS,
    defaultEnabled = true,
) {
    companion object {
        var routeVisible: Boolean = true
    }
}

class DebugModule : Module(
    name = "debug",
    description = "Debug overlay and state transition logging.",
    category = ModuleCategory.DEBUG,
) {
    companion object {
        var debugEnabled: Boolean = false
    }
}
