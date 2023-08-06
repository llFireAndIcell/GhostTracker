package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.hud.elements.MultiColorText
import me.fireandice.ghosttracker.hud.elements.SingleColorText
import me.fireandice.ghosttracker.hud.elements.TextComponent
import me.fireandice.ghosttracker.tracker.GhostDrops
import me.fireandice.ghosttracker.tracker.GhostTimer
import me.fireandice.ghosttracker.utils.FONT_HEIGHT
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.text.DecimalFormat

class TimerHud : BasicHud(true) {

    @Transient private val lines: ArrayList<TextComponent> = ArrayList(11)
    @Transient private val exampleLines: ArrayList<TextComponent> = ArrayList(11)

    @Transient private var width = 0f
    @Transient private var height = 0f

    @Transient private val format = DecimalFormat("#,##0.##")
    @Transient private val marginFormat = DecimalFormat("0.00")

    override fun getWidth(scale: Float, example: Boolean): Float = width
    override fun getHeight(scale: Float, example: Boolean): Float = height
    override fun shouldShow(): Boolean = isEnabled && (GhostConfig.showEverywhere || ScoreboardUtils.inDwarvenMines)

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        if (example) return drawLines(exampleLines, x, y, scale)
        drawLines(lines, x, y, scale)
    }

    private fun drawLines(linesToDraw: ArrayList<TextComponent>, x: Float, y: Float, scale: Float) {
        var drawnLines = 0
        var textY = y
        var longestLine = 0f

        for (line in linesToDraw) {
            if (!line.shouldDraw) continue
            line.draw(x, textY, scale)
            drawnLines++
            textY += FONT_HEIGHT * scale
            longestLine = longestLine.coerceAtLeast(line.width)
        }
        height = (drawnLines * FONT_HEIGHT - 1) * scale
        width = longestLine * scale
    }

    private fun refreshLines() {
        val config = GhostConfig
        val stats = GhostTimer.stats
        val millis: Long = GhostTimer.elapsedTime
        var seconds: Int = (millis / 1000f).toInt()

        if (config.timer_kills) {
            val killRate =
                if (millis == 0.toLong()) "-"
                else format.format(stats.kills.toFloat() / seconds * 3600)
            (lines[0] as SingleColorText).set("Kills/hr: $killRate", config.killColor.rgb)
        }

        if (config.timer_sorrow) {
            val sorrowRate =
                if (millis == 0L) "-"
                else format.format(stats.sorrowCount.toFloat() / seconds * 3600)
            (lines[1] as MultiColorText).apply {
                set(0, "Sorrows/hr: $sorrowRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
                if (config.timer_margins && stats.sorrowCount != 0) set(1, " ($diff)", config.marginColor.rgb)
            }
        }

        if (config.timer_volta) {
            val voltaRate =
                if (millis == 0L) "-"
                else format.format(stats.voltaCount.toFloat() / seconds * 3600)
            (lines[2] as MultiColorText).apply {
                set(0, "Voltas/hr: $voltaRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
                if (config.timer_margins && stats.voltaCount != 0) set(1, " ($diff)", config.marginColor.rgb)
            }
        }

        if (config.timer_plasma) {
            val plasmaRate =
                if (millis == 0L) "-"
                else format.format(stats.plasmaCount.toFloat() / seconds * 3600)
            (lines[3] as MultiColorText).apply {
                set(0, "Plasmas/hr: $plasmaRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
                if (config.timer_margins && stats.plasmaCount != 0) set(1, " ($diff)", config.marginColor.rgb)
            }
        }

        if (config.timer_boots) {
            val bootsRate =
                if (millis == 0L) "-"
                else format.format(stats.bootsCount.toFloat() / seconds * 3600)
            (lines[4] as MultiColorText).apply {
                set(0, "Ghostly boots/hr: $bootsRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
                if (config.timer_margins && stats.bootsCount != 0) set(1, " ($diff)", config.marginColor.rgb)
            }
        }

        if (config.timer_coins) {
            val coinsRate =
                if (millis == 0L) "-"
                else format.format(stats.coinsCount.toFloat() / seconds * 3600)
            (lines[5] as MultiColorText).apply {
                set(0, "1m coins/hr: $coinsRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
                if (config.timer_margins && stats.coinsCount != 0) set(1, " ($diff)", config.marginColor.rgb)
            }
        }

        if (config.timer_mf)
            (lines[6] as SingleColorText).set("Average MF: ${stats.getAverageMf(format)}", config.mfColor.rgb)

        if (config.timer_averageXp)
            (lines[7] as SingleColorText).set("Average XP: ${stats.getAverageXp(format)}", config.xpColor.rgb)

        if (config.timer_xpRate) {
            val xpRate: String =
                if (millis <= 0.toLong()) "-"
                else format.format(stats.totalXp / seconds * 3600)
            (lines[8] as SingleColorText).set("XP/hr: $xpRate", config.xpColor.rgb)
        }

        if (config.timer_totalXp)
            (lines[9] as SingleColorText).set("Total XP: ${format.format(stats.totalXp)}", config.xpColor.rgb)

        if (config.timer_time) {
            val timeString = StringBuilder()
            val hours: Int = (seconds / 3600f).toInt()
            seconds %= 3600
            val minutes: Int = (seconds / 60f).toInt()
            seconds %= 60

            if (hours > 0) timeString.append("${hours}h ")
            if (minutes > 0 || hours > 0) timeString.append("${minutes}m ")     // if it's 1h 0m it should still show minutes
            timeString.append("${seconds}s")

            (lines[10] as MultiColorText).apply {
                set(0, "Time: $timeString", config.timeColor.rgb)
                set(1, " (Paused)", GhostConfig.pauseColor.rgb)
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) refreshLines()
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)

        val config = GhostConfig
        val stats = GhostTimer.stats
        val millis: Long = GhostTimer.elapsedTime
        var seconds: Int = (millis / 1000f).toInt()

        //<editor-fold desc="initializing lines">
        val killRate =
            if (millis == 0.toLong()) "-"
            else format.format(stats.kills.toFloat() / seconds * 3600)
        lines.add(SingleColorText("Kills/hr: $killRate", config.killColor.rgb) { config.timer_kills })

        val sorrowRate =
            if (millis == 0L) "-"
            else format.format(stats.sorrowCount.toFloat() / seconds * 3600)
        lines.add(MultiColorText().apply {
            add("Sorrows/hr: $sorrowRate", config.dropColor.rgb) { config.timer_sorrow }
            val diff = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
            add(" ($diff)", config.marginColor.rgb) { config.timer_margins && stats.sorrowCount != 0 }
        })

        val voltaRate =
            if (millis == 0L) "-"
            else format.format(stats.voltaCount.toFloat() / seconds * 3600)
        lines.add(MultiColorText().apply {
            add("Voltas/hr: $voltaRate", config.dropColor.rgb) { config.timer_volta }
            val diff = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
            add(" ($diff)", config.marginColor.rgb) { config.timer_margins && stats.voltaCount != 0 }
        })

        val plasmaRate =
            if (millis == 0L) "-"
            else format.format(stats.plasmaCount.toFloat() / seconds * 3600)
        lines.add(MultiColorText().apply {
            add("Plasmas/hr: $plasmaRate", config.dropColor.rgb) { config.timer_plasma }
            val diff = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
            add(" ($diff)", config.marginColor.rgb) { config.timer_margins && stats.plasmaCount != 0 }
        })

        val bootsRate =
            if (millis == 0L) "-"
            else format.format(stats.bootsCount.toFloat() / seconds * 3600)
        lines.add(MultiColorText().apply {
            add("Ghostly boots/hr: $bootsRate", config.dropColor.rgb) { config.timer_boots }
            val diff = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
            add(" ($diff)", config.marginColor.rgb) { config.timer_margins && stats.bootsCount != 0 }
        })

        val coinsRate =
            if (millis == 0L) "-"
            else format.format(stats.coinsCount.toFloat() / seconds * 3600)
        lines.add(MultiColorText().apply {
            add("1m coins/hr: $coinsRate", config.dropColor.rgb) { config.timer_coins }
            val diff = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
            add(" ($diff)", config.marginColor.rgb) { config.timer_margins && stats.coinsCount != 0 }
        })

        lines.add(SingleColorText("Average MF: ${stats.getAverageMf(format)}", config.mfColor.rgb) { config.timer_mf })

        lines.add(SingleColorText("Average XP: ${stats.getAverageXp(format)}", config.xpColor.rgb) { config.timer_averageXp })

        val xpRate: String =
            if (millis <= 0.toLong()) "-"
            else format.format(stats.totalXp / seconds * 3600)
        lines.add(SingleColorText("XP/hr: $xpRate", config.xpColor.rgb) { config.timer_xpRate })

        lines.add(SingleColorText("Total XP: ${format.format(stats.totalXp)}", config.xpColor.rgb) { config.timer_totalXp })

        val timeString = StringBuilder()
        val hours: Int = (seconds / 3600f).toInt()
        seconds %= 3600
        val minutes: Int = (seconds / 60f).toInt()
        seconds %= 60

        if (hours > 0) timeString.append("${hours}h ")
        if (minutes > 0 || hours > 0) timeString.append("${minutes}m ")     // if it's 1h 0m it should still show minutes
        timeString.append("${seconds}s")

        lines.add(MultiColorText().apply {
            add("Time: $timeString", config.timeColor.rgb) { config.timer_time }
            add(" (Paused)", GhostConfig.pauseColor.rgb) { GhostTimer.isPaused }
        })
        //</editor-fold>

        //<editor-fold desc="initializing example lines">
        exampleLines.add(
            SingleColorText("Kills/hr: 6,000", config.killColor.rgb) { config.timer_kills }
        )
        exampleLines.add(MultiColorText().apply {
            add("Sorrows/hr: 50", config.dropColor.rgb) { config.timer_sorrow }
            add(" (+0.50%)", config.marginColor.rgb) { config.timer_margins }
        })
        exampleLines.add(MultiColorText().apply {
            add("Voltas/hr: 50", config.dropColor.rgb) { config.timer_volta }
            add(" (+0.50%)", config.marginColor.rgb) { config.timer_margins }
        })
        exampleLines.add(MultiColorText().apply {
            add("Plasmas/hr: 50", config.dropColor.rgb) { config.timer_plasma }
            add(" (+0.50%)", config.marginColor.rgb) { config.timer_margins }
        })
        exampleLines.add(MultiColorText().apply {
            add("Ghostly boots/hr: 50", config.dropColor.rgb) { config.timer_boots }
            add(" (+0.50%)", config.marginColor.rgb) { config.timer_boots }
        })
        exampleLines.add(MultiColorText().apply {
            add("1m coins/hr: 50", config.dropColor.rgb) { config.timer_coins }
            add(" (+0.50%)", config.marginColor.rgb) { config.timer_boots }
        })
        exampleLines.add(
            SingleColorText("Average MF: 215.33", config.mfColor.rgb) { config.timer_mf }
        )
        exampleLines.add(
            SingleColorText("Average XP: 183.33", config.xpColor.rgb) { config.timer_averageXp }
        )
        exampleLines.add(
            SingleColorText("XP/hr: 1,100,000", config.xpColor.rgb) { config.timer_xpRate }
        )
        exampleLines.add(
            SingleColorText("Total XP: 1,100,000", config.xpColor.rgb) { config.timer_totalXp }
        )
        exampleLines.add(MultiColorText().apply {
            add("Time: 1h 0m 0s", config.timeColor.rgb) { config.timer_time }
            add(" (Paused)", config.pauseColor.rgb) { true }
        })
        //</editor-fold>
    }
}