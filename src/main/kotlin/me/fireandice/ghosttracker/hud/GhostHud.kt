package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.hud.elements.BasicHudLine
import me.fireandice.ghosttracker.hud.elements.HudLine
import me.fireandice.ghosttracker.hud.elements.SuffixHudLine
import me.fireandice.ghosttracker.hud.elements.with
import me.fireandice.ghosttracker.tracker.GhostDrops
import me.fireandice.ghosttracker.utils.FONT_HEIGHT
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import net.minecraftforge.common.MinecraftForge
import java.text.DecimalFormat

class GhostHud : BasicHud(true) {

    @Transient private var lines: ArrayList<HudLine> = ArrayList(10)
    @Transient private var exampleLines: ArrayList<HudLine> = ArrayList(10)

    @Transient private var width = 0f
    @Transient private var height = 0f

    @Transient private val intFormat = DecimalFormat("#,###")
    @Transient private val decimalFormat = DecimalFormat("#,##0.##")
    @Transient private val marginFormat = DecimalFormat("0.00")

    init {
        MinecraftForge.EVENT_BUS.register(this)

        val config = GhostConfig
        val stats = GhostTracker.ghostStats

        //<editor-fold desc="initializing lines">
        lines += BasicHudLine(
            "Kills: ",
            intFormat.format(stats.kills) with config::killColor,
            Images.Kills,
            config::tracker_kills
        )
        lines += SuffixHudLine(
            "Sorrows: ",
            intFormat.format(stats.sorrowCount) with config::dropColor,
            stats.getPercentDifference(GhostDrops.Sorrow, marginFormat) with config::marginColor,
            Images.Sorrow,
            config::tracker_sorrow
        ) { config.showMargins && stats.sorrowCount != 0 }
        lines += SuffixHudLine(
            "Voltas: ",
            intFormat.format(stats.voltaCount) with config::dropColor,
            stats.getPercentDifference(GhostDrops.Volta, marginFormat) with config::marginColor,
            Images.Volta,
            config::tracker_volta
        ) { config.showMargins && stats.voltaCount != 0 }
        lines += SuffixHudLine(
            "Plasmas: ",
            intFormat.format(stats.plasmaCount) with config::dropColor,
            stats.getPercentDifference(GhostDrops.Plasma, marginFormat) with config::marginColor,
            Images.Plasma,
            config::tracker_plasma
        ) { config.showMargins && stats.plasmaCount != 0 }
        lines += SuffixHudLine(
            "Ghostly Boots: ",
            intFormat.format(stats.bootsCount) with config::dropColor,
            stats.getPercentDifference(GhostDrops.Boots, marginFormat) with config::marginColor,
            Images.Boots,
            config::tracker_boots
        ) { config.showMargins && stats.bootsCount != 0 }
        lines += SuffixHudLine(
            "1m Coins: ",
            intFormat.format(stats.coinsCount) with config::dropColor,
            stats.getPercentDifference(GhostDrops.Coins, marginFormat) with config::marginColor,
            Images.Coins,
            config::tracker_coins
        ) { config.showMargins && stats.coinsCount != 0 }
        lines += BasicHudLine(
            "Average MF: ",
            stats.getAverageMf(decimalFormat) with config::mfColor,
            Images.MagicFind,
            config::tracker_mf
        )
        val averageXp = stats.getAverageXp(decimalFormat)
        lines += BasicHudLine(
            "Average XP: ",
            (if (config.showPrefixes) averageXp else "$averageXp/kill") with config::xpColor,
            Images.CombatXp,
            config::tracker_averageXp
        )
        lines += BasicHudLine(
            "Total XP: ",
            decimalFormat.format(stats.totalXp) with config::xpColor,
            Images.CombatXp,
            config::tracker_totalXp
        )
        lines += BasicHudLine(
            "Total Coins: ",
            intFormat.format(stats.totalValue) with config::coinColor,
            Images.Coins, // TODO get better coin image (like a furfsky icon or smth)
            config::tracker_totalMoney
        )
        //</editor-fold>

        //<editor-fold desc="initializing example lines">
        exampleLines += BasicHudLine(
            "Kills: ",
            "1,000" with config::killColor,
            Images.Kills,
            config::tracker_kills
        )
        exampleLines += SuffixHudLine(
            "Sorrows: ",
            "100" with config::dropColor,
            " (+0.50%)" with config::marginColor,
            Images.Sorrow,
            config::tracker_sorrow
        ) { config.showMargins }
        exampleLines += SuffixHudLine(
            "Voltas: ",
            "200" with config::dropColor,
            " (+0.50%)" with config::marginColor,
            Images.Volta,
            config::tracker_volta
        ) { config.showMargins }
        exampleLines += SuffixHudLine(
            "Plasmas: ",
            "50" with config::dropColor,
            " (+0.50%)" with config::marginColor,
            Images.Plasma,
            config::tracker_plasma
        ) { config.showMargins }
        exampleLines += SuffixHudLine(
            "Ghostly Boots: ",
            "5" with config::dropColor,
            " (+0.50%)" with config::marginColor,
            Images.Boots,
            config::tracker_boots
        ) { config.showMargins }
        exampleLines += SuffixHudLine(
            "1m Coins: ",
            "1" with config::dropColor,
            " (+0.50%)" with config::marginColor,
            Images.Coins,
            config::tracker_coins
        ) { config.showMargins }
        exampleLines += BasicHudLine(
            "Average MF: ",
            "300.5" with config::mfColor,
            Images.MagicFind,
            config::tracker_mf
        )
        exampleLines += BasicHudLine(
            "Average XP: ",
            "250.5" with config::xpColor,
            Images.CombatXp,
            config::tracker_averageXp
        )
        exampleLines += BasicHudLine(
            "Total XP: ",
            "1,100,000" with config::xpColor,
            Images.CombatXp,
            config::tracker_totalXp
        )
        exampleLines += BasicHudLine(
            "Total Coins: ",
            "30,000,000" with config::coinColor,
            Images.Coins, // TODO get better coin image (like a furfsky icon or smth)
            config::tracker_totalMoney
        )
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

    fun refreshLines() {
        val config = GhostConfig
        val stats = GhostTracker.ghostStats

        if (config.tracker_kills)
            (lines[0] as BasicHudLine).text.text = intFormat.format(stats.kills)

        if (config.tracker_sorrow) {
            val line = lines[1] as SuffixHudLine
            line.text.text = intFormat.format(stats.sorrowCount)
            if (line.suffixVisible())
                line.margin.text = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
        }

        if (config.tracker_volta) {
            val line = lines[2] as SuffixHudLine
            line.text.text = intFormat.format(stats.voltaCount)
            if (line.suffixVisible())
                line.margin.text = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
        }

        if (config.tracker_plasma) {
            val line = lines[3] as SuffixHudLine
            line.text.text = intFormat.format(stats.plasmaCount)
            if (line.suffixVisible())
                line.margin.text = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
        }

        if (config.tracker_boots) {
            val line = lines[4] as SuffixHudLine
            line.text.text = intFormat.format(stats.bootsCount)
            if (line.suffixVisible())
                line.margin.text = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
        }

        if (config.tracker_coins) {
            val line = lines[5] as SuffixHudLine
            line.text.text = intFormat.format(stats.coinsCount)
            if (line.suffixVisible())
                line.margin.text = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
        }

        if (config.tracker_mf)
            (lines[6] as BasicHudLine).text.text = stats.getAverageMf(decimalFormat)

        if (config.tracker_averageXp) {
            val averageXp = stats.getAverageXp(decimalFormat)
            (lines[7] as BasicHudLine).text.text = if (config.showPrefixes) averageXp else "$averageXp/kill"
        }

        if (config.tracker_totalXp)
            (lines[8] as BasicHudLine).text.text = decimalFormat.format(stats.totalXp)

        if (config.tracker_totalMoney) {
            (lines[9] as BasicHudLine).text.text = intFormat.format(stats.totalValue)
        }
    }
}