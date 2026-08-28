package de.eferu.helix.debug

import de.eferu.helix.macro.MacroManager
import de.eferu.helix.modules.DebugModule
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor

object DebugOverlay {
    fun initialize() = Unit

    fun renderHud(graphics: GuiGraphicsExtractor) {
        if (!DebugModule.debugEnabled) return
        val client = Minecraft.getInstance()
        val macro = MacroManager.macro
        var y = 4
        fun line(text: String) {
            graphics.text(client.font, text, 4, y, 0xFFCCCCCC.toInt(), false)
            y += 10
        }
        line("FPS ${client.fps}")
        line("State ${macro.state}")
        line("Scanner ${MacroManager.scanner().lastScanNanos / 1_000_000.0} ms")
        line("Recoveries ${de.eferu.helix.statistics.StatisticsManager.current.recoveries}")
    }
}

object DebugRenderer {
    fun render(context: LevelRenderContext) {
        // Reserved for world-space debug geometry.
    }
}
