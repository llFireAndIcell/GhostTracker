package me.fireandice.ghosttracker.hud.elements

import me.fireandice.ghosttracker.config.GhostConfig
import org.polyfrost.polyui.data.PolyImage
import org.polyfrost.polyui.dsl.DrawableDSL
import org.polyfrost.polyui.dsl.polyUI
import org.polyfrost.polyui.unit.Align
import org.polyfrost.polyui.unit.Vec2

/**
 * A hud line that two different colored text components. Used for drops and time hud lines. Note: prefix should
 * include a space after, and suffix should include a space before
 * @param prefix The prefix text. This is not a [ColoredText] because it will always be the same color as [main]
 * @param main The main text that displays the relevant tracker stat
 * @param suffix Text after the main text that may be hidden by the user
 * @param icon The icon that may display before the hud line
 * @param visible A getter to decide of the whole line should be visible
 * @param suffixVisible A getter to decide of the suffix should be visible
 */
class SuffixHudLine(
    var prefix: String? = null,
    var main: ColoredText,
    var suffix: ColoredText,
    private val icon: PolyImage,
    val visible: () -> Boolean,
    val suffixVisible: () -> Boolean
) : HudLine {

    @Suppress("UnstableApiUsage")
    override fun draw(polyUI: DrawableDSL.Master, x: Float, y: Float, scale: Float): Boolean {
        if (!visible()) return false

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
