package de.eferu.helix.macro

import de.eferu.helix.world.TargetManager
import de.eferu.helix.world.WorldScanner
import net.minecraft.client.Minecraft

object MacroManager {
    private val scanner = WorldScanner()
    private val targetManager = TargetManager()
    val macro = ForagingMacro(scanner, targetManager)

    fun initialize() {
        // lazy init
    }

    fun onTick(client: Minecraft) {
        macro.tick(client)
    }

    fun scanner(): WorldScanner = scanner
}
