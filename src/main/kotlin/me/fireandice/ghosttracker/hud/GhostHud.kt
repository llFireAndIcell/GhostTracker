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
        if (example) {
            drawExample(x, y, scale)
            return
        }
        if (lines.size == 0) return

        var longestLine = 0f
        var textY = y

        for (line in lines) {
            TextRenderer.drawScaledString(line, x, textY, color.rgb, TextRenderer.TextType.SHADOW, scale)
            textY += 9 * scale
            longestLine = longestLine.coerceAtLeast(mc.fontRendererObj.getStringWidth(line) * scale)
        }
        width = longestLine
        height = (lines.size * 9 - 1) * scale
    }

    private var exampleLines: ArrayList<String> = ArrayList(9)
    private var exampleWidth = 0f
    private var exampleHeight= 0f

    private fun drawExample(x: Float, y: Float, scale: Float) {
        var textY = y
        var longestLine = 0f
        for (line in exampleLines) {
            TextRenderer.drawScaledString(line, x, textY, color.rgb, TextRenderer.TextType.SHADOW, scale)
            textY += 9 * scale
            longestLine = longestLine.coerceAtLeast(mc.fontRendererObj.getStringWidth(line) * scale)
        }
        exampleWidth = longestLine
        exampleHeight = (exampleLines.size * 9 - 1) * scale
    }

    override fun getWidth(scale: Float, example: Boolean): Float = if (example) exampleWidth else width

    override fun getHeight(scale: Float, example: Boolean): Float = if (example) exampleHeight else height

    override fun shouldShow(): Boolean = isEnabled && ScoreboardUtils.inDwarvenMines

    private var format = DecimalFormat("#,##0.##")
    private var killFormat = DecimalFormat("#,###")
    private var ticks = 0
    @Subscribe
    fun onTick(event: TickEvent) {
        if (event.stage != Stage.START) return
        ticks++
        if (ticks % 10 != 0) return
        ticks = 0

        lines.clear()
        exampleLines.clear()

        val config = GhostConfig
        if (config.showKills) {
            lines.add("Kills: ${killFormat.format(GhostStats.kills)}")
            exampleLines.add("Kills: 1,000")
        }
        if (config.showSorrow) {
            lines.add("Sorrows: ${GhostStats.sorrowCount}")
            exampleLines.add("Sorrows: 100")
        }
        if (config.showVolta) {
            lines.add("Voltas: ${GhostStats.voltaCount}")
            exampleLines.add("Voltas: 200")
        }
        if (config.showPlasma) {
            lines.add("Plasmas: ${GhostStats.plasmaCount}")
            exampleLines.add("Plasmas: 50")
        }
        if (config.showBoots) {
            lines.add("Ghostly boots: ${GhostStats.bootsCount}")
            exampleLines.add("Ghostly boots: 5")
        }
        if (config.showCoins) {
            lines.add("1m coins: ${GhostStats.coinsCount}")
            exampleLines.add("1m coins: 1")
        }

        if (config.showMf) {
            val mf =
                if (GhostStats.mfDropCount == 0) "-"
                else format.format(GhostStats.totalMf.toFloat() / GhostStats.mfDropCount)
            lines.add("Average magic find: $mf")
            exampleLines.add("Average magic find: 152.33")
        }

        if (config.showAverageXp) {
            val averageXp: String =
                if (GhostStats.kills == 0) "-"
                else format.format(GhostStats.totalXp / GhostStats.kills)
            lines.add("Average XP: $averageXp")
            exampleLines.add("Average XP: 231.55")
        }

        if (config.showTotalXp) {
            lines.add("Total XP: ${format.format(GhostStats.totalXp)}")
            exampleLines.add("Total XP: 1,100,000")
        }
    }
}