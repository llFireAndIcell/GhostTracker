package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.GhostConfig
import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.tracker.GhostDrops
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

    override fun getWidth(scale: Float, example: Boolean): Float = if (example) exampleWidth else width

    override fun getHeight(scale: Float, example: Boolean): Float = if (example) exampleHeight else height

    override fun shouldShow(): Boolean = isEnabled && ScoreboardUtils.inDwarvenMines

    override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
        if (example) {
            drawExample(x, y, scale)
            return
        }
        if (lines.size == 0) return

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
        refreshExampleLines()
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
        val stats = GhostTracker.ghostStats

        if (config.showKills) {
            lines.add(TextComponent {
                add("Kills: ${intFormat.format(stats.kills)}", config.killColor)
            })
        }

        if (config.showSorrow) {
            lines.add(TextComponent {
                add("Sorrows: ${stats.sorrowCount}", config.dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.SORROW)
                if (config.showMargins && stats.sorrowCount != 0 && diff != "") add(" ($diff)", config.marginColor)
            })
        }

        if (config.showVolta) {
            lines.add(TextComponent {
                add("Voltas: ${stats.voltaCount}", config.dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.VOLTA)
                if (config.showMargins && stats.voltaCount != 0 && diff != "") add(" ($diff)", config.marginColor)
            })
        }

        if (config.showPlasma) {
            lines.add(TextComponent {
                add("Plasmas: ${stats.plasmaCount}", config.dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.PLASMA)
                if (config.showMargins && stats.plasmaCount != 0 && diff != "") add(" ($diff)", config.marginColor)
            })
        }

        if (config.showBoots) {
            lines.add(TextComponent {
                add("Ghostly boots: ${stats.bootsCount}", config.dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.BOOTS)
                if (config.showMargins && stats.bootsCount != 0 && diff != "") add(" ($diff)", config.marginColor)
            })
        }

        if (config.showCoins) {
            lines.add(TextComponent {
                add("1m coins: ${stats.coinsCount}", config.dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.COINS)
                if (config.showMargins && stats.coinsCount != 0 && diff != "") add(" ($diff)", config.marginColor)
            })
        }

        if (config.showTotalXp) {
            lines.add(TextComponent {
                add("Total XP: ${decimalFormat.format(stats.totalXp)}", config.xpColor)
            })
        }
    }

    private fun refreshExampleLines() {
        exampleLines.clear()

        val config = GhostConfig
        GhostTracker.ghostStats

        if (config.showKills) {
            exampleLines.add(TextComponent {
                add("Kills: 1,000", config.killColor)
            })
        }

        if (config.showSorrow) {
            exampleLines.add(TextComponent {
                add("Sorrows: 100", config.dropColor)
                if (config.showMargins) add(" (+0.50%)", config.marginColor)
            })
        }

        if (config.showVolta) {
            exampleLines.add(TextComponent {
                add("Voltas: 200", config.dropColor)
                if (config.showMargins) add(" (+0.50%)", config.marginColor)
            })
        }

        if (config.showPlasma) {
            exampleLines.add(TextComponent {
                add("Plasmas: 50", config.dropColor)
                if (config.showMargins) add(" (+0.50%)", config.marginColor)
            })
        }

        if (config.showBoots) {
            exampleLines.add(TextComponent {
                add("Ghostly boots: 5", config.dropColor)
                if (config.showMargins) add(" (+0.50%)", config.marginColor)
            })
        }

        if (config.showCoins) {
            exampleLines.add(TextComponent {
                add("1m coins: 1", config.dropColor)
                if (config.showMargins) add(" (+0.50%)", config.marginColor)
            })
        }

        if (config.showTotalXp) {
            exampleLines.add(TextComponent {
                add("Total XP: 1,100,000", config.xpColor)
            })
        }
    }

    @Transient private var ticks = 0
    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        ticks++
        if (ticks % 10 != 0) return
        ticks = 0

        refreshLines()
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }
}