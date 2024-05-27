package me.fireandice.ghosttracker.hud.elements

import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.utils.drawTexturedRect
import net.minecraft.util.ResourceLocation
import kotlin.reflect.KProperty0

/**
 * A hud line that two different colored text components. Used for drops and time hud lines. Note: prefix should
 * include a space after, and suffix should include a space before
 * @param prefix The prefix text. This is not a [ColoredText] because it will always be the same color as [main]
 * @param main The main text that displays the relevant tracker stat
 * @param suffix Text after the main text that may be hidden by the user
 * @param image The icon that may display before the hud line
 * @param visible The backing property of the config option that decides if the line is shown
 * @param suffixVisible An expression to calculate if the suffix should be shown
 */
class SuffixHudLine(
    var prefix: String? = null,
    var main: ColoredText,
    var suffix: ColoredText,
    private val image: ResourceLocation,
    val visible: KProperty0<Boolean>,
    val suffixVisible: () -> Boolean
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
            TextRenderer.drawScaledString(prefix, currentX, y, main.color, GhostConfig.shadow, scale)
            val prefixWidth = mc.fontRendererObj.getStringWidth(prefix)
            currentX += prefixWidth * scale
            currentWidth += prefixWidth
        }

        TextRenderer.drawScaledString(main.text, currentX, y, main.color, GhostConfig.shadow, scale)
        val textWidth = mc.fontRendererObj.getStringWidth(main.text)
        currentWidth += textWidth

        if (suffixVisible()) {
            currentX += textWidth * scale
            TextRenderer.drawScaledString(suffix.text, currentX, y, suffix.color, GhostConfig.shadow, scale)
            currentWidth += mc.fontRendererObj.getStringWidth(suffix.text)
        }

        width = currentWidth
        return true
    }
}