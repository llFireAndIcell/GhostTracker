package me.fireandice.ghosttracker.hud.elements

import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.utils.FONT_HEIGHT
import me.fireandice.ghosttracker.utils.Image
import net.minecraft.client.gui.Gui

/**
 * A hud line that two different colored text components
 */
class DualHudLine(
    override var id: String,
    var first: ColoredText,
    var second: ColoredText,
    private val image: Image,
    var visible: () -> Boolean
) : HudLine {

    override val width: Float
        get() = mc.fontRendererObj.getStringWidth("${first.text} ${second.text}").toFloat()
    override val height: Float
        get() = FONT_HEIGHT.toFloat()

    override fun draw(x: Float, y: Float, scale: Float): Boolean {
        if (!visible()) return false

        mc.textureManager.bindTexture(image.location)
        Gui.drawModalRectWithCustomSizedTexture(
            x.toInt(),
            y.toInt(),
            0f,
            0f,
            16,
            16,
            image.width.toFloat(),
            image.height.toFloat()
        )

        TextRenderer.drawScaledString(first.text, x, y, first.color, GhostConfig.shadow(), scale)
        val currentWidth = mc.fontRendererObj.getStringWidth(first.text) * scale

        TextRenderer.drawScaledString(" ${second.text}", x + currentWidth, y, second.color, GhostConfig.shadow(), scale)
        return true
    }
}