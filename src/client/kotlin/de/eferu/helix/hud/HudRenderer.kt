package de.eferu.helix.hud

import de.eferu.helix.config.ConfigManager
import de.eferu.helix.macro.MacroManager
import de.eferu.helix.modules.HudModule
import de.eferu.helix.route.HelixRouteManager
import de.eferu.helix.safety.EnvironmentGuard
import de.eferu.helix.statistics.StatisticsManager
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import java.util.concurrent.TimeUnit

object HudManager {
    fun initialize() = Unit
}

object HudRenderer {
    fun render(graphics: GuiGraphicsExtractor, tickCounter: DeltaTracker) {
        if (!HudModule.visible) return
        val client = Minecraft.getInstance()
        val width = client.window.guiScaledWidth
        val height = client.window.guiScaledHeight
        val stats = StatisticsManager.current
        val hud = ConfigManager.hud
        val theme = ConfigManager.client
        val font = client.font

        renderLeftPanel(graphics, font, hud.leftPanelX, hud.leftPanelY, stats, theme)
        renderCenterStatus(graphics, font, width / 2f, 72f, stats)
        if (hud.rightPanelVisible) {
            renderRightPanel(graphics, font, width + hud.rightPanelX, hud.rightPanelY, stats)
        }
        renderBottomPanel(graphics, font, width / 2f, height - 28f)
        renderEnvironmentBanner(graphics, font, width, theme)
        de.eferu.helix.notification.NotificationManager.active().forEachIndexed { index, notification ->
            renderNotification(
                graphics,
                font,
                width - 220,
                height - 60 - index * 34,
                notification.title,
                notification.message,
                notification.alpha,
                theme,
            )
        }
    }

    private fun renderLeftPanel(
        graphics: GuiGraphicsExtractor,
        font: net.minecraft.client.gui.Font,
        x: Float,
        y: Float,
        stats: de.eferu.helix.statistics.SessionStatistics,
        theme: de.eferu.helix.config.ClientConfig,
    ) {
        val w = 180
        val h = 150
        drawPanel(graphics, x.toInt(), y.toInt(), w, h, theme.themeBackground, theme.themePrimary)
        var line = y.toInt() + 10
        drawText(graphics, font, "HELIX FORAGING", x.toInt() + 10, line, theme.themePrimary)
        line += 16
        drawText(graphics, font, "Session ${formatDuration(stats.sessionDurationMs())}", x.toInt() + 10, line, theme.themeText)
        line += 12
        drawText(graphics, font, "Logs ${stats.blocksHarvested}", x.toInt() + 10, line, theme.themeText)
        line += 12
        drawText(graphics, font, "Rate ${formatCompact(stats.blocksPerHour())}/h", x.toInt() + 10, line, theme.themeSuccess)
        line += 12
        drawText(graphics, font, "XP +${formatCompact(stats.xpGained)}", x.toInt() + 10, line, theme.themeSuccess)
        line += 12
        drawText(graphics, font, "XP/h ${formatCompact(stats.xpPerHour())}", x.toInt() + 10, line, theme.themeSuccess)
        line += 12
        drawText(graphics, font, "Route ${stats.routeName}", x.toInt() + 10, line, theme.themeText)
        line += 12
        val progress = ((HelixRouteManager.progress()) * 100).toInt()
        drawText(graphics, font, "Progress ${progress}%", x.toInt() + 10, line, theme.themeSecondary)
    }

    private fun renderCenterStatus(
        graphics: GuiGraphicsExtractor,
        font: net.minecraft.client.gui.Font,
        centerX: Float,
        y: Float,
        stats: de.eferu.helix.statistics.SessionStatistics,
    ) {
        val state = MacroManager.macro.state.name
        val tree = "TREE ${stats.routePoint}"
        graphics.centeredText(font, "HELIX", centerX.toInt(), y.toInt(), 0xFFE056FD.toInt())
        graphics.centeredText(font, tree, centerX.toInt(), y.toInt() + 14, 0xFF9B59FF.toInt())
        graphics.centeredText(font, state, centerX.toInt(), y.toInt() + 30, 0xFFECECF4.toInt())
    }

