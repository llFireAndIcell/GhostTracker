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
import me.fireandice.ghosttracker.tracker.GhostTimer
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import me.fireandice.ghosttracker.utils.mc
import java.text.DecimalFormat

class TimerHud : BasicHud(true) {

    @Transient private var lines: ArrayList<String> = ArrayList(6)
    @Transient private var height = 0f
    @Transient private var width = 0f

    @Transient private var exampleLines: ArrayList<String> = ArrayList(6)
    @Transient private var exampleWidth = 0f
    @Transient private var exampleHeight = 0f

    @Transient private val format = DecimalFormat("#,##0.##")

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

    @Transient private var ticks = 0
    @Subscribe
    fun onTick(event: TickEvent) {
        if (event.stage != Stage.START) return
        ticks++
        if (ticks % 10 != 0) return
        ticks = 0

        lines.clear()
        exampleLines.clear()

        val stats = GhostTimer.sessionStats
        val time = GhostTimer.elapsedTime()
        var seconds: Int = (time / 1000f).toInt()

        val killRate =
            if (time == 0.toLong()) "-"
            else format.format(stats.kills.toFloat() / seconds * 3600)
        lines.add("Kills per hour: $killRate")
        exampleLines.add("Kills per hour: 6,000")

        val mf =
            if (stats.mfDropCount == 0) "-"
            else format.format(stats.totalMf.toFloat() / stats.mfDropCount)
        lines.add("Average magic find: $mf")
        exampleLines.add("Average magic find: 215.33")

        val averageXp =
            if (stats.kills <= 0) "-"
            else format.format(stats.totalXp / stats.kills)
        lines.add("Average XP: $averageXp")
        exampleLines.add("Average XP: 183.33")

        val xpRate: String =
            if (time <= 0.toLong()) "-"
            else format.format(stats.totalXp / seconds * 3600)
        lines.add("XP per hour: $xpRate")
        exampleLines.add("XP per hour: 1,100,000")

        lines.add("Total XP: ${format.format(stats.totalXp)}")
        exampleLines.add("Total XP: 1,100,000")

        val timeString = StringBuilder()
        val hours: Int = (seconds / 3600f).toInt()
        seconds %= 3600
        val minutes: Int = (seconds / 60f).toInt()
        seconds %= 60

        if (hours > 0) timeString.append("${hours}h ")
        if (minutes > 0 || hours > 0) timeString.append("${minutes}m ")     // if it's 1h 0m it should still show minutes
        timeString.append("${seconds}s")

        lines.add("Time: $timeString")
        exampleLines.add("Time: 1h 0m 0s")
    }

    init {
        EventManager.INSTANCE.register(this)
    }
}