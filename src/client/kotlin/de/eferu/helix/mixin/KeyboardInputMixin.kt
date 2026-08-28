package de.eferu.helix.mixin

import de.eferu.helix.macro.MacroManager
import de.eferu.helix.pathfinding.MovementController
import net.minecraft.client.Options
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import net.minecraft.client.player.KeyboardInput

@Mixin(KeyboardInput::class)
abstract class KeyboardInputMixin {
    @Shadow
    private lateinit var options: Options

    @Inject(method = ["tick"], at = [At("HEAD")])
    private fun onHelixKeyboardTick(callbackInfo: CallbackInfo) {
        if (!MacroManager.macro.running || MacroManager.macro.paused) return
        val movement: MovementController = MacroManager.macro.movementController()
        options.keyUp.isDown = movement.forward
        options.keyDown.isDown = movement.backward
        options.keyLeft.isDown = movement.left
        options.keyRight.isDown = movement.right
        options.keyJump.isDown = movement.jump
    }
}
