package me.fireandice.ghosttracker.hud.elements

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.core.OneColor
import kotlin.reflect.KProperty0

class ColoredText(
    var text: String,
    private val colorRef: KProperty0<OneColor>
) {
    val color get() = colorRef.get().rgb
}

/**
 * Creates a [ColoredText] with the receiving [String] and the specified color property from your [Config]
 */
infix fun String.with(color: KProperty0<OneColor>) = ColoredText(this, color)
