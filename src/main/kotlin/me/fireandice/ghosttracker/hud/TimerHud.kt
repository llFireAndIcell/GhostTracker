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

    private var lines: ArrayList<String> = ArrayList(6)
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

    private fun drawLine(text: String, x: Float, y: Float, color: Int, scale: Float) {
        TextRenderer.drawScaledString(text, x, y, color, TextRenderer.TextType.SHADOW, scale)
    }

    override fun getWidth(scale: Float, example: Boolean): Float = width * scale

    override fun getHeight(scale: Float, example: Boolean): Float = height

    override fun shouldShow(): Boolean =
        isEnabled && ScoreboardUtils.inDwarvenMines && (GhostTimer.isTracking || GhostTimer.isPaused)

    private val format = DecimalFormat("#,##0.##")
    private var ticks = 0
    @Subscribe
    fun onTick(event: TickEvent) {
        if (event.stage != Stage.START || !shouldShow()) return
        ticks++
        if (ticks % 10 != 0) return

        lines.clear()
        val stats = GhostTimer.sessionStats
        val time = GhostTimer.elapsedTime()
        var seconds: Int = (time / 1000f).toInt()

        val killRate =
            if (time == 0.toLong()) "-"
            else format.format(stats.kills.toFloat() / seconds * 3600)
        lines.add("Kills per hour: $killRate")

        val mf =
            if (stats.mfDropCount == 0) "-"
            else format.format(stats.totalMf.toFloat() / stats.mfDropCount)
        lines.add("Average magic find: $mf")

        val averageXp =
            if (stats.kills <= 0) "-"
            else format.format(stats.totalXp / stats.kills)
        lines.add("Average XP: $averageXp")

        val xpRate: String =
            if (time <= 0.toLong()) "-"
            else format.format(stats.totalXp / seconds * 3600)
        lines.add("XP per hour: $xpRate")

        lines.add("Total XP: ${format.format(stats.totalXp)}")

        val timeString = StringBuilder()
        val hours: Int = (seconds / 3600f).toInt()
        seconds %= 3600
        val minutes: Int = (seconds / 60f).toInt()
        seconds %= 60

        if (hours > 0) timeString.append("${hours}h ")
        if (minutes > 0 || hours > 0) timeString.append("${minutes}m ")     // if it's 1h 0m it should still show minutes
        timeString.append("${seconds}s")

        lines.add("Time: $timeString")
        ticks = 0
    }
}