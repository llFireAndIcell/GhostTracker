package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.GhostConfig
import me.fireandice.ghosttracker.tracker.GhostDrops
import me.fireandice.ghosttracker.tracker.GhostTimer
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.text.DecimalFormat

class TimerHud : BasicHud(true) {

    @Transient private var lines: ArrayList<TextComponent> = ArrayList(11)
    @Transient private var width = 0f
    @Transient private var height = 0f

    @Transient private var exampleLines: ArrayList<TextComponent> = ArrayList(11)
    @Transient private var exampleWidth = 0f
    @Transient private var exampleHeight = 0f

    @Transient private val format = DecimalFormat("#,##0.##")
    @Transient private val marginFormat = DecimalFormat("0.00")

    override fun getWidth(scale: Float, example: Boolean): Float = if (example) exampleWidth else width

    override fun getHeight(scale: Float, example: Boolean): Float = if (example) exampleHeight else height

    override fun shouldShow(): Boolean = isEnabled && (GhostConfig.showEverywhere || ScoreboardUtils.inDwarvenMines)

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        if (example) {
            refreshExampleLines()
            if (exampleLines.isEmpty()) return
            drawExample(x, y, scale)
            return
        }
        if (lines.isEmpty()) return

        var longestLine = 0f
        var textY = y

        for (line in lines) {
            line.draw(x, textY, scale)
            textY += 9 * scale
            longestLine = longestLine.coerceAtLeast(line.width)
        }
        width = longestLine
        height = (lines.size * 9 - 1) * scale
    }

    private fun drawExample(x: Float, y: Float, scale: Float) {
        var textY = y
        var longestLine = 0f
        for (line in exampleLines) {
            line.draw(x, textY, scale)
            textY += 9 * scale
            longestLine = longestLine.coerceAtLeast(line.width)
        }
        exampleWidth = longestLine
        exampleHeight = (exampleLines.size * 9 - 1) * scale
    }

    private fun refreshLines() {
        lines.clear()

        val config = GhostConfig
        val stats = GhostTimer.stats
        val time: Long = GhostTimer.elapsedTime
        var seconds: Int = (time / 1000f).toInt()

        if (config.showKillsPerHour) {
            val killRate =
                if (time == 0.toLong()) "-"
                else format.format(stats.kills.toFloat() / seconds * 3600)
            lines.add(SingleColorText("Kills per hour: $killRate", config.killColor.rgb))
        }

        if (config.showSorrowsPerHour) {
            val sorrowRate =
                if (time == 0L) "-"
                else format.format(stats.sorrowCount.toFloat() / seconds * 3600)
            lines.add(MultiColorText().apply {
                add("Sorrows per hour: $sorrowRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
                if (config.showTimerMargins && stats.sorrowCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            })
        }

        if (config.showVoltasPerHour) {
            val voltaRate =
                if (time == 0L) "-"
                else format.format(stats.voltaCount.toFloat() / seconds * 3600)
            lines.add(MultiColorText().apply {
                add("Voltas per hour: $voltaRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
                if (config.showTimerMargins && stats.voltaCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            })
        }

        if (config.showPlasmasPerHour) {
            val plasmaRate =
                if (time == 0L) "-"
                else format.format(stats.plasmaCount.toFloat() / seconds * 3600)
            lines.add(MultiColorText().apply {
                add("Plasmas per hour: $plasmaRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
                if (config.showTimerMargins && stats.plasmaCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            })
        }

        if (config.showBootsPerHour) {
            val bootsRate =
                if (time == 0L) "-"
                else format.format(stats.bootsCount.toFloat() / seconds * 3600)
            lines.add(MultiColorText().apply {
                add("Ghostly boots per hour: $bootsRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
                if (config.showTimerMargins && stats.bootsCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            })
        }

        if (config.showCoinDropsPerHour) {
            val coinsRate =
                if (time == 0L) "-"
                else format.format(stats.coinsCount.toFloat() / seconds * 3600)
            lines.add(MultiColorText().apply {
                add("1m coins per hour: $coinsRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
                if (config.showTimerMargins && stats.coinsCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            })
        }

        if (config.showTimerMf)
            lines.add(SingleColorText("Average MF: ${stats.getAverageMf(format)}", config.mfColor.rgb))

        if (config.showTimerXp)
            lines.add(SingleColorText("Average XP: ${stats.getAverageXp(format)}", config.xpColor.rgb))

        if (config.showXpPerHour) {
            val xpRate: String =
                if (time <= 0.toLong()) "-"
                else format.format(stats.totalXp / seconds * 3600)
            lines.add(SingleColorText("XP per hour: $xpRate", config.xpColor.rgb))
        }

        if (config.showTotalTimerXp)
            lines.add(SingleColorText("Total XP: ${format.format(stats.totalXp)}", config.xpColor.rgb))

        if (config.showTime) {
            val timeString = StringBuilder()
            val hours: Int = (seconds / 3600f).toInt()
            seconds %= 3600
            val minutes: Int = (seconds / 60f).toInt()
            seconds %= 60

            if (hours > 0) timeString.append("${hours}h ")
            if (minutes > 0 || hours > 0) timeString.append("${minutes}m ")     // if it's 1h 0m it should still show minutes
            timeString.append("${seconds}s")

            lines.add(MultiColorText().apply {
                add("Time: $timeString", config.timeColor.rgb)
                if (!GhostTimer.isTracking && time > 0) add(" (Paused)", GhostConfig.pauseColor.rgb)
            })
        }
    }

    private fun refreshExampleLines() {
        exampleLines.clear()

        val config = GhostConfig

        if (config.showKillsPerHour)
            exampleLines.add(SingleColorText("Kills per hour: 6,000", config.killColor.rgb))

        if (config.showSorrowsPerHour) exampleLines.add(MultiColorText().apply {
            add("Sorrows per hour: 50", config.dropColor.rgb)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor.rgb)
        })

        if (config.showVoltasPerHour) exampleLines.add(MultiColorText().apply {
            add("Voltas per hour: 50", config.dropColor.rgb)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor.rgb)
        })

        if (config.showPlasmasPerHour) exampleLines.add(MultiColorText().apply {
            add("Plasmas per hour: 50", config.dropColor.rgb)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor.rgb)
        })

        if (config.showBootsPerHour) exampleLines.add(MultiColorText().apply {
            add("Ghostly boots per hour: 50", config.dropColor.rgb)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor.rgb)
        })

        if (config.showCoinDropsPerHour) exampleLines.add(MultiColorText().apply {
            add("1m coins per hour: 50", config.dropColor.rgb)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor.rgb)
        })

        if (config.showTimerMf) exampleLines.add(SingleColorText("Average MF: 215.33", config.mfColor.rgb))

        if (config.showTimerXp) exampleLines.add(SingleColorText("Average XP: 183.33", config.xpColor.rgb))

        if (config.showXpPerHour) exampleLines.add(SingleColorText("XP per hour: 1,100,000", config.xpColor.rgb))

        if (config.showTotalTimerXp) exampleLines.add(SingleColorText("Total XP: 1,100,000", config.xpColor.rgb))

        if (config.showTime) exampleLines.add(MultiColorText().apply {
            add("Time: 1h 0m 0s", config.timeColor.rgb)
            add(" (Paused)", config.pauseColor.rgb)
        })
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        refreshLines()
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }
}