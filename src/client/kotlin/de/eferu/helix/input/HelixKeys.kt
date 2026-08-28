package de.eferu.helix.input

import net.minecraft.client.KeyMapping
import net.minecraft.resources.Identifier

object HelixKeys {
    val CATEGORY: KeyMapping.Category = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath("helixforaging", "helix"),
    )
}
