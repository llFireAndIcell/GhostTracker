package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.tracker.GhostDrops
import me.fireandice.ghosttracker.utils.FONT_HEIGHT
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.text.DecimalFormat

class GhostHud : BasicHud(true) {

    @Transient private var lines: ArrayList<TextComponent> = ArrayList(9)
    @Transient private var exampleLines: ArrayList<TextComponent> = ArrayList(9)

    @Transient private var width = 0f
    @Transient private var height = 0f

    @Transient private val intFormat = DecimalFormat("#,###")
    @Transient private val decimalFormat = DecimalFormat("#,##0.##")
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
        val stats = GhostTracker.ghostStats

        if (config.tracker_kills)
            (lines[0] as SingleColorText).set("Kills: ${intFormat.format(stats.kills)}", config.killColor.rgb)

        if (config.tracker_sorrow) (lines[1] as MultiColorText).apply {
            set(0, "Sorrows: ${intFormat.format(stats.sorrowCount)}", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
            if (config.tracker_margins && stats.sorrowCount != 0) set(1, " ($diff)", config.marginColor.rgb)
        }

        if (config.tracker_volta) (lines[2] as MultiColorText).apply {
            set(0, "Voltas: ${intFormat.format(stats.voltaCount)}", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
            if (config.tracker_margins && stats.voltaCount != 0) set(1, " ($diff)", config.marginColor.rgb)
        }

        if (config.tracker_plasma) (lines[3] as MultiColorText).apply {
            set(0, "Plasmas: ${intFormat.format(stats.plasmaCount)}", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
            if (config.tracker_margins && stats.plasmaCount != 0) set(1, " ($diff)", config.marginColor.rgb)
        }

        if (config.tracker_boots) (lines[4] as MultiColorText).apply {
            set(0, "Ghostly boots: ${intFormat.format(stats.bootsCount)}", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
            if (config.tracker_margins && stats.bootsCount != 0) set(1, " ($diff)", config.marginColor.rgb)
        }

        if (config.tracker_coins) (lines[5] as MultiColorText).apply {
            set(0, "1m coins: ${intFormat.format(stats.coinsCount)}", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
            if (config.tracker_margins && stats.coinsCount != 0) set(1, " ($diff)", config.marginColor.rgb)
        }

        if (config.tracker_mf)
            (lines[6] as SingleColorText).set("Average MF: ${stats.getAverageMf(decimalFormat)}", config.mfColor.rgb)

        if (config.tracker_averageXp)
            (lines[7] as SingleColorText).set("Average XP: ${stats.getAverageXp(decimalFormat)}", config.xpColor.rgb)

        if (config.tracker_totalXp)
            (lines[8] as SingleColorText).set("Total XP: ${decimalFormat.format(stats.totalXp)}", config.xpColor.rgb)
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) refreshLines()
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)

        val config = GhostConfig
        val stats = GhostTracker.ghostStats

        //<editor-fold desc="initializing lines">
        lines.add(SingleColorText("Kills: ${intFormat.format(stats.kills)}", config.killColor.rgb) { config.tracker_kills })
        lines.add(MultiColorText().apply {
            add("Sorrows: ${intFormat.format(stats.sorrowCount)}", config.dropColor.rgb) { config.tracker_sorrow }
            val diff = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
            add(" ($diff)", config.marginColor.rgb) { config.tracker_margins && stats.sorrowCount != 0 }
        })
        lines.add(MultiColorText().apply {
            add("Voltas: ${intFormat.format(stats.voltaCount)}", config.dropColor.rgb) { config.tracker_volta }
            val diff = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
            add(" ($diff)", config.marginColor.rgb) { config.tracker_margins && stats.voltaCount != 0 }
        })
        lines.add(MultiColorText().apply {
            add("Plasmas: ${intFormat.format(stats.plasmaCount)}", config.dropColor.rgb) { config.tracker_plasma }
            val diff = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
            add(" ($diff)", config.marginColor.rgb) { config.tracker_margins && stats.plasmaCount != 0 }
        })
        lines.add(MultiColorText().apply {
            add("Ghostly boots: ${intFormat.format(stats.bootsCount)}", config.dropColor.rgb) { config.tracker_boots }
            val diff = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
            add(" ($diff)", config.marginColor.rgb) { config.tracker_margins && stats.bootsCount != 0 }
        })
        lines.add(MultiColorText().apply {
            add("1m coins: ${intFormat.format(stats.coinsCount)}", config.dropColor.rgb) { config.tracker_coins }
            val diff = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
            add(" ($diff)", config.marginColor.rgb) { config.tracker_margins && stats.coinsCount != 0 }
        })
        lines.add(SingleColorText("Average MF: ${stats.getAverageMf(decimalFormat)}", config.mfColor.rgb) { config.tracker_mf })
        lines.add(SingleColorText("Average XP: ${stats.getAverageXp(decimalFormat)}", config.xpColor.rgb) { config.tracker_averageXp })
        lines.add(SingleColorText("Total XP: ${decimalFormat.format(stats.totalXp)}", config.xpColor.rgb) { config.tracker_totalXp })
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
}