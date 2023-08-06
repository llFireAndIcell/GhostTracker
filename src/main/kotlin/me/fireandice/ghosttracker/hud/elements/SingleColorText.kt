package me.fireandice.ghosttracker.hud.elements

import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.renderer.TextRenderer

/**
 * @param text The text
 * @param color The color in RGBA format
 * @param shadowType The type of text shadow used, as a [TextRenderer.TextType]
 * @param shouldDrawCheck The function used to calculate whether this element should draw. This is run every time
 * [shouldDraw] is called, and is inlined.
 */
class SingleColorText(
    private var text: String,
    private var color: Int,
    private val shadowType: TextRenderer.TextType = TextRenderer.TextType.SHADOW,
    inline var shouldDrawCheck: () -> Boolean
) : TextComponent {

    /**
     * This is unscaled, and is calculated everytime the getter is called
     */
    override val width: Float
        get() = UMinecraft.getFontRenderer().getStringWidth(text).toFloat()

    override var shouldDraw: Boolean
        get() = shouldDrawCheck()
        set(value) {
            shouldDrawCheck = { value }
        }

    override fun draw(x: Float, y: Float, scale: Float) =
        TextRenderer.drawScaledString(text, x, y, color, shadowType, scale)

    fun set(text: String, color: Int) {
        this.text = text
        this.color = color
    }
}