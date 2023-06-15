package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.renderer.TextRenderer

class SingleColorText(
    private val text: String,
    private val color: Int,
    private val shadowType: TextRenderer.TextType = TextRenderer.TextType.SHADOW
) : TextComponent {

    override var width: Float = 0f

    override fun draw(x: Float, y: Float, scale: Float) {
        TextRenderer.drawScaledString(text, x, y, color, shadowType, scale)
        width = UMinecraft.getFontRenderer().getStringWidth(text) * scale
    }
}