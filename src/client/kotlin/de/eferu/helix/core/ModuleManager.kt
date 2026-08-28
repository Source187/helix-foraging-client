package de.eferu.helix.core

import de.eferu.helix.modules.DebugModule
import de.eferu.helix.modules.ForagingModule
import de.eferu.helix.modules.HudModule
import de.eferu.helix.modules.RouteVisualsModule
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper

object ModuleManager {
    private val modules = mutableListOf<Module>()

    val foraging: ForagingModule by lazy { register(ForagingModule()) }
    val hud: HudModule by lazy { register(HudModule()) }
    val routeVisuals: RouteVisualsModule by lazy { register(RouteVisualsModule()) }
    val debug: DebugModule by lazy { register(DebugModule()) }

    fun initialize() {
        foraging
        hud
        routeVisuals
        debug
        modules.forEach { KeyMappingHelper.registerKeyMapping(it.keybind) }
    }

    private fun <T : Module> register(module: T): T {
        modules += module
        return module
    }

    fun all(): List<Module> = modules.toList()

    fun byCategory(category: ModuleCategory): List<Module> =
        modules.filter { it.category == category }

    fun onTick() {
        modules.filter { it.enabled }.forEach { it.onTick() }
        modules.forEach { it.onKeyInput() }
    }

    fun onRender() {
        modules.filter { it.enabled }.forEach { it.onRender() }
    }

    fun onWorldChange() {
        modules.forEach { it.onWorldChange() }
    }
}
