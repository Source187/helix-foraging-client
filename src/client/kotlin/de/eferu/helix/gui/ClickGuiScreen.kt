package de.eferu.helix.gui

import de.eferu.helix.config.ConfigManager
import de.eferu.helix.core.ModuleCategory
import de.eferu.helix.core.ModuleManager
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class ClickGuiScreen : Screen(Component.literal("Helix Client")) {
    private var selectedCategory: ModuleCategory = ModuleCategory.FORAGING
    private lateinit var searchBox: EditBox
    private var fade = 0f

    override fun init() {
        super.init()
        searchBox = EditBox(font, width / 2 - 100, 24, 200, 18, Component.literal("Search"))
        addRenderableWidget(searchBox)
        var y = 56
        ModuleCategory.entries.forEach { category ->
            addRenderableWidget(
                Button.builder(Component.literal(category.displayName)) {
                    selectedCategory = category
                }.bounds(20, y, 110, 20).build(),
            )
            y += 24
        }
        addRenderableWidget(
            Button.builder(Component.literal("Save Config")) {
                ConfigManager.saveAll()
            }.bounds(width - 120, height - 28, 100, 20).build(),
        )
        addRenderableWidget(
            Button.builder(Component.literal("Reset Layout")) {
                ConfigManager.resetHudLayout()
            }.bounds(width - 230, height - 28, 100, 20).build(),
        )
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        fade = (fade + partialTick * 0.08f).coerceAtMost(1f)
        graphics.fill(0, 0, width, height, applyAlpha(0xCC080810.toInt(), fade))
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        graphics.centeredText(font, "HELIX CLIENT", width / 2, 8, 0xFF9B59FF.toInt())
        searchBox.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick)

        var y = 56
        val query = searchBox.value.lowercase()
        ModuleManager.byCategory(selectedCategory)
            .filter { query.isBlank() || it.name.contains(query) || it.description.contains(query) }
            .forEach { module ->
                val cardX = 150
                drawCard(graphics, cardX, y, width - cardX - 20, 42)
                graphics.text(font, module.name.uppercase(), cardX + 10, y + 8, 0xFFECECF4.toInt(), false)
                graphics.text(font, module.description, cardX + 10, y + 20, 0xFFBBBBBB.toInt(), false)
                val toggleLabel = if (module.enabled) "ON" else "OFF"
                graphics.text(
                    font,
                    toggleLabel,
                    width - 70,
                    y + 14,
                    if (module.enabled) 0xFF4ADE80.toInt() else 0xFFF87171.toInt(),
                    false,
                )
                y += 48
            }
        super.extractRenderState(graphics, mouseX, mouseY, partialTick)
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (event.button() == 0) {
            var y = 56
            val query = searchBox.value.lowercase()
            ModuleManager.byCategory(selectedCategory)
                .filter { query.isBlank() || it.name.contains(query) || it.description.contains(query) }
                .forEach { module ->
                    if (event.x() in 150.0..(width - 20).toDouble() && event.y() in y.toDouble()..(y + 42).toDouble()) {
                        module.toggle()
                        return true
                    }
                    y += 48
                }
        }
        return super.mouseClicked(event, doubleClick)
    }

    private fun drawCard(graphics: GuiGraphicsExtractor, x: Int, y: Int, w: Int, h: Int) {
        graphics.fill(x, y, x + w, y + h, 0xAA14141C.toInt())
        graphics.fill(x, y, x + w, y + 1, 0xFF9B59FF.toInt())
    }

    private fun applyAlpha(color: Int, alpha: Float): Int {
        val a = ((color ushr 24) and 0xFF) * alpha
        return ((a.toInt() and 0xFF) shl 24) or (color and 0x00FFFFFF)
    }
}