    private fun renderRightPanel(
        graphics: GuiGraphicsExtractor,
        font: net.minecraft.client.gui.Font,
        x: Float,
        y: Float,
        stats: de.eferu.helix.statistics.SessionStatistics,
    ) {
        val profile = ConfigManager.helix.activeProfile.uppercase()
        val theme = ConfigManager.client
        drawPanel(graphics, x.toInt(), y.toInt(), 200, 120, theme.themeBackground, theme.themePrimary)
        var line = y.toInt() + 10
        drawText(graphics, font, profile, x.toInt() + 10, line, theme.themeSecondary)
        line += 14
        drawText(graphics, font, "Status: ${MacroManager.macro.state}", x.toInt() + 10, line, theme.themeText)
        line += 12
        drawText(graphics, font, "Route: ${stats.routePoint} / ${stats.routePointCount}", x.toInt() + 10, line, theme.themeText)
        line += 12
        drawText(graphics, font, "Recoveries: ${stats.recoveries}", x.toInt() + 10, line, theme.themeWarning)
    }

    private fun renderBottomPanel(graphics: GuiGraphicsExtractor, font: net.minecraft.client.gui.Font, centerX: Float, y: Float) {
        val player = Minecraft.getInstance().player ?: return
        val health = player.health.toInt()
        val maxHealth = player.maxHealth.toInt()
        graphics.centeredText(font, "❤ $health / $maxHealth", centerX.toInt(), y.toInt(), 0xFFF87171.toInt())
    }

    private fun renderEnvironmentBanner(
        graphics: GuiGraphicsExtractor,
        font: net.minecraft.client.gui.Font,
        width: Int,
        theme: de.eferu.helix.config.ClientConfig,
    ) {
        if (EnvironmentGuard.isAllowed()) return
        graphics.centeredText(font, EnvironmentGuard.denyReason(), width / 2, 12, theme.themeError)
    }

    private fun renderNotification(
        graphics: GuiGraphicsExtractor,
        font: net.minecraft.client.gui.Font,
        x: Int,
        y: Int,
        title: String,
        message: String,
        alpha: Float,
        theme: de.eferu.helix.config.ClientConfig,
    ) {
        val color = applyAlpha(theme.themeBackground, alpha)
        drawPanel(graphics, x, y, 200, 28, color, applyAlpha(theme.themePrimary, alpha))
        drawText(graphics, font, title, x + 8, y + 6, applyAlpha(theme.themeText, alpha))
        if (message.isNotBlank()) drawText(graphics, font, message, x + 8, y + 16, applyAlpha(0xFFCCCCCC.toInt(), alpha))
    }

    private fun drawPanel(graphics: GuiGraphicsExtractor, x: Int, y: Int, w: Int, h: Int, fill: Int, border: Int) {
        graphics.fill(x, y, x + w, y + h, fill)
        graphics.fill(x, y, x + w, y + 1, border)
        graphics.fill(x, y + h - 1, x + w, y + h, border)
        graphics.fill(x, y, x + 1, y + h, border)
        graphics.fill(x + w - 1, y, x + w, y + h, border)
    }

    private fun drawText(graphics: GuiGraphicsExtractor, font: net.minecraft.client.gui.Font, text: String, x: Int, y: Int, color: Int) {
        graphics.text(font, text, x, y, color, false)
    }

    private fun formatDuration(ms: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(ms)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun formatCompact(value: Double): String = when {
        value >= 1_000_000 -> String.format("%.1fm", value / 1_000_000)
        value >= 1_000 -> String.format("%.1fk", value / 1_000)
        else -> String.format("%.0f", value)
    }

    private fun applyAlpha(color: Int, alpha: Float): Int {
        val a = ((color ushr 24) and 0xFF) * alpha
        return ((a.toInt() and 0xFF) shl 24) or (color and 0x00FFFFFF)
    }
}
