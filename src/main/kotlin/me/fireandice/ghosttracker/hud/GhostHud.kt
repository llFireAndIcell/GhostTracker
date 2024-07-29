package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.hud.elements.BasicHudLine
import me.fireandice.ghosttracker.hud.elements.HudLine
import me.fireandice.ghosttracker.hud.elements.SuffixHudLine
import me.fireandice.ghosttracker.hud.elements.withColor
import me.fireandice.ghosttracker.tracker.GhostDrops
import me.fireandice.ghosttracker.utils.FONT_HEIGHT
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import net.minecraftforge.common.MinecraftForge
import java.text.DecimalFormat

class GhostHud : BasicHud(true) {

    @Transient private val lineKeys: ArrayList<String> = arrayListOf(
        "kills",
        "sorrow",
        "volta",
        "plasma",
        "boots",
        "1m-coin",
        "mf",
        "average-xp",
        "total-xp",
        "scavenger",
        "total-coins"
    )

    @Transient private val lines: MutableMap<String, HudLine> = mutableMapOf()
    @Transient private val exampleLines: MutableMap<String, HudLine> = mutableMapOf()

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
        lines["kills"] = BasicHudLine(
            "Kills: ",
            intFormat.format(stats.kills) withColor config::killColor,
            Images.Kills,
            config::tracker_kills
        )

        lines["sorrow"] = SuffixHudLine(
            "Sorrows: ",
            intFormat.format(stats.sorrowCount) withColor config::dropColor,
            stats.getPercentDifference(GhostDrops.Sorrow, marginFormat) withColor config::marginColor,
            Images.Sorrow,
            config::tracker_sorrow
        ) { config.showMargins && stats.sorrowCount != 0 }

        lines["volta"] = SuffixHudLine(
            "Voltas: ",
            intFormat.format(stats.voltaCount) withColor config::dropColor,
            stats.getPercentDifference(GhostDrops.Volta, marginFormat) withColor config::marginColor,
            Images.Volta,
            config::tracker_volta
        ) { config.showMargins && stats.voltaCount != 0 }

        lines["plasma"] = SuffixHudLine(
            "Plasmas: ",
            intFormat.format(stats.plasmaCount) withColor config::dropColor,
            stats.getPercentDifference(GhostDrops.Plasma, marginFormat) withColor config::marginColor,
            Images.Plasma,
            config::tracker_plasma
        ) { config.showMargins && stats.plasmaCount != 0 }

        lines["boots"] = SuffixHudLine(
            "Ghostly Boots: ",
            intFormat.format(stats.bootsCount) withColor config::dropColor,
            stats.getPercentDifference(GhostDrops.Boots, marginFormat) withColor config::marginColor,
            Images.Boots,
            config::tracker_boots
        ) { config.showMargins && stats.bootsCount != 0 }
        lines["1m-coins"] = SuffixHudLine(
            "1m Coins: ",
            intFormat.format(stats.coinsCount) withColor config::dropColor,
            stats.getPercentDifference(GhostDrops.Coins, marginFormat) withColor config::marginColor,
            Images.Coins,
            config::tracker_coins
        ) { config.showMargins && stats.coinsCount != 0 }

        lines["mf"] = BasicHudLine(
            "Average MF: ",
            stats.getAverageMf(decimalFormat) withColor config::mfColor,
            Images.MagicFind,
            config::tracker_mf
        )

        val averageXp = stats.getAverageXp(decimalFormat)
        lines["average-xp"] = BasicHudLine(
            "Average XP: ",
            (if (config.showPrefixes) averageXp else "$averageXp/kill") withColor config::xpColor,
            Images.CombatXp,
            config::tracker_averageXp
        )
        lines["total-xp"] = BasicHudLine(
            "Total XP: ",
            decimalFormat.format(stats.totalXp) withColor config::xpColor,
            Images.CombatXp,
            config::tracker_totalXp
        )

        lines["scavenger"] = BasicHudLine(
            "Scavenger: ",
            intFormat.format(stats.scavenger) withColor config::coinColor,
            Images.Coins,
            config::tracker_scavenger
        )

        lines["total-coins"] = BasicHudLine(
            "Total Coins: ",
            intFormat.format(stats.totalValue) withColor config::coinColor,
            Images.Money,
            config::tracker_totalMoney
        )
        //</editor-fold>

        //<editor-fold desc="initializing example lines">
        exampleLines["kills"] = BasicHudLine(
            "Kills: ",
            "1,000" withColor config::killColor,
            Images.Kills,
            config::tracker_kills
        )

