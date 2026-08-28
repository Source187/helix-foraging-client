package de.eferu.helix.core

import net.minecraft.client.Minecraft

class ClientContext {
    val minecraft: Minecraft get() = Minecraft.getInstance()
}
