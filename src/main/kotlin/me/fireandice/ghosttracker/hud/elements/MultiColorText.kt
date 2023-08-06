package me.fireandice.ghosttracker.hud.elements

import cc.polyfrost.oneconfig.renderer.TextRenderer

class MultiColorText(
    private val shadowType: TextRenderer.TextType = TextRenderer.TextType.SHADOW
) : TextComponent {

    private var components: ArrayList<SingleColorText> = ArrayList()

    /**
     * This is unscaled, and is calculated every time the getter is called
     */
    override val width: Float
        get() {
            var sum = 0f
            for (comp in components) if (comp.shouldDraw) sum += comp.width
            return sum
        }

    // multicolor text should only draw if the first component is visible
    override var shouldDraw: Boolean
        get() = try {
            components[0].shouldDraw
        } catch (e: IndexOutOfBoundsException) {
            false
        }
        set(value) {
            for (comp in components) comp.shouldDraw = value
        }

    override fun draw(x: Float, y: Float, scale: Float) {
        var textX = x

        for (comp in components) {
            if (!comp.shouldDraw) continue
            comp.draw(textX, y, scale)
            textX += comp.width * scale
        }
    }

    fun add(text: String, color: Int, shouldDrawCheck: () -> Boolean) {
        components.add(SingleColorText(text, color, shadowType, shouldDrawCheck))
    }

    fun set(index: Int, text: String, color: Int) {
        try {
            if (!components[index].shouldDraw) return
            components[index].set(text, color)
        } catch (e: IndexOutOfBoundsException) {
            add(text, color, components[0].shouldDrawCheck)
        }
    }
}