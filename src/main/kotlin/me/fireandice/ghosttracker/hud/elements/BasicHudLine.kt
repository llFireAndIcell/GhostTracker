package me.fireandice.ghosttracker.hud.elements

import me.fireandice.ghosttracker.config.GhostConfig
import org.polyfrost.polyui.data.PolyImage
import org.polyfrost.polyui.dsl.DrawableDSL
import org.polyfrost.polyui.unit.Align
import org.polyfrost.polyui.unit.Vec2
import kotlin.reflect.KProperty0

/**
 * A general hud line that only uses one color. This is used for kills, magic find, and all xp displays. Note: the
 * prefix should have a space at the end
 * @param prefix The prefix text. This is not a [ColoredText] because it will always be the same color as [main]
 * @param main The main text that displays the relevant tracker stat
 * @param icon The icon that may display before the hud line
 * @param visible The backing property of the config option that decides if the line is shown
 */
class BasicHudLine(
    var prefix: String? = null,
    var main: ColoredText,
    private val icon: PolyImage,
    private var visible: KProperty0<Boolean>
) : HudLine {

    override var width: Float = 0f
    override var height: Float = 0f

    @Suppress("UnstableApiUsage")
    override fun draw(polyUI: DrawableDSL.Master, x: Float, y: Float, scale: Float): Boolean {
        if (!visible.get()) {
            width = 0f
            height = 0f
            return false
        }
        height = 9f

        polyUI.group(alignment = Align(pad = Vec2.of(2f, 2f))) {

            if (GhostConfig.showIcons) {
                image(icon) {
                    width = 8 * scale
                    height = 8 * scale
                }
//                mc.textureManager.bindTexture(image)
//                drawTexturedRect(
//                    x = currentX.toDouble(),
//                    y = y.toDouble(),
//                    u = 0f,
//                    v = 0f,
//                    width = 8 * scale.toDouble(),
//                    height = 8 * scale.toDouble(),
//                    textureWidth = 8f * scale,
//                    textureHeight = 8f * scale
//                )
            }

            if (GhostConfig.showPrefixes) {
                text(prefix.orEmpty()) {
                    color = main.color
                    height = 9f
                }
//                TextRenderer.drawScaledString(prefix, currentX, y, main.color, GhostConfig.shadow, scale)
            }

            text(main.text) {
                color = main.color
                height = 9f
            }
//            TextRenderer.drawScaledString(main.text, currentX, y, main.color, GhostConfig.shadow, scale)
        }
        return true
    }
}
