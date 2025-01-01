package me.fireandice.ghosttracker.hud

import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.hud.elements.HudLine
import me.fireandice.ghosttracker.hud.model.TrackerViewModel
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.dsl.polyUI
import org.polyfrost.polyui.unit.Align
import org.polyfrost.universal.UMatrixStack

class TrackerHud : LegacyHud() {

    @Transient
    lateinit var vm: TrackerViewModel

    @Transient
    override var width = 0f

    @Transient
    override var height = 0f

    fun shouldShow(): Boolean {
        return enabled // straight-forward
                && !hidden // also straight-forward
                && (GhostConfig.showEverywhere || ScoreboardUtils.inDwarvenMines) // always show if 'show everywhere' is enabled, always show if in dwarven mines
    }

    override fun title(): String = "Ghost Tracker HUD"
    override fun category(): Category = Category.COMBAT

    override fun create(): Drawable {
        vm = TrackerViewModel()
        return super.create()
    }

    override fun update(): Boolean {
        return vm.update()
    }

    override fun render(stack: UMatrixStack, x: Float, y: Float, scaleX: Float, scaleY: Float) {
        if (!shouldShow()) return
        if (!isReal) return drawLines(vm.exampleLines, x, y)
        drawLines(vm.lines, x, y)
    }

    override fun clone(): Hud<Drawable> {
        return (super.clone() as TrackerHud).also { it.vm = this.vm.clone() }
    }

    private fun drawLines(linesToDraw: ArrayList<HudLine>, x: Float, y: Float) {
        polyUI {
            group(alignment = Align(mode = Align.Mode.Vertical)) {
                for (line in linesToDraw) !line.draw(this@polyUI, x, y, 1f)
            }
        }.also {
            width = it.size.x
            height = it.size.y
        }
    }
}
