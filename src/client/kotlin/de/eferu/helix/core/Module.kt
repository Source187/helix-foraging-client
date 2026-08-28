package de.eferu.helix.core

import com.mojang.blaze3d.platform.InputConstants
import de.eferu.helix.input.HelixKeys
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

abstract class Module(
    val name: String,
    val description: String,
    val category: ModuleCategory,
    defaultEnabled: Boolean = false,
    defaultKey: Int = GLFW.GLFW_KEY_UNKNOWN,
) {
    var enabled: Boolean = defaultEnabled
        private set

    var keybind: KeyMapping = KeyMapping(
        "key.helixforaging.$name",
        InputConstants.Type.KEYSYM,
        defaultKey,
        HelixKeys.CATEGORY,
    )

    fun enable() {
        if (!enabled) {
            enabled = true
            onEnable()
        }
    }

    fun disable() {
        if (enabled) {
            onDisable()
            enabled = false
        }
    }

    fun toggle() {
        if (enabled) disable() else enable()
    }

    open fun onEnable() {}
    open fun onDisable() {}
    open fun onTick() {}
    open fun onRender() {}
    open fun onWorldChange() {}
    open fun onKeyInput() {
        if (keybind.consumeClick()) toggle()
    }
}
