package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.renderer.TextRenderer.TextType

/**
 * Stores a single line of text to be rendered, composed of many strings that can be different colors. NOTE: Spaces
 * are not automatically added between components, remember to add them if you want them.
 * @param shadowType The type of text shadow to be rendered (NONE, SHADOW, or FULL), as a `TextRenderer.TextType`
 * @param init A block where the object being created is supplied as `it`. This should mainly be used to add components
 */
class TextComponent(private val shadowType: TextType = TextType.SHADOW, init: (TextComponent) -> Unit) {

    private var components: ArrayList<ColoredText> = ArrayList()
    var width: Float = 0f

    fun add(text: String, color: OneColor) {
        components.add(ColoredText(text, color))
    }

    fun draw(x: Float, y: Float, scale: Float) {
        if (components.isEmpty()) return

        width = 0f
        var textX = x

        for (comp in components) {
            TextRenderer.drawScaledString(comp.text, textX, y, comp.color.rgb, shadowType, scale)
            val compWidth = UMinecraft.getFontRenderer().getStringWidth(comp.text) * scale
            textX += compWidth
            width += compWidth
        }
    }

    init {
        init(this)
    }

    data class ColoredText(val text: String, val color: OneColor)
}