package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.GhostConfig
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

    @Transient private var lines: ArrayList<TextComponent> = ArrayList(11)
    @Transient private var indices: HashMap<String, Int> = hashMapOf(
        "showKillsPerHour" to 0,
        "showSorrowsPerHour" to 1,
        "showVoltasPerHour" to 2,
        "showPlasmasPerHour" to 3,
        "showBootsPerHour" to 4,
        "showCoinDropsPerHour" to 5,
        "showTimerMf" to 6,
        "showTimerXp" to 7,
        "showXpPerHour" to 8,
        "showTotalTimerXp" to 9,
        "showTime" to 10
    )
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
        if (example) return drawExample(x, y, scale)

        var longestLine = 0f
        var textY = y
        var drawnLines = 0

        for (line in lines) {
            if (!line.shouldDraw) continue
            line.draw(x, textY, scale)
            drawnLines++
            textY += FONT_HEIGHT * scale
            longestLine = longestLine.coerceAtLeast(line.width)
        }
        width = longestLine * scale
        height = (drawnLines * FONT_HEIGHT - 1) * scale
    }

    private fun drawExample(x: Float, y: Float, scale: Float) {
        var textY = y
        var longestLine = 0f
        var drawnLines = 0

        for (line in exampleLines) {
            if (!line.shouldDraw) continue
            line.draw(x, textY, scale)
            drawnLines++
            textY += FONT_HEIGHT * scale
            longestLine = longestLine.coerceAtLeast(line.width)
        }
        exampleWidth = longestLine * scale
        exampleHeight = (drawnLines * FONT_HEIGHT - 1) * scale
    }

    private fun refreshLines() {
        val config = GhostConfig
        val stats = GhostTimer.stats
        val time: Long = GhostTimer.elapsedTime
        var seconds: Int = (time / 1000f).toInt()

        if (config.showKillsPerHour) {
            val killRate =
                if (time == 0.toLong()) "-"
                else format.format(stats.kills.toFloat() / seconds * 3600)
            lines[0] = SingleColorText("Kills/hr: $killRate", config.killColor.rgb)
        }

        if (config.showSorrowsPerHour) {
            val sorrowRate =
                if (time == 0L) "-"
                else format.format(stats.sorrowCount.toFloat() / seconds * 3600)
            lines[1] = MultiColorText().apply {
                add("Sorrows/hr: $sorrowRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
                if (config.showTimerMargins && stats.sorrowCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            }
        }

        if (config.showVoltasPerHour) {
            val voltaRate =
                if (time == 0L) "-"
                else format.format(stats.voltaCount.toFloat() / seconds * 3600)
            lines[2] = MultiColorText().apply {
                add("Voltas/hr: $voltaRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
                if (config.showTimerMargins && stats.voltaCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            }
        }

        if (config.showPlasmasPerHour) {
            val plasmaRate =
                if (time == 0L) "-"
                else format.format(stats.plasmaCount.toFloat() / seconds * 3600)
            lines[3] = MultiColorText().apply {
                add("Plasmas/hr: $plasmaRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
                if (config.showTimerMargins && stats.plasmaCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            }
        }

        if (config.showBootsPerHour) {
            val bootsRate =
                if (time == 0L) "-"
                else format.format(stats.bootsCount.toFloat() / seconds * 3600)
            lines[4] = MultiColorText().apply {
                add("Ghostly boots/hr: $bootsRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
                if (config.showTimerMargins && stats.bootsCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            }
        }

        if (config.showCoinDropsPerHour) {
            val coinsRate =
                if (time == 0L) "-"
                else format.format(stats.coinsCount.toFloat() / seconds * 3600)
            lines[5] = MultiColorText().apply {
                add("1m coins/hr: $coinsRate", config.dropColor.rgb)
                val diff = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
                if (config.showTimerMargins && stats.coinsCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            }
        }

        if (config.showTimerMf)
            lines[6] = SingleColorText("Average MF: ${stats.getAverageMf(format)}", config.mfColor.rgb)

        if (config.showTimerXp)
            lines[7] = SingleColorText("Average XP: ${stats.getAverageXp(format)}", config.xpColor.rgb)

        if (config.showXpPerHour) {
            val xpRate: String =
                if (time <= 0.toLong()) "-"
                else format.format(stats.totalXp / seconds * 3600)
            lines[8] = SingleColorText("XP/hr: $xpRate", config.xpColor.rgb)
        }

        if (config.showTotalTimerXp)
            lines[9] = SingleColorText("Total XP: ${format.format(stats.totalXp)}", config.xpColor.rgb)

        if (config.showTime) {
            val timeString = StringBuilder()
            val hours: Int = (seconds / 3600f).toInt()
            seconds %= 3600
            val minutes: Int = (seconds / 60f).toInt()
            seconds %= 60

            if (hours > 0) timeString.append("${hours}h ")
            if (minutes > 0 || hours > 0) timeString.append("${minutes}m ")     // if it's 1h 0m it should still show minutes
            timeString.append("${seconds}s")

            lines[10] = MultiColorText().apply {
                add("Time: $timeString", config.timeColor.rgb)
                if (!GhostTimer.isTracking && time > 0) add(" (Paused)", GhostConfig.pauseColor.rgb)
            }
        }
    }

    fun onConfigUpdate(optionName: String, newValue: Boolean) {
        val index = indices[optionName] ?: return
        lines[index].shouldDraw = newValue
        exampleLines[index].shouldDraw = newValue
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) refreshLines()
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)

        val config = GhostConfig
        val stats = GhostTimer.stats
        val time: Long = GhostTimer.elapsedTime
        var seconds: Int = (time / 1000f).toInt()

        //<editor-fold desc="initializing lines">
        val killRate =
            if (time == 0.toLong()) "-"
            else format.format(stats.kills.toFloat() / seconds * 3600)
        lines.add(
            SingleColorText("Kills/hr: $killRate", config.killColor.rgb)
            .apply { shouldDraw = config.showKillsPerHour }
        )

        val sorrowRate =
            if (time == 0L) "-"
            else format.format(stats.sorrowCount.toFloat() / seconds * 3600)
        lines.add(
            MultiColorText().apply {
            add("Sorrows/hr: $sorrowRate", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
            if (config.showTimerMargins && stats.sorrowCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            shouldDraw = config.showSorrowsPerHour
        })

        val voltaRate =
            if (time == 0L) "-"
            else format.format(stats.voltaCount.toFloat() / seconds * 3600)
        lines.add(
            MultiColorText().apply {
            add("Voltas/hr: $voltaRate", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
            if (config.showTimerMargins && stats.voltaCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            shouldDraw = config.showVoltasPerHour
        })

        val plasmaRate =
            if (time == 0L) "-"
            else format.format(stats.plasmaCount.toFloat() / seconds * 3600)
        lines.add(
            MultiColorText().apply {
            add("Plasmas/hr: $plasmaRate", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
            if (config.showTimerMargins && stats.plasmaCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            shouldDraw = config.showPlasmasPerHour
        })

        val bootsRate =
            if (time == 0L) "-"
            else format.format(stats.bootsCount.toFloat() / seconds * 3600)
        lines.add(
            MultiColorText().apply {
            add("Ghostly boots/hr: $bootsRate", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
            if (config.showTimerMargins && stats.bootsCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            shouldDraw = config.showBootsPerHour
        })

        val coinsRate =
            if (time == 0L) "-"
            else format.format(stats.coinsCount.toFloat() / seconds * 3600)
        lines.add(
            MultiColorText().apply {
            add("1m coins/hr: $coinsRate", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
            if (config.showTimerMargins && stats.coinsCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
            shouldDraw = config.showCoinDropsPerHour
        })

        lines.add(
            SingleColorText("Average MF: ${stats.getAverageMf(format)}", config.mfColor.rgb)
                .apply { shouldDraw = config.showTimerMf }
        )

        lines.add(
            SingleColorText("Average XP: ${stats.getAverageXp(format)}", config.xpColor.rgb)
                .apply { shouldDraw = config.showTimerXp }
        )

        val xpRate: String =
            if (time <= 0.toLong()) "-"
            else format.format(stats.totalXp / seconds * 3600)
        lines.add(
            SingleColorText("XP/hr: $xpRate", config.xpColor.rgb)
                .apply { shouldDraw = config.showXpPerHour }
        )

        lines.add(
            SingleColorText("Total XP: ${format.format(stats.totalXp)}", config.xpColor.rgb)
                .apply { shouldDraw = config.showTotalTimerXp }
        )

        val timeString = StringBuilder()
        val hours: Int = (seconds / 3600f).toInt()
        seconds %= 3600
        val minutes: Int = (seconds / 60f).toInt()
        seconds %= 60

        if (hours > 0) timeString.append("${hours}h ")
        if (minutes > 0 || hours > 0) timeString.append("${minutes}m ")     // if it's 1h 0m it should still show minutes
        timeString.append("${seconds}s")

        lines.add(
            MultiColorText().apply {
            add("Time: $timeString", config.timeColor.rgb)
            if (!GhostTimer.isTracking && time > 0) add(" (Paused)", GhostConfig.pauseColor.rgb)
            shouldDraw = config.showTime
        })
        //</editor-fold>

        //<editor-fold desc="initializing example lines">
        exampleLines.add(
            SingleColorText("Kills/hr: 6,000", config.killColor.rgb)
                .apply { shouldDraw = config.showKillsPerHour }
        )
        exampleLines.add(
            MultiColorText().apply {
            add("Sorrows/hr: 50", config.dropColor.rgb)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor.rgb)
            shouldDraw = config.showSorrowsPerHour
        })
        exampleLines.add(MultiColorText().apply {
            add("Voltas/hr: 50", config.dropColor.rgb)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor.rgb)
            shouldDraw = config.showVoltasPerHour
        })
        exampleLines.add(MultiColorText().apply {
            add("Plasmas/hr: 50", config.dropColor.rgb)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor.rgb)
            shouldDraw = config.showPlasmasPerHour
        })
        exampleLines.add(MultiColorText().apply {
            add("Ghostly boots/hr: 50", config.dropColor.rgb)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor.rgb)
            shouldDraw = config.showBootsPerHour
        })
        exampleLines.add(MultiColorText().apply {
            add("1m coins/hr: 50", config.dropColor.rgb)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor.rgb)
            shouldDraw = config.showCoinDropsPerHour
        })
        exampleLines.add(
            SingleColorText("Average MF: 215.33", config.mfColor.rgb)
                .apply { shouldDraw = config.showTimerMf }
        )
        exampleLines.add(
            SingleColorText("Average XP: 183.33", config.xpColor.rgb)
                .apply { shouldDraw = config.showTimerXp }
        )
        exampleLines.add(
            SingleColorText("XP/hr: 1,100,000", config.xpColor.rgb)
                .apply { shouldDraw = config.showXpPerHour }
        )
        exampleLines.add(
            SingleColorText("Total XP: 1,100,000", config.xpColor.rgb)
                .apply { shouldDraw = config.showTotalTimerXp }
        )
        exampleLines.add(
            MultiColorText().apply {
            add("Time: 1h 0m 0s", config.timeColor.rgb)
            add(" (Paused)", config.pauseColor.rgb)
            shouldDraw = config.showTime
        })
        //</editor-fold>
    }
}