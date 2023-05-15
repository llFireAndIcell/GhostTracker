package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.Stage
import cc.polyfrost.oneconfig.events.event.TickEvent
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.renderer.TextRenderer
import me.fireandice.ghosttracker.GhostConfig
import me.fireandice.ghosttracker.tracker.GhostStats
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import me.fireandice.ghosttracker.utils.mc
import java.text.DecimalFormat

class GhostHud : BasicHud(true) {

    private var lines: ArrayList<String> = ArrayList(9)
    private var height = 0f
    private var width = 0f

    init {
        EventManager.INSTANCE.register(this)
    }

    @Color(
        name = "Text Color"
    )
    var color = OneColor(0, 255, 255)

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        if (lines.size == 0) return

        var longestLine = 0f
        var textY = y

        for (line in lines) {
            drawLine(
                line,
                x,
                textY,
                color.rgb,
                scale
            )
            textY += 9 * scale
            longestLine = longestLine.coerceAtLeast(mc.fontRendererObj.getStringWidth(line) * scale)
        }
        width = longestLine
        height = (lines.size * 9 - 1) * scale
    }

    private fun drawLine(text: String, x: Float, y: Float, color: Int, scale: Float) =
        TextRenderer.drawScaledString(text, x, y, color, TextRenderer.TextType.SHADOW, scale)

    override fun getWidth(scale: Float, example: Boolean): Float = width

    override fun getHeight(scale: Float, example: Boolean): Float = height

    override fun shouldShow(): Boolean = isEnabled && ScoreboardUtils.inDwarvenMines

    private var format = DecimalFormat("#,##0.##")
    private var killFormat = DecimalFormat("#,###")
    private var ticks = 0
    @Subscribe
    fun onTick(event: TickEvent) {
        if (event.stage != Stage.START || !shouldShow()) return
        ticks++
        if (ticks % 10 != 0) return

        lines.clear()
        val config = GhostConfig
        if (config.showKills) lines.add("Kills: ${killFormat.format(GhostStats.kills)}")
        if (config.showSorrow) lines.add("Sorrows: ${GhostStats.sorrowCount}")
        if (config.showVolta) lines.add("Voltas: ${GhostStats.voltaCount}")
        if (config.showPlasma) lines.add("Plasmas: ${GhostStats.plasmaCount}")
        if (config.showBoots) lines.add("Ghostly boots: ${GhostStats.bootsCount}")
        if (config.showCoins) lines.add("1m coins: ${GhostStats.coinsCount}")

        if (config.showMf) {
            val mf =
                if (GhostStats.mfDropCount == 0) "-"
                else format.format(GhostStats.totalMf.toFloat() / GhostStats.mfDropCount)
            lines.add("Average magic find: $mf")
        }

        if (config.showAverageXp) {
            val averageXp: String =
                if (GhostStats.kills == 0) "-"
                else format.format(GhostStats.totalXp / GhostStats.kills)
            lines.add("Average XP: $averageXp")
        }

        if (config.showTotalXp) lines.add("Total XP: ${format.format(GhostStats.totalXp)}")
        ticks = 0
    }
}