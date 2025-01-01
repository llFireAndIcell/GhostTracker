package me.fireandice.ghosttracker.hud.elements

import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.polyui.color.PolyColor
import kotlin.reflect.KProperty0

class ColoredText(
    var text: String,
    private val colorRef: KProperty0<PolyColor>
) {
    val color get() = colorRef.get()
}

/**
 * Creates a [ColoredText] with the receiving [String] and the specified color property from your [Config]
 */
infix fun String.withColor(color: KProperty0<PolyColor>) = ColoredText(this, color)
