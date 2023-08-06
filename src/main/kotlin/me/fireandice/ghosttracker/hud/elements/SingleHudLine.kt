package me.fireandice.ghosttracker.hud.elements

import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.utils.FONT_HEIGHT
import me.fireandice.ghosttracker.utils.Image
import net.minecraft.client.gui.Gui

/**
 * A general hud line that only uses one color. This is used for kills, magic find, and all xp displays
 */
class SingleHudLine(
    override val id: String,
    var text: ColoredText,
    val image: Image,
    var visible: () -> Boolean
) : HudLine {

    override val width: Float
        get() = mc.fontRendererObj.getStringWidth(text.text).toFloat()
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

        TextRenderer.drawScaledString(text.text, x, y, text.color, GhostConfig.shadow(), scale)
        return true
    }
}