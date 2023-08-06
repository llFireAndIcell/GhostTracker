package me.fireandice.ghosttracker.hud.elements

import cc.polyfrost.oneconfig.config.core.OneColor
import kotlin.reflect.KProperty0

class ColoredText(
    var text: String,
    private val colorRef: KProperty0<OneColor>
) {
    val color: Int
        get() = colorRef.get().rgb
}

infix fun String.with(color: KProperty0<OneColor>) = ColoredText(this, color)