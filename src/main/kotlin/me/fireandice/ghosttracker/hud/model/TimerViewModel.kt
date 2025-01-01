package me.fireandice.ghosttracker.hud.model

import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.hud.Images
import me.fireandice.ghosttracker.hud.elements.BasicHudLine
import me.fireandice.ghosttracker.hud.elements.HudLine
import me.fireandice.ghosttracker.hud.elements.SuffixHudLine
import me.fireandice.ghosttracker.hud.elements.withColor
import me.fireandice.ghosttracker.tracker.GhostDrops
import me.fireandice.ghosttracker.tracker.GhostTimer
import java.text.DecimalFormat

/**
 * Model class associated with a timer hud. Stores the text that is displayed and whether the data is dirty
 */
data class TimerViewModel(
    val lines: ArrayList<HudLine> = ArrayList(12),
    val exampleLines: ArrayList<HudLine> = ArrayList(12),
    var dirty: Boolean = false
) : Cloneable {

    /**
     * Sets up the text lines
     */
    init {
        val config = GhostConfig
        val stats = GhostTimer.stats
        val millis: Long = GhostTimer.elapsedTime
        var seconds: Int = (millis / 1000).toInt()

        //<editor-fold desc="initializing lines">
        val killRate = if (millis == 0.toLong()) "-"
        else decimalFormat.format(stats.kills.toFloat() / seconds * 3600)
        lines[0] = BasicHudLine(
            "Kills/hr: ",
            (if (config.showPrefixes) killRate else "$killRate/hr") withColor config::killColor,
            Images.Kills,
            config::timer_kills
        )

        val sorrowRate = if (seconds == 0) "-"
        else decimalFormat.format(stats.sorrowCount.toFloat() / seconds * 3600)
        lines[1] = SuffixHudLine(
            "Sorrows/hr: ",
            (if (config.showPrefixes) sorrowRate else "$sorrowRate/hr") withColor config::dropColor,
            " (${stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)})" withColor config::marginColor,
            Images.Sorrow,
            config::timer_sorrow,
        ) { config.showMargins && stats.sorrowCount != 0 }

        val voltaRate = if (seconds == 0) "-"
        else decimalFormat.format(stats.voltaCount.toFloat() / seconds * 3600)
        lines[2] = SuffixHudLine(
            "Voltas/hr: ",
            (if (config.showPrefixes) voltaRate else "$voltaRate/hr") withColor config::dropColor,
            " (${stats.getPercentDifference(GhostDrops.Volta, marginFormat)})" withColor config::marginColor,
            Images.Volta,
            config::timer_volta,
        ) { config.showMargins && stats.voltaCount != 0 }

        val plasmaRate = if (seconds == 0) "-"
        else decimalFormat.format(stats.plasmaCount.toFloat() / seconds * 3600)
        lines[3] = SuffixHudLine(
            "Plasmas/hr: ",
            (if (config.showPrefixes) plasmaRate else "$plasmaRate/hr") withColor config::dropColor,
            " (${stats.getPercentDifference(GhostDrops.Plasma, marginFormat)})" withColor config::marginColor,
            Images.Plasma,
            config::timer_plasma,
        ) { config.showMargins && stats.plasmaCount != 0 }

        val bootsRate = if (seconds == 0) "-"
        else decimalFormat.format(stats.bootsCount.toFloat() / seconds * 3600)
        lines[4] = SuffixHudLine(
            "Ghostly Boots/hr: ",
            (if (config.showPrefixes) bootsRate else "$bootsRate/hr") withColor config::dropColor,
            " (${stats.getPercentDifference(GhostDrops.Boots, marginFormat)})" withColor config::marginColor,
            Images.Boots,
            config::timer_boots,
        ) { config.showMargins && stats.bootsCount != 0 }

        val coinsRate = if (seconds == 0) "-"
        else decimalFormat.format(stats.coinsCount.toFloat() / seconds * 3600)
        lines[5] = SuffixHudLine(
            "1m Coins/hr: ",
            (if (config.showPrefixes) coinsRate else "$coinsRate/hr") withColor config::dropColor,
            " (${stats.getPercentDifference(GhostDrops.Coins, marginFormat)})" withColor config::marginColor,
            Images.Coins,
            config::timer_coins,
        ) { config.showMargins && stats.coinsCount != 0 }

        lines[6] = BasicHudLine(
            "Average MF: ",
            stats.getAverageMf(decimalFormat) withColor config::mfColor,
            Images.MagicFind,
            config::timer_mf
        )

        val averageXp = stats.getAverageXp(decimalFormat)
        lines[7] = BasicHudLine(
            "Average XP: ",
            (if (config.showPrefixes) averageXp else "$averageXp/kill") withColor config::xpColor,
            Images.CombatXp,
            config::timer_averageXp
        )

        val xpRate: String = if (seconds == 0) "-"
        else decimalFormat.format(stats.totalXp / seconds * 3600)
        lines[8] = BasicHudLine(
            "XP/hr: ",
            (if (config.showPrefixes) xpRate else "$xpRate/hr") withColor config::xpColor,
            Images.CombatXp,
            config::timer_xpRate
        )

        val scavRate: String = if (seconds == 0) "-"
        else decimalFormat.format(stats.scavenger / seconds * 3600)
        lines[9] = BasicHudLine(
            "Scavenger/hr: ",
            (if (config.showPrefixes) scavRate else "$scavRate/hr") withColor config::coinColor,
            Images.Coins,
            config::timer_scavenger,
        )

        val moneyRate: String = if (seconds == 0) "-"
        else decimalFormat.format((stats.totalValue) / seconds * 3600)
        lines[10] = BasicHudLine(
            "Coins/hr: ",
            (if (config.showPrefixes) moneyRate else "$moneyRate/hr") withColor config::coinColor,
            Images.Money,
            config::timer_moneyRate
        )

        val hours: Int = (seconds / 3600f).toInt()
        seconds %= 3600
        val minutes: Int = (seconds / 60f).toInt()
        seconds %= 60

        val timeString = buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0 || hours > 0) append("${minutes}m ")    // if it's 1h 0m it should still show minutes
            append("${seconds}s")
        }

        lines[11] = SuffixHudLine(
            "Time: ",
            timeString withColor config::timeColor,
            " (Paused)" withColor config::marginColor,
            Images.Time,
            config::timer_time,
        ) { GhostTimer.isPaused }
        //</editor-fold>

        //<editor-fold desc="initializing example lines">
        exampleLines[0] = BasicHudLine(
            "Kills/hr: ",
            (if (config.showPrefixes) "6,000" else "6,000/hr") withColor config::killColor,
            Images.Kills,
            config::timer_kills
        )
        exampleLines[1] = SuffixHudLine(
            "Sorrows/hr: ",
            (if (config.showPrefixes) "50" else "50/hr") withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Sorrow,
            config::timer_sorrow
        ) { config.showMargins }
        exampleLines[2] = SuffixHudLine(
            "Voltas/hr: ",
            (if (config.showPrefixes) "50" else "50/hr") withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Volta,
            config::timer_volta
        ) { config.showMargins }
        exampleLines[3] = SuffixHudLine(
            "Plasmas/hr: ",
            (if (config.showPrefixes) "50" else "50/hr") withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Plasma,
            config::timer_plasma
        ) { config.showMargins }
        exampleLines[4] = SuffixHudLine(
            "Ghostly Boots/hr: ",
            (if (config.showPrefixes) "50" else "50/hr") withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Boots,
            config::timer_boots
        ) { config.showMargins }
        exampleLines[5] = SuffixHudLine(
            "1m Coins/hr: ",
            (if (config.showPrefixes) "50" else "50/hr") withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Coins,
            config::timer_coins
        ) { config.showMargins }
        exampleLines[6] = BasicHudLine(
            "Average MF: ", "215.33" withColor config::mfColor, Images.MagicFind, config::timer_mf
        )
        exampleLines[7] = BasicHudLine(
            "Average XP: ",
            (if (config.showPrefixes) "183.33" else "183.33/kill") withColor config::xpColor,
            Images.CombatXp,
            config::timer_averageXp
        )
        exampleLines[8] = BasicHudLine(
            "XP/hr: ",
            (if (config.showPrefixes) "1,100,000" else "1,100,000/hr") withColor config::xpColor,
            Images.CombatXp,
            config::timer_xpRate
        )
        exampleLines[9] = BasicHudLine(
            "Scavenger/hr: ",
            (if (config.showPrefixes) "1,000,000" else "1,000,000/hr") withColor config::coinColor,
            Images.Coins,
            config::timer_scavenger,
        )
        exampleLines[10] = BasicHudLine(
            "Coins/hr: ",
            (if (config.showPrefixes) "30,000,000" else "30,000,000/hr") withColor config::coinColor,
            Images.Money,
            config::timer_moneyRate
        )
        exampleLines[11] = SuffixHudLine(
            "Time: ",
            "1h 0m 0s" withColor config::timeColor,
            " (Paused)" withColor config::pauseColor,
            Images.Time,
            config::timer_time
        ) { true }
        //</editor-fold>
    }

    /**
     * Recalculates text only if the model has been marked as dirty. Automatically sets dirty to `false` upon completion
     * @return Whether the data has been updated
     */
    fun update(): Boolean {
        if (!dirty) return false
        val config = GhostConfig
        val stats = GhostTimer.stats
        val millis: Long = GhostTimer.elapsedTime
        var seconds: Int = (millis / 1000f).toInt()

        if (config.timer_kills) {
            val killRate = if (seconds == 0) "-"
            else decimalFormat.format(stats.kills.toFloat() / seconds * 3600)

            (lines[0] as BasicHudLine).main.text = if (config.showPrefixes) killRate else "$killRate/hr"
        }

        if (config.timer_sorrow) {
            val sorrowRate = if (seconds == 0) "-"
            else decimalFormat.format(stats.sorrowCount.toFloat() / seconds * 3600)

            val line = lines[1] as SuffixHudLine
            line.main.text = if (config.showPrefixes) sorrowRate else "$sorrowRate/hr"
            if (line.suffixVisible()) line.suffix.text = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
        }

        if (config.timer_volta) {
            val voltaRate = if (seconds == 0) "-"
            else decimalFormat.format(stats.voltaCount.toFloat() / seconds * 3600)

            val line = lines[2] as SuffixHudLine
            line.main.text = if (config.showPrefixes) voltaRate else "$voltaRate/hr"
            if (line.suffixVisible()) line.suffix.text = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
        }

        if (config.timer_plasma) {
            val plasmaRate = if (seconds == 0) "-"
            else decimalFormat.format(stats.plasmaCount.toFloat() / seconds * 3600)

            val line = lines[3] as SuffixHudLine
            line.main.text = if (config.showPrefixes) plasmaRate else "$plasmaRate/hr"
            if (line.suffixVisible()) line.suffix.text = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
        }

        if (config.timer_boots) {
            val bootsRate = if (seconds == 0) "-"
            else decimalFormat.format(stats.bootsCount.toFloat() / seconds * 3600)

            val line = lines[4] as SuffixHudLine
            line.main.text = if (config.showPrefixes) bootsRate else "$bootsRate/hr"
            if (line.suffixVisible()) line.suffix.text = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
        }

        if (config.timer_coins) {
            val coinsRate = if (seconds == 0) "-"
            else decimalFormat.format(stats.coinsCount.toFloat() / seconds * 3600)

            val line = lines[5] as SuffixHudLine
            line.main.text = if (config.showPrefixes) coinsRate else "$coinsRate/hr"
            if (line.suffixVisible()) line.suffix.text = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
        }

        if (config.timer_mf) (lines[6] as BasicHudLine).main.text = stats.getAverageMf(decimalFormat)

        if (config.timer_averageXp) {
            val averageXp = stats.getAverageXp(decimalFormat)
            (lines[7] as BasicHudLine).main.text = if (config.showPrefixes) averageXp else "$averageXp/kill"
        }

        if (config.timer_xpRate) {
            val xpRate = if (seconds == 0) "-"
            else decimalFormat.format(stats.totalXp / seconds * 3600)
            (lines[8] as BasicHudLine).main.text = if (config.showPrefixes) xpRate else "$xpRate/hr"
        }

        if (config.timer_scavenger) {
            val scavRate = if (seconds == 0) "-"
            else decimalFormat.format(stats.scavenger / seconds * 3600)
            (lines[9] as BasicHudLine).main.text = if (config.showPrefixes) scavRate else "$scavRate/hr"
        }

        if (config.timer_moneyRate) {
            val moneyRate = if (seconds == 0) "-"
            else decimalFormat.format(stats.totalValue / seconds * 3600)
            (lines[10] as BasicHudLine).main.text = if (config.showPrefixes) moneyRate else "$moneyRate/hr"
        }

        if (config.timer_time) {
            val hours: Int = (seconds / 3600f).toInt()
            seconds %= 3600
            val minutes: Int = (seconds / 60f).toInt()
            seconds %= 60

            val timeString = buildString {
                if (hours > 0) append("${hours}h ")
                if (minutes > 0 || hours > 0) append("${minutes}m ")    // if it's 1h 0m it should still show minutes
                append("${seconds}s")
            }

            (lines[11] as SuffixHudLine).main.text = timeString
        }

        dirty = false
        return true
    }

    public override fun clone(): TimerViewModel {
        return TimerViewModel(lines, exampleLines, dirty)
    }

    companion object {
        private val decimalFormat = DecimalFormat("#,##0.##")
        private val marginFormat = DecimalFormat("0.00")
    }
}
