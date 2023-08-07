package me.fireandice.ghosttracker.hud.elements

import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.hud.Image
import me.fireandice.ghosttracker.utils.FONT_HEIGHT
import net.minecraft.client.gui.Gui
import kotlin.reflect.KProperty0

/**
 * A general hud line that only uses one color. This is used for kills, magic find, and all xp displays. Note: the
 * prefix should have a space at the end
 * @param prefix The prefix text. This is not a [ColoredText] because it will always be the same color as [text]
 * @param text The main text that displays the relevant tracker stat
 * @param image The icon that may display before the hud line
 * @param visible The backing property of the config option that decides if the line is shown
 */
class BasicHudLine(
    var prefix: String? = null,
    var text: ColoredText,
    private val image: Image,
    private var visible: KProperty0<Boolean>
) : HudLine {

    override val width: Float
        get() {
            var w = mc.fontRendererObj.getStringWidth(text.text).toFloat()
            if (GhostConfig.showIcons) w += 10
            if (GhostConfig.showPrefixes && prefix != null) w += mc.fontRendererObj.getStringWidth(prefix)
            return w
        }
    override val height: Float
        get() = FONT_HEIGHT.toFloat()

    override fun draw(x: Float, y: Float, scale: Float): Boolean {
        if (!visible.get()) return false

        val scale = 1f  // TODO remove this when I add scaling support

        var currentX = x
        if (GhostConfig.showIcons) {
            mc.textureManager.bindTexture(image.location)
            Gui.drawModalRectWithCustomSizedTexture(
                currentX.toInt(),
                y.toInt(),
                0f,
                0f,
                8 * scale.toInt(),
                8 * scale.toInt(),
                image.width.toFloat(),
                image.height.toFloat()
            )
            currentX += 10 * scale
        }

        if (GhostConfig.showPrefixes) {
            TextRenderer.drawScaledString(prefix, currentX, y, text.color, GhostConfig.shadow(), scale)
            currentX += mc.fontRendererObj.getStringWidth(prefix) * scale
        }

        TextRenderer.drawScaledString(text.text, currentX, y, text.color, GhostConfig.shadow(), scale)
        return true
    }
}