package me.fireandice.ghosttracker.hud.elements

import me.fireandice.ghosttracker.config.GhostConfig
import org.polyfrost.polyui.data.PolyImage
import org.polyfrost.polyui.dsl.DrawableDSL
import org.polyfrost.polyui.dsl.polyUI
import org.polyfrost.polyui.unit.Align
import org.polyfrost.polyui.unit.Vec2
import kotlin.reflect.KProperty0

/**
 * A hud line that two different colored text components. Used for drops and time hud lines. Note: prefix should
 * include a space after, and suffix should include a space before
 * @param prefix The prefix text. This is not a [ColoredText] because it will always be the same color as [main]
 * @param main The main text that displays the relevant tracker stat
 * @param suffix Text after the main text that may be hidden by the user
 * @param icon The icon that may display before the hud line
 * @param visible The backing property of the config option that decides if the line is shown
 * @param suffixVisible An expression to calculate if the suffix should be shown
 */
class SuffixHudLine(
    var prefix: String? = null,
    var main: ColoredText,
    var suffix: ColoredText,
    private val icon: PolyImage,
    val visible: KProperty0<Boolean>,
    val suffixVisible: () -> Boolean
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

        polyUI {
            group(alignment = Align(pad = Vec2.of(2f, 2f))) {
                if (GhostConfig.showIcons) {
                    image(icon) {
                        width = 8 * scale
                        height = 8 * scale
                    }
                }

                if (GhostConfig.showPrefixes) {
                    text(prefix.orEmpty()) {
                        color = main.color
                        height = 9f
                    }
                }

                text(main.text) {
                    color = main.color
                    height = 9f
                }

                if (suffixVisible()) {
                    text(suffix.text) {
                        color = suffix.color
                        height = 9f
                    }
                }
            }
        }

        return true
    }
}
