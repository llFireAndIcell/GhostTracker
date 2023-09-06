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
 * @param prefix The prefix text. This is not a [ColoredText] because it will always be the same color as [text]
 * @param text The main text that displays the relevant tracker stat
 * @param margin Text after the main text that that displays the margins, and may be hidden by the user
 * @param image The icon that may display before the hud line
 * @param visible The backing property of the config option that decides if the line is shown
 * @param suffixVisible An expression to calculate if the suffix should be shown
 */
class SuffixHudLine(
    var prefix: String? = null,
    var text: ColoredText,
    var margin: ColoredText,
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
            val guiScale = mc.gameSettings.guiScale
            mc.textureManager.bindTexture(image)
            drawTexturedRect(
                currentX.toDouble(),
                y.toDouble(),
                0f,
                0f,
                8 * scale.toDouble(),
                8 * scale.toDouble(),
                16f / guiScale * scale,
                16f / guiScale * scale
            )
            currentX += 10 * scale
            currentWidth += 10
        }

        if (GhostConfig.showPrefixes) {
            TextRenderer.drawScaledString(prefix, currentX, y, text.color, GhostConfig.shadow(), scale)
            val prefixWidth = mc.fontRendererObj.getStringWidth(prefix)
            currentX += prefixWidth * scale
            currentWidth += prefixWidth
        }

        TextRenderer.drawScaledString(text.text, currentX, y, text.color, GhostConfig.shadow(), scale)
        val textWidth = mc.fontRendererObj.getStringWidth(text.text)
        currentWidth += textWidth

        if (!suffixVisible()) return true
        currentX += textWidth * scale
        TextRenderer.drawScaledString(margin.text, currentX, y, margin.color, GhostConfig.shadow(), scale)
        currentWidth += mc.fontRendererObj.getStringWidth(margin.text)

        width = currentWidth
        return true
    }
}