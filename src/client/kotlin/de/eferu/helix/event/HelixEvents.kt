package de.eferu.helix.event

import de.eferu.helix.core.ModuleManager
import de.eferu.helix.debug.DebugOverlay
import de.eferu.helix.debug.DebugRenderer
import de.eferu.helix.hud.HudRenderer
import de.eferu.helix.input.KeybindManager
import de.eferu.helix.macro.MacroManager
import de.eferu.helix.route.RouteRenderer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.minecraft.resources.Identifier

object HelixEvents {
    private val HUD_ID = Identifier.fromNamespaceAndPath("helixforaging", "hud")

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            KeybindManager.handle(client)
            ModuleManager.onTick()
            MacroManager.onTick(client)
        }

        HudElementRegistry.addLast(HUD_ID) { graphics, deltaTracker ->
            HudRenderer.render(graphics, deltaTracker)
            DebugOverlay.renderHud(graphics)
        }

        LevelRenderEvents.END_MAIN.register { context ->
            RouteRenderer.render(context)
            DebugRenderer.render(context)
            context.bufferSource().endBatch()
        }
    }
}