        exampleLines["sorrow"] = SuffixHudLine(
            "Sorrows: ",
            "100" withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Sorrow,
            config::tracker_sorrow
        ) { config.showMargins }

        exampleLines["volta"] = SuffixHudLine(
            "Voltas: ",
            "200" withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Volta,
            config::tracker_volta
        ) { config.showMargins }

        exampleLines["plasma"] = SuffixHudLine(
            "Plasmas: ",
            "50" withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Plasma,
            config::tracker_plasma
        ) { config.showMargins }

        exampleLines["boots"] = SuffixHudLine(
            "Ghostly Boots: ",
            "5" withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Boots,
            config::tracker_boots
        ) { config.showMargins }

        exampleLines["1m-coins"] = SuffixHudLine(
            "1m Coins: ",
            "1" withColor config::dropColor,
            " (+0.50%)" withColor config::marginColor,
            Images.Coins,
            config::tracker_coins
        ) { config.showMargins }

        exampleLines["mf"] = BasicHudLine(
            "Average MF: ",
            "300.5" withColor config::mfColor,
            Images.MagicFind,
            config::tracker_mf
        )

        exampleLines["average-xp"] = BasicHudLine(
            "Average XP: ",
            "250.5" withColor config::xpColor,
            Images.CombatXp,
            config::tracker_averageXp
        )

        exampleLines["total-xp"] = BasicHudLine(
            "Total XP: ",
            "1,100,000" withColor config::xpColor,
            Images.CombatXp,
            config::tracker_totalXp
        )

        exampleLines["scavenger"] = BasicHudLine(
            "Scavenger: ",
            "1,000,000" withColor config::coinColor,
            Images.Coins,
            config::tracker_scavenger
        )

        exampleLines["total-coins"] = BasicHudLine(
            "Total Coins: ",
            "30,000,000" withColor config::coinColor,
            Images.Money,
            config::tracker_totalMoney
        )
        //</editor-fold>
    }

    override fun getWidth(scale: Float, example: Boolean): Float = width
    override fun getHeight(scale: Float, example: Boolean): Float = height
    override fun shouldShow(): Boolean = isEnabled && (GhostConfig.showEverywhere || ScoreboardUtils.inDwarvenMines)

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        var drawnLines = 0
        var textY = y
        var longestLine = 0f

        for (key in lineKeys) {
            val line = if (example) exampleLines[key] else lines[key]
            if (line == null) continue

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
        val stats = GhostTracker.ghostStats

        if (config.tracker_kills)
            (lines["kills"] as BasicHudLine).main.text = intFormat.format(stats.kills)

        if (config.tracker_sorrow) {
            val line = lines["sorrow"] as SuffixHudLine
            line.main.text = intFormat.format(stats.sorrowCount)
            if (line.suffixVisible())
                line.suffix.text = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
        }

        if (config.tracker_volta) {
            val line = lines["volta"] as SuffixHudLine
            line.main.text = intFormat.format(stats.voltaCount)
            if (line.suffixVisible())
                line.suffix.text = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
        }

        if (config.tracker_plasma) {
            val line = lines["plasma"] as SuffixHudLine
            line.main.text = intFormat.format(stats.plasmaCount)
            if (line.suffixVisible())
                line.suffix.text = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
        }

        if (config.tracker_boots) {
            val line = lines["boots"] as SuffixHudLine
            line.main.text = intFormat.format(stats.bootsCount)
            if (line.suffixVisible())
                line.suffix.text = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
        }

        if (config.tracker_coins) {
            val line = lines["1m-coins"] as SuffixHudLine
            line.main.text = intFormat.format(stats.coinsCount)
            if (line.suffixVisible())
                line.suffix.text = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
        }

        if (config.tracker_mf)
            (lines["mf"] as BasicHudLine).main.text = stats.getAverageMf(decimalFormat)

        if (config.tracker_averageXp) {
            val averageXp = stats.getAverageXp(decimalFormat)
            (lines["average-xp"] as BasicHudLine).main.text = if (config.showPrefixes) averageXp else "$averageXp/kill"
        }

        if (config.tracker_totalXp)
            (lines["total-xp"] as BasicHudLine).main.text = decimalFormat.format(stats.totalXp)

        if (config.tracker_scavenger)
            (lines["scavenger"] as BasicHudLine).main.text = intFormat.format(stats.scavenger)

        if (config.tracker_totalMoney) {
            (lines["total-coins"] as BasicHudLine).main.text = intFormat.format(stats.totalValue)
        }
    }
}
