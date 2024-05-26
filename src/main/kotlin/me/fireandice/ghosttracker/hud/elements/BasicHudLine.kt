package me.fireandice.ghosttracker.hud.elements

import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.utils.drawTexturedRect
import net.minecraft.util.ResourceLocation
import kotlin.reflect.KProperty0

/**
 * A general hud line that only uses one color. This is used for kills, magic find, and all xp displays. Note: the
 * prefix should have a space at the end
 * @param prefix The prefix text. This is not a [ColoredText] because it will always be the same color as [main]
 * @param main The main text that displays the relevant tracker stat
 * @param image The icon that may display before the hud line
 * @param visible The backing property of the config option that decides if the line is shown
 */
class BasicHudLine(
    var prefix: String? = null,
    var main: ColoredText,
    private val image: ResourceLocation,
    private var visible: KProperty0<Boolean>
) : HudLine {

    override var width: Float = 0f
    override var height: Float = 0f

    override fun draw(x: Float, y: Float, scale: Float): Boolean {
        if (!visible.get()) {
            width = 0f
            height = 0f
            return false
        }
        height = 9f

        var currentX = x
        var currentWidth = 0f

        if (GhostConfig.showIcons) {
            mc.textureManager.bindTexture(image)
            drawTexturedRect(
                x = currentX.toDouble(),
                y = y.toDouble(),
                u = 0f,
                v = 0f,
                width = 8 * scale.toDouble(),
                height = 8 * scale.toDouble(),
                textureWidth = 8f * scale,
                textureHeight = 8f * scale
            )
            currentX += 10 * scale
            currentWidth += 10
        }

        if (GhostConfig.showPrefixes) {
            TextRenderer.drawScaledString(prefix, currentX, y, main.color, GhostConfig.shadow(), scale)
            val prefixWidth = mc.fontRendererObj.getStringWidth(prefix)
            currentX += prefixWidth * scale
            currentWidth += prefixWidth
        }

        TextRenderer.drawScaledString(main.text, currentX, y, main.color, GhostConfig.shadow(), scale)
        currentWidth += mc.fontRendererObj.getStringWidth(main.text)
        width = currentWidth
        return true
    }
}