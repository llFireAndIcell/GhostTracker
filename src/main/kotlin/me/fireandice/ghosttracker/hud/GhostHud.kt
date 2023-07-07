package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.GhostConfig
import me.fireandice.ghosttracker.GhostTracker
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
    @Transient private var width = 0f
    @Transient private var height = 0f

    @Transient private var exampleLines: ArrayList<TextComponent> = ArrayList(9)
    @Transient private var exampleWidth = 0f
    @Transient private var exampleHeight = 0f

    @Transient private val intFormat = DecimalFormat("#,###")
    @Transient private val decimalFormat = DecimalFormat("#,##0.##")
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
            textY += (FONT_HEIGHT + 1) * scale
            longestLine = longestLine.coerceAtLeast(line.width)
        }

        width = longestLine
        height = (lines.size * (FONT_HEIGHT + 1) - 1) * scale
    }

    private fun drawExample(x: Float, y: Float, scale: Float) {
        var textY = y
        var longestLine = 0f
        for (line in exampleLines) {
            line.draw(x, textY, scale)
            textY += (FONT_HEIGHT + 1) * scale
            longestLine = longestLine.coerceAtLeast(line.width)
        }
        exampleWidth = longestLine
        exampleHeight = (exampleLines.size * (FONT_HEIGHT + 1) - 1) * scale
    }

    private fun refreshLines() {
        lines.clear()

        val config = GhostConfig
        val stats = GhostTracker.ghostStats

        if (config.showKills)
            lines.add(SingleColorText("Kills: ${intFormat.format(stats.kills)}", config.killColor.rgb))

        if (config.showSorrow) lines.add(MultiColorText().apply {
            add("Sorrows: ${intFormat.format(stats.sorrowCount)}", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Sorrow, marginFormat)
            if (config.showMargins && stats.sorrowCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
        })

        if (config.showVolta) lines.add(MultiColorText().apply {
            add("Voltas: ${intFormat.format(stats.voltaCount)}", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Volta, marginFormat)
            if (config.showMargins && stats.voltaCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
        })

        if (config.showPlasma) lines.add(MultiColorText().apply {
            add("Plasmas: ${intFormat.format(stats.plasmaCount)}", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Plasma, marginFormat)
            if (config.showMargins && stats.plasmaCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
        })

        if (config.showBoots) lines.add(MultiColorText().apply {
            add("Ghostly boots: ${intFormat.format(stats.bootsCount)}", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Boots, marginFormat)
            if (config.showMargins && stats.bootsCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
        })

        if (config.showCoins) lines.add(MultiColorText().apply {
            add("1m coins: ${intFormat.format(stats.coinsCount)}", config.dropColor.rgb)
            val diff = stats.getPercentDifference(GhostDrops.Coins, marginFormat)
            if (config.showMargins && stats.coinsCount != 0 && diff != null) add(" ($diff)", config.marginColor.rgb)
        })

        if (config.showMf)
            lines.add(SingleColorText("Average MF: ${stats.getAverageMf(decimalFormat)}", config.mfColor.rgb))

        if (config.showXp)
            lines.add(SingleColorText("Average XP: ${stats.getAverageXp(decimalFormat)}", config.xpColor.rgb))

        if (config.showTotalXp)
            lines.add(SingleColorText("Total XP: ${decimalFormat.format(stats.totalXp)}", config.xpColor.rgb))
    }

    private fun refreshExampleLines() {
        exampleLines.clear()

        val config = GhostConfig
        GhostTracker.ghostStats

        if (config.showKills) exampleLines.add(SingleColorText("Kills: 1,000", config.killColor.rgb))

        if (config.showSorrow) exampleLines.add(MultiColorText().apply {
            add("Sorrows: 100", config.dropColor.rgb)
            if (config.showMargins) add(" (+0.50%)", config.marginColor.rgb)
        })

        if (config.showVolta) exampleLines.add(MultiColorText().apply {
            add("Voltas: 200", config.dropColor.rgb)
            if (config.showMargins) add(" (+0.50%)", config.marginColor.rgb)
        })

        if (config.showPlasma) exampleLines.add(MultiColorText().apply {
            add("Plasmas: 50", config.dropColor.rgb)
            if (config.showMargins) add(" (+0.50%)", config.marginColor.rgb)
        })

        if (config.showBoots) exampleLines.add(MultiColorText().apply {
            add("Ghostly boots: 5", config.dropColor.rgb)
            if (config.showMargins) add(" (+0.50%)", config.marginColor.rgb)
        })

        if (config.showCoins) exampleLines.add(MultiColorText().apply {
            add("1m coins: 1", config.dropColor.rgb)
            if (config.showMargins) add(" (+0.50%)", config.marginColor.rgb)
        })

        if (config.showMf) exampleLines.add(SingleColorText("Average MF: 300.5", config.mfColor.rgb))

        if (config.showXp) exampleLines.add(SingleColorText("Average XP: 250.5", config.xpColor.rgb))

        if (config.showTotalXp) exampleLines.add(SingleColorText("Total XP: 1,100,000", config.xpColor.rgb))
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