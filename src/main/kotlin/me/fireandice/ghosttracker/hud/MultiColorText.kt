package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.renderer.TextRenderer

class MultiColorText(
    private val shadowType: TextRenderer.TextType = TextRenderer.TextType.SHADOW
) : TextComponent {

    private var components: ArrayList<ColoredText> = ArrayList()
    override val width: Float
        get() {
            var sum = 0f
            for (comp in components) sum += UMinecraft.getFontRenderer().getStringWidth(comp.text)
            return sum
        }
    override var shouldDraw: Boolean = true

    override fun draw(x: Float, y: Float, scale: Float) {
        if (components.isEmpty()) return

        var textX = x

        for (comp in components) {
            TextRenderer.drawScaledString(comp.text, textX, y, comp.color, shadowType, scale)
            textX += UMinecraft.getFontRenderer().getStringWidth(comp.text) * scale
        }
    }

    fun add(text: String, color: Int) {
        components.add(ColoredText(text, color))
    }

    fun set(index: Int, value: ColoredText) {
        components[index] = value
    }

    fun set(index: Int, string: String, color: Int) = set(index, ColoredText(string, color))

    class ColoredText(val text: String, val color: Int)
}