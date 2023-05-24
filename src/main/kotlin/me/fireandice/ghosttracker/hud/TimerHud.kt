package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.GhostConfig
import me.fireandice.ghosttracker.tracker.GhostDrops
import me.fireandice.ghosttracker.tracker.GhostTimer
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.text.DecimalFormat

class TimerHud : BasicHud(true) {

    @Transient private var lines: ArrayList<TextComponent> = ArrayList(6)
    @Transient private var width = 0f
    @Transient private var height = 0f

    @Transient private var exampleLines: ArrayList<TextComponent> = ArrayList(6)
    @Transient private var exampleWidth = 0f
    @Transient private var exampleHeight = 0f

    @Transient private val format = DecimalFormat("#,##0.##")

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
        val stats = GhostTimer.sessionStats
        val time = GhostTimer.elapsedTime()
        var seconds: Int = (time / 1000f).toInt()

        if (config.showKillsPerHour) {
            val killRate =
                if (time == 0.toLong()) "-"
                else format.format(stats.kills.toFloat() / seconds * 3600)
            lines.add(TextComponent {
                add("Kills per hour: $killRate", config.killColor)
            })
        }

        if (config.showSorrowsPerHour) {
            val sorrowRate =
                if (time == 0L) "-"
                else format.format(stats.sorrowCount.toFloat() / seconds * 3600)
            lines.add(TextComponent {
                add("Sorrows per hour: $sorrowRate", config.dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.SORROW)
                if (config.showTimerMargins && stats.sorrowCount != 0 && diff != "") add(" ($diff)", config.marginColor)
            })
        }

        if (config.showVoltasPerHour) {
            val voltaRate =
                if (time == 0L) "-"
                else format.format(stats.voltaCount.toFloat() / seconds * 3600)
            lines.add(TextComponent {
                add("Voltas per hour: $voltaRate", config.dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.VOLTA)
                if (config.showTimerMargins && stats.voltaCount != 0 && diff != "") add(" ($diff)", config.marginColor)
            })
        }

        if (config.showPlasmasPerHour) {
            val plasmaRate =
                if (time == 0L) "-"
                else format.format(stats.plasmaCount.toFloat() / seconds * 3600)
            lines.add(TextComponent {
                add("Plasmas per hour: $plasmaRate", config.dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.PLASMA)
                if (config.showTimerMargins && stats.plasmaCount != 0 && diff != "") add(" ($diff)", config.marginColor)
            })
        }

        if (config.showBootsPerHour) {
            val bootsRate =
                if (time == 0L) "-"
                else format.format(stats.bootsCount.toFloat() / seconds * 3600)
            lines.add(TextComponent {
                add("Ghostly boots per hour: $bootsRate", config.dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.BOOTS)
                if (config.showTimerMargins && stats.bootsCount != 0 && diff != "") add(" ($diff)", config.marginColor)
            })
        }

        if (config.showCoinDropsPerHour) {
            val coinsRate =
                if (time == 0L) "-"
                else format.format(stats.coinsCount.toFloat() / seconds * 3600)
            lines.add(TextComponent {
                add("1m coins per hour: $coinsRate", config.dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.COINS)
                if (config.showTimerMargins && stats.coinsCount != 0 && diff != "") add(" ($diff)", config.marginColor)
            })
        }

        if (config.showAverageMf) {
            val mf =
                if (stats.getAverageMf() == null) "-"
                else format.format(stats.getAverageMf())
            lines.add(TextComponent {
                add("Average MF: $mf", config.mfColor)
            })
        }

        if (config.showAverageXp) {
            val averageXp =
                if (stats.kills <= 0) "-"
                else format.format(stats.totalXp / stats.kills)
            lines.add(TextComponent {
                add("Average XP: $averageXp", config.xpColor)
            })
        }

        if (config.showXpPerHour) {
            val xpRate: String =
                if (time <= 0.toLong()) "-"
                else format.format(stats.totalXp / seconds * 3600)
            lines.add(TextComponent {
                add("XP per hour: $xpRate", config.xpColor)
            })
        }

        if (config.showSessionXp) {
            lines.add(TextComponent {
                add("Total XP: ${format.format(stats.totalXp)}", config.xpColor)
            })
        }

        if (config.showTime) {
            val timeString = StringBuilder()
            val hours: Int = (seconds / 3600f).toInt()
            seconds %= 3600
            val minutes: Int = (seconds / 60f).toInt()
            seconds %= 60

            if (hours > 0) timeString.append("${hours}h ")
            if (minutes > 0 || hours > 0) timeString.append("${minutes}m ")     // if it's 1h 0m it should still show minutes
            timeString.append("${seconds}s")

            lines.add(TextComponent {
                add("Time: $timeString", config.timeColor)  // making it the same color as kills idk
            })
        }
    }

    private fun refreshExampleLines() {
        exampleLines.clear()

        val config = GhostConfig

        if (config.showKillsPerHour) exampleLines.add(TextComponent {
            add("Kills per hour: 6,000", config.killColor)
        })

        if (config.showSorrowsPerHour) exampleLines.add(TextComponent {
            add("Sorrows per hour: 50", config.dropColor)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor)
        })

        if (config.showVoltasPerHour) exampleLines.add(TextComponent {
            add("Voltas per hour: 50", config.dropColor)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor)
        })

        if (config.showPlasmasPerHour) exampleLines.add(TextComponent {
            add("Plasmas per hour: 50", config.dropColor)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor)
        })

        if (config.showBootsPerHour) exampleLines.add(TextComponent {
            add("Ghostly boots per hour: 50", config.dropColor)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor)
        })

        if (config.showCoinDropsPerHour) exampleLines.add(TextComponent {
            add("1m coins per hour: 50", config.dropColor)
            if (config.showTimerMargins) add(" (+0.50%)", config.marginColor)
        })

        if (config.showAverageMf) exampleLines.add(TextComponent {
            add("Average MF: 215.33", config.mfColor)
        })

        if (config.showAverageXp) exampleLines.add(TextComponent {
            add("Average XP: 183.33", config.xpColor)
        })

        if (config.showXpPerHour) exampleLines.add(TextComponent {
            add("XP per hour: 1,100,000", config.xpColor)
        })

        if (config.showSessionXp) exampleLines.add(TextComponent {
            add("Total XP: 1,100,000", config.xpColor)
        })

        if (config.showTime) exampleLines.add(TextComponent {
            add("Time: 1h 0m 0s", config.timeColor)
        })
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