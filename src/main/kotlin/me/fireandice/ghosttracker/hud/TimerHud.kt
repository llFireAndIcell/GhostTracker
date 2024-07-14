package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.hud.elements.BasicHudLine
import me.fireandice.ghosttracker.hud.elements.HudLine
import me.fireandice.ghosttracker.hud.elements.SuffixHudLine
import me.fireandice.ghosttracker.hud.elements.withColor
import me.fireandice.ghosttracker.tracker.GhostDrops
import me.fireandice.ghosttracker.tracker.GhostTimer
import me.fireandice.ghosttracker.utils.FONT_HEIGHT
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import me.fireandice.ghosttracker.utils.stringBuilder
import net.minecraftforge.common.MinecraftForge
import java.text.DecimalFormat

class TimerHud : BasicHud(true) {

    @Transient private val lines: ArrayList<HudLine> = ArrayList(12)
    @Transient private val exampleLines: ArrayList<HudLine> = ArrayList(12)

    @Transient private var width = 0f
    @Transient private var height = 0f

    @Transient private val decimalFormat = DecimalFormat("#,##0.##")
    @Transient private val marginFormat = DecimalFormat("0.00")

    init {
        MinecraftForge.EVENT_BUS.register(this)

        val config = GhostConfig
        val stats = GhostTimer.stats
        val millis: Long = GhostTimer.elapsedTime
        var seconds: Int = (millis / 1000).toInt()

        //<editor-fold desc="initializing lines">
        val killRate =
            if (millis == 0.toLong()) "-"
            else decimalFormat.format(stats.kills.toFloat() / seconds * 3600)
        lines += BasicHudLine(
            "Kills/hr: ",
            (if (config.showPrefixes) killRate else "$killRate/hr") withColor config::killColor,
            Images.Kills,
            config::timer_kills
        )

        val sorrowRate =
            if (seconds == 0) "-"
            else decimalFormat.format(stats.sorrowCount.toFloat() / seconds * 3600)
        lines += SuffixHudLine(
            "Sorrows/hr: ",
            (if (config.showPrefixes) sorrowRate else "$sorrowRate/hr") withColor config::dropColor,
            " (${stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)})" withColor config::marginColor,
            Images.Sorrow,
            config::timer_sorrow,
        ) { config.showMargins && stats.sorrowCount != 0 }

        val voltaRate =
            if (seconds == 0) "-"
            else decimalFormat.format(stats.voltaCount.toFloat() / seconds * 3600)
        lines += SuffixHudLine(
            "Voltas/hr: ",
            (if (config.showPrefixes) voltaRate else "$voltaRate/hr") withColor config::dropColor,
            " (${stats.getPercentDifference(GhostDrops.Volta, marginFormat)})" withColor config::marginColor,
            Images.Volta,
            config::timer_volta,
        ) { config.showMargins && stats.voltaCount != 0 }

        val plasmaRate =
            if (seconds == 0) "-"
            else decimalFormat.format(stats.plasmaCount.toFloat() / seconds * 3600)
        lines += SuffixHudLine(
            "Plasmas/hr: ",
            (if (config.showPrefixes) plasmaRate else "$plasmaRate/hr") withColor config::dropColor,
            " (${stats.getPercentDifference(GhostDrops.Plasma, marginFormat)})" withColor config::marginColor,
            Images.Plasma,
            config::timer_plasma,
        ) { config.showMargins && stats.plasmaCount != 0 }

        val bootsRate =
            if (seconds == 0) "-"
            else decimalFormat.format(stats.bootsCount.toFloat() / seconds * 3600)
        lines += SuffixHudLine(
            "Ghostly Boots/hr: ",
            (if (config.showPrefixes) bootsRate else "$bootsRate/hr") withColor config::dropColor,
            " (${stats.getPercentDifference(GhostDrops.Boots, marginFormat)})" withColor config::marginColor,
            Images.Boots,
            config::timer_boots,
        ) { config.showMargins && stats.bootsCount != 0 }

        val coinsRate =
            if (seconds == 0) "-"
            else decimalFormat.format(stats.coinsCount.toFloat() / seconds * 3600)
        lines += SuffixHudLine(
            "1m Coins/hr: ",
            (if (config.showPrefixes) coinsRate else "$coinsRate/hr") withColor config::dropColor,
            " (${stats.getPercentDifference(GhostDrops.Coins, marginFormat)})" withColor config::marginColor,
            Images.Coins,
            config::timer_coins,
        ) { config.showMargins && stats.coinsCount != 0 }

        lines += BasicHudLine(
            "Average MF: ",
            stats.getAverageMf(decimalFormat) withColor config::mfColor,
            Images.MagicFind,
            config::timer_mf
        )

        val averageXp = stats.getAverageXp(decimalFormat)
        lines += BasicHudLine(
            "Average XP: ",
            (if (config.showPrefixes) averageXp else "$averageXp/kill") withColor config::xpColor,
            Images.CombatXp,
            config::timer_averageXp
        )

        val xpRate: String =
            if (seconds == 0) "-"
            else decimalFormat.format(stats.totalXp / seconds * 3600)
        lines += BasicHudLine(
            "XP/hr: ",
            (if (config.showPrefixes) xpRate else "$xpRate/hr") withColor config::xpColor,
            Images.CombatXp,
            config::timer_xpRate
        )

        val scavRate: String =
            if (seconds == 0) "-"
            else decimalFormat.format(stats.scavenger / seconds * 3600)
        lines += BasicHudLine(
            "Scavenger/hr: ",
            (if (config.showPrefixes) scavRate else "$scavRate/hr") withColor config::coinColor,
            Images.Coins,
            config::timer_scavenger,
        )

        val moneyRate: String =
            if (seconds == 0) "-"
            else decimalFormat.format((stats.totalValue) / seconds * 3600)
        lines += BasicHudLine(
            "Coins/hr: ",
            (if (config.showPrefixes) moneyRate else "$moneyRate/hr") withColor config::coinColor,
            Images.Money,
            config::timer_moneyRate
        )

        val hours: Int = (seconds / 3600f).toInt()
        seconds %= 3600
        val minutes: Int = (seconds / 60f).toInt()
        seconds %= 60

        val timeString = stringBuilder {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0 || hours > 0) append("${minutes}m ")    // if it's 1h 0m it should still show minutes
            append("${seconds}s")
        }

