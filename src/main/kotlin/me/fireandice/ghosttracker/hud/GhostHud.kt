package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.hud.elements.*
import me.fireandice.ghosttracker.tracker.GhostDrops
import me.fireandice.ghosttracker.utils.FONT_HEIGHT
import me.fireandice.ghosttracker.utils.Image
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.text.DecimalFormat

class GhostHud : BasicHud(true) {

    @Transient private var lines: ArrayList<HudLine> = ArrayList(9)
    @Transient private var exampleLines: ArrayList<TextComponent> = ArrayList(9)

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
        lines += SingleHudLine(
            "kills",
            "Kills: ${intFormat.format(stats.kills)}" with config::killColor,
            Image(ResourceLocation(GhostTracker.MODID, "kills.png"), 160, 160)
        ) { config.tracker_kills }
        lines += DualHudLine(
            "sorrow",
            "Sorrows: ${intFormat.format(stats.sorrowCount)}" with config::dropColor,
            stats.getPercentDifference(GhostDrops.Sorrow, marginFormat) with config::marginColor,
            Image(ResourceLocation(GhostTracker.MODID, "sorrow.png"), 16, 16),
        ) { config.tracker_sorrow }
        lines += DualHudLine(
            "volta",
            "Voltas: ${intFormat.format(stats.voltaCount)}" with config::dropColor,
            stats.getPercentDifference(GhostDrops.Volta, marginFormat) with config::marginColor,
            Image(ResourceLocation(GhostTracker.MODID, "volta.png"), 185, 185),
        ) { config.tracker_volta }
        lines += DualHudLine(
            "plasma",
            "Plasmas: ${intFormat.format(stats.plasmaCount)}" with config::dropColor,
            stats.getPercentDifference(GhostDrops.Plasma, marginFormat) with config::marginColor,
            Image(ResourceLocation(GhostTracker.MODID, "plasma.png"), 185, 185)
        ) { config.tracker_plasma }
        lines += DualHudLine(
            "boots",
            "Ghostly boots: ${intFormat.format(stats.bootsCount)}" with config::dropColor,
            stats.getPercentDifference(GhostDrops.Boots, marginFormat) with config::marginColor,
            Image(ResourceLocation(GhostTracker.MODID, "ghostly boots.png"), 160, 160)
        ) { config.tracker_boots }
        lines += DualHudLine(
            "coins",
            "1m coins: ${intFormat.format(stats.coinsCount)}" with config::dropColor,
            stats.getPercentDifference(GhostDrops.Coins, marginFormat) with config::marginColor,
            Image(ResourceLocation(GhostTracker.MODID, "coins.png"), 185, 185)
        ) { config.tracker_coins }
        lines += SingleHudLine(
            "mf",
            "Average MF: ${stats.getAverageMf(decimalFormat)}" with config::mfColor,
            Image(ResourceLocation(GhostTracker.MODID, "magic find.png"), 160, 160)
        ) { config.tracker_mf }
        lines += SingleHudLine(
            "averageXp",
            "Average XP: ${stats.getAverageXp(decimalFormat)}" with config::xpColor,
            Image(ResourceLocation(GhostTracker.MODID, "xp.png"), 500, 500)
        ) {config.tracker_averageXp }
        lines += SingleHudLine(
            "TotalXp",
            "Total XP: ${decimalFormat.format(stats.totalXp)}" with config::xpColor,
            Image(ResourceLocation(GhostTracker.MODID, "xp.png"), 500, 500)
        ) { config.tracker_totalXp }
        //</editor-fold>