        lines += SuffixHudLine(
            "Time: ",
            timeString withColor config::timeColor,
            " (Paused)" withColor config::marginColor,
            Images.Time,
            config::timer_time,
        ) { GhostTimer.isPaused }
        //</editor-fold>

        //<editor-fold desc="initializing example lines">
        exampleLines += BasicHudLine(
            "Kills/hr: ",
            (if (config.showPrefixes) "6,000" else "6,000/hr") withColor config::killColor,
            Images.Kills,
            config::timer_kills
        )
        exampleLines += SuffixHudLine(
            "Sorrows/hr: ",
            (if (config.showPrefixes) "50" else "50/hr") withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Sorrow,
            config::timer_sorrow
        ) { config.showMargins }
        exampleLines += SuffixHudLine(
            "Voltas/hr: ",
            (if (config.showPrefixes) "50" else "50/hr") withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Volta,
            config::timer_volta
        ) { config.showMargins }
        exampleLines += SuffixHudLine(
            "Plasmas/hr: ",
            (if (config.showPrefixes) "50" else "50/hr") withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Plasma,
            config::timer_plasma
        ) { config.showMargins }
        exampleLines += SuffixHudLine(
            "Ghostly Boots/hr: ",
            (if (config.showPrefixes) "50" else "50/hr") withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Boots,
            config::timer_boots
        ) { config.showMargins }
        exampleLines += SuffixHudLine(
            "1m Coins/hr: ",
            (if (config.showPrefixes) "50" else "50/hr") withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Coins,
            config::timer_coins
        ) { config.showMargins }
        exampleLines += BasicHudLine(
            "Average MF: ",
            "215.33" withColor config::mfColor,
            Images.MagicFind,
            config::timer_mf
        )
        exampleLines += BasicHudLine(
            "Average XP: ",
            (if (config.showPrefixes) "183.33" else "183.33/kill") withColor config::xpColor,
            Images.CombatXp,
            config::timer_averageXp
        )
        exampleLines += BasicHudLine(
            "XP/hr: ",
            (if (config.showPrefixes) "1,100,000" else "1,100,000/hr") withColor config::xpColor,
            Images.CombatXp,
            config::timer_xpRate
        )
        exampleLines += BasicHudLine(
            "Scavenger/hr: ",
            (if (config.showPrefixes) "1,000,000" else "1,000,000/hr") withColor config::coinColor,
            Images.Coins,
            config::timer_scavenger,
        )
        exampleLines += BasicHudLine(
            "Coins/hr: ",
            (if (config.showPrefixes) "30,000,000" else "30,000,000/hr") withColor config::coinColor,
            Images.Money,
            config::timer_moneyRate
        )
        exampleLines += SuffixHudLine(
            "Time: ",
            "1h 0m 0s" withColor config::timeColor,
            " (Paused)" withColor config::pauseColor,
            Images.Time,
            config::timer_time
        ) { true }
        //</editor-fold>
    }

    override fun getWidth(scale: Float, example: Boolean): Float = width
    override fun getHeight(scale: Float, example: Boolean): Float = height

    override fun shouldShow(): Boolean = isEnabled && (GhostConfig.showEverywhere || ScoreboardUtils.inDwarvenMines)

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        if (example) return drawLines(exampleLines, x, y, scale)
        drawLines(lines, x, y, scale)
    }

    private fun drawLines(linesToDraw: ArrayList<HudLine>, x: Float, y: Float, scale: Float) {
        var drawnLines = 0
        var textY = y
        var longestLine = 0f

        for (line in linesToDraw) {
            if (!line.draw(x, textY, scale)) continue
            drawnLines++
            textY += FONT_HEIGHT * scale
            longestLine = longestLine.coerceAtLeast(line.width)
        }
        height = (drawnLines * FONT_HEIGHT - 1) * scale
        width = longestLine * scale
    }

    /**
     * Called in `EventListener.onTickStart()`
     */
    fun refreshLines() {
        val config = GhostConfig
        val stats = GhostTimer.stats
        val millis: Long = GhostTimer.elapsedTime
        var seconds: Int = (millis / 1000f).toInt()

        if (config.timer_kills) {
            val killRate =
                if (seconds == 0) "-"
                else decimalFormat.format(stats.kills.toFloat() / seconds * 3600)

            (lines[0] as BasicHudLine).main.text = if (config.showPrefixes) killRate else "$killRate/hr"
        }

        if (config.timer_sorrow) {
            val sorrowRate =
                if (seconds == 0) "-"
                else decimalFormat.format(stats.sorrowCount.toFloat() / seconds * 3600)

            val line = lines[1] as SuffixHudLine
            line.main.text = if (config.showPrefixes) sorrowRate else "$sorrowRate/hr"
            if (line.suffixVisible())
                line.suffix.text = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
        }

        if (config.timer_volta) {
            val voltaRate =
                if (seconds == 0) "-"
                else decimalFormat.format(stats.voltaCount.toFloat() / seconds * 3600)

            val line = lines[2] as SuffixHudLine
            line.main.text = if (config.showPrefixes) voltaRate else "$voltaRate/hr"
            if (line.suffixVisible())
                line.suffix.text = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
        }

        if (config.timer_plasma) {
            val plasmaRate =
                if (seconds == 0) "-"
                else decimalFormat.format(stats.plasmaCount.toFloat() / seconds * 3600)

            val line = lines[3] as SuffixHudLine
            line.main.text = if (config.showPrefixes) plasmaRate else "$plasmaRate/hr"
            if (line.suffixVisible())
                line.suffix.text = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
        }

        if (config.timer_boots) {
            val bootsRate =
                if (seconds == 0) "-"
                else decimalFormat.format(stats.bootsCount.toFloat() / seconds * 3600)

            val line = lines[4] as SuffixHudLine
            line.main.text = if (config.showPrefixes) bootsRate else "$bootsRate/hr"
            if (line.suffixVisible())
                line.suffix.text = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
        }

        if (config.timer_coins) {
            val coinsRate =
                if (seconds == 0) "-"
                else decimalFormat.format(stats.coinsCount.toFloat() / seconds * 3600)
            
            val line = lines[5] as SuffixHudLine
            line.main.text = if (config.showPrefixes) coinsRate else "$coinsRate/hr"
            if (line.suffixVisible())
                line.suffix.text = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
        }

        if (config.timer_mf)
            (lines[6] as BasicHudLine).main.text = stats.getAverageMf(decimalFormat)

        if (config.timer_averageXp) {
            val averageXp = stats.getAverageXp(decimalFormat)
            (lines[7] as BasicHudLine).main.text = if (config.showPrefixes) averageXp else "$averageXp/kill"
        }

        if (config.timer_xpRate) {
            val xpRate =
                if (seconds == 0) "-"
                else decimalFormat.format(stats.totalXp / seconds * 3600)
            (lines[8] as BasicHudLine).main.text = if (config.showPrefixes) xpRate else "$xpRate/hr"
        }

        if (config.timer_scavenger) {
            val scavRate =
                if (seconds == 0) "-"
                else decimalFormat.format(stats.scavenger / seconds * 3600)
            (lines[9] as BasicHudLine).main.text = if (config.showPrefixes) scavRate else "$scavRate/hr"
        }

        if (config.timer_moneyRate) {
            val moneyRate =
                if (seconds == 0) "-"
                else decimalFormat.format(stats.totalValue / seconds * 3600)
            (lines[10] as BasicHudLine).main.text = if (config.showPrefixes) moneyRate else "$moneyRate/hr"
        }

        if (config.timer_time) {
            val hours: Int = (seconds / 3600f).toInt()
            seconds %= 3600
            val minutes: Int = (seconds / 60f).toInt()
            seconds %= 60

            val timeString = stringBuilder {
                if (hours > 0) append("${hours}h ")
                if (minutes > 0 || hours > 0) append("${minutes}m ")    // if it's 1h 0m it should still show minutes
                append("${seconds}s")
            }

            (lines[11] as SuffixHudLine).main.text = timeString
        }
    }
}