        //<editor-fold desc="initializing example lines">
        exampleLines.add(SingleColorText("Kills: 1,000", config.killColor.rgb) { config.tracker_kills })
        exampleLines.add(MultiColorText().apply {
            add("Sorrows: 100", config.dropColor.rgb) { config.tracker_sorrow }
            add(" (+0.50%)", config.marginColor.rgb) { config.tracker_margins }
        })
        exampleLines.add(MultiColorText().apply {
            add("Voltas: 200", config.dropColor.rgb) { config.tracker_volta }
            add(" (+0.50%)", config.marginColor.rgb) { config.tracker_margins }
        })
        exampleLines.add(MultiColorText().apply {
            add("Plasmas: 50", config.dropColor.rgb) { config.tracker_plasma }
            add(" (+0.50%)", config.marginColor.rgb) { config.tracker_margins }
        })
        exampleLines.add(MultiColorText().apply {
            add("Ghostly boots: 5", config.dropColor.rgb) { config.tracker_boots }
            add(" (+0.50%)", config.marginColor.rgb) { config.tracker_margins }
        })
        exampleLines.add(MultiColorText().apply {
            add("1m coins: 1", config.dropColor.rgb) { config.tracker_coins }
            add(" (+0.50%)", config.marginColor.rgb) { config.tracker_margins }
        })
        exampleLines.add(SingleColorText("Average MF: 300.5", config.mfColor.rgb) { config.tracker_mf })
        exampleLines.add(SingleColorText("Average XP: 250.5", config.xpColor.rgb) { config.tracker_averageXp })
        exampleLines.add(SingleColorText("Total XP: 1,100,000", config.xpColor.rgb) { config.tracker_totalXp })
        //</editor-fold>
    }

    override fun getWidth(scale: Float, example: Boolean): Float = width
    override fun getHeight(scale: Float, example: Boolean): Float = height
    override fun shouldShow(): Boolean = isEnabled && (GhostConfig.showEverywhere || ScoreboardUtils.inDwarvenMines)

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        drawLines(lines, x, y, scale)
    }

    private fun drawLines(linesToDraw: ArrayList<HudLine>, x: Float, y: Float, scale: Float) {
        var drawnLines = 0
        var textY = y
        var longestLine = 0f

        for (line in linesToDraw) {
            if (line.draw(x, textY, scale)) continue
            drawnLines++
            textY += FONT_HEIGHT * scale
            longestLine = longestLine.coerceAtLeast(line.width)
        }
        height = (drawnLines * FONT_HEIGHT - 1) * scale
        width = longestLine * scale
    }

    private fun refreshLines() {
        val config = GhostConfig
        val stats = GhostTracker.ghostStats

        if (config.tracker_kills)
            (lines[0] as SingleHudLine).text.text = "Kills: ${intFormat.format(stats.kills)}"

        if (config.tracker_sorrow) {
            val line = lines[1] as DualHudLine
            line.first.text = "Sorrows: ${intFormat.format(stats.sorrowCount)}"
            if (config.tracker_margins && stats.sorrowCount != 0)
                line.second.text = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
        }

        if (config.tracker_volta) {
            val line = lines[2] as DualHudLine
            line.first.text = "Voltas: ${intFormat.format(stats.voltaCount)}"
            if (config.tracker_margins && stats.voltaCount != 0)
                line.second.text = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
        }

        if (config.tracker_plasma) {
            val line = lines[3] as DualHudLine
            line.first.text = "Plasmas: ${intFormat.format(stats.plasmaCount)}"
            if (config.tracker_margins && stats.plasmaCount != 0)
                line.second.text = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
        }

        if (config.tracker_boots) {
            val line = lines[4] as DualHudLine
            line.first.text = "Ghostly boots: ${intFormat.format(stats.bootsCount)}"
            if (config.tracker_margins && stats.bootsCount != 0)
                line.second.text = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
        }

        if (config.tracker_coins) {
            val line = lines[5] as DualHudLine
            line.first.text = "1m coins: ${intFormat.format(stats.coinsCount)}"
            if (config.tracker_margins && stats.coinsCount != 0)
                line.second.text = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
        }

        if (config.tracker_mf)
            (lines[6] as SingleHudLine).text.text = "Average MF: ${stats.getAverageMf(decimalFormat)}"

        if (config.tracker_averageXp)
            (lines[7] as SingleHudLine).text.text = "Average XP: ${stats.getAverageXp(decimalFormat)}"

        if (config.tracker_totalXp)
            (lines[8] as SingleHudLine).text.text = "Total XP: ${decimalFormat.format(stats.totalXp)}"
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) refreshLines()
    }
}