package me.fireandice.ghosttracker.hud

import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.Stage
import cc.polyfrost.oneconfig.events.event.TickEvent
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import me.fireandice.ghosttracker.GhostConfig
import me.fireandice.ghosttracker.tracker.GhostDrops
import me.fireandice.ghosttracker.tracker.GhostTimer
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import java.text.DecimalFormat

class TimerHud : BasicHud(true) {

    @Transient private var lines: ArrayList<TextComponent> = ArrayList(6)
    @Transient private var height = 0f
    @Transient private var width = 0f

    @Transient private val killColor = OneColor(85, 255, 255)   // aqua
    @Transient private val dropColor = OneColor(85, 85, 255)    // blue
    @Transient private val mfColor = OneColor(255, 170, 0)      // gold
    @Transient private val xpColor = OneColor(255, 85, 85)      // red
    @Transient private val marginColor = OneColor(85, 85, 85)   // dark gray

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
                it.add("Kills per hour: $killRate", killColor)
            })
        }

        if (config.showSorrowsPerHour) {
            val sorrowRate =
                if (time == 0L) "-"
                else format.format(stats.sorrowCount.toFloat() / seconds * 3600)
            lines.add(TextComponent {
                it.add("Sorrows per hour: $sorrowRate", dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.SORROW)
                if (stats.sorrowCount != 0 && diff != "") it.add(" ($diff)", marginColor)
            })
        }

        if (config.showVoltasPerHour) {
            val voltaRate =
                if (time == 0L) "-"
                else format.format(stats.voltaCount.toFloat() / seconds * 3600)
            lines.add(TextComponent {
                it.add("Voltas per hour: $voltaRate", dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.VOLTA)
                if (stats.voltaCount != 0 && diff != "") it.add(" ($diff)", marginColor)
            })
        }

        if (config.showPlasmasPerHour) {
            val plasmaRate =
                if (time == 0L) "-"
                else format.format(stats.plasmaCount.toFloat() / seconds * 3600)
            lines.add(TextComponent {
                it.add("Plasmas per hour: $plasmaRate", dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.PLASMA)
                if (stats.plasmaCount != 0 && diff != "") it.add(" ($diff)", marginColor)
            })
        }

        if (config.showBootsPerHour) {
            val bootsRate =
                if (time == 0L) "-"
                else format.format(stats.bootsCount.toFloat() / seconds * 3600)
            lines.add(TextComponent {
                it.add("Ghostly boots per hour: $bootsRate", dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.BOOTS)
                if (stats.bootsCount != 0 && diff != "") it.add(" ($diff)", marginColor)
            })
        }

        if (config.showCoinDropsPerHour) {
            val coinsRate =
                if (time == 0L) "-"
                else format.format(stats.coinsCount.toFloat() / seconds * 3600)
            lines.add(TextComponent {
                it.add("1m coins per hour: $coinsRate", dropColor)
                val diff = stats.getPercentDiffString(GhostDrops.COINS)
                if (stats.coinsCount != 0 && diff != "") it.add(" ($diff)", marginColor)
            })
        }

        if (config.showAverageMf) {
            val mf =
                if (stats.mfDropCount == 0) "-"
                else format.format(stats.totalMf.toFloat() / stats.mfDropCount)
            lines.add(TextComponent {
                it.add("Average MF: $mf", mfColor)
            })
        }

        if (config.showAverageXp) {
            val averageXp =
                if (stats.kills <= 0) "-"
                else format.format(stats.totalXp / stats.kills)
            lines.add(TextComponent {
                it.add("Average XP: $averageXp", xpColor)
            })
        }

        if (config.showXpPerHour) {
            val xpRate: String =
                if (time <= 0.toLong()) "-"
                else format.format(stats.totalXp / seconds * 3600)
            lines.add(TextComponent {
                it.add("XP per hour: $xpRate", xpColor)
            })
        }

        if (config.showSessionXp) {
            lines.add(TextComponent {
                it.add("Total XP: ${format.format(stats.totalXp)}", xpColor)
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
                it.add("Time: $timeString", killColor)  // making it the same color as kills idk
            })
        }
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

    private fun refreshExampleLines() {
        exampleLines.clear()

        val config = GhostConfig

        if (config.showKillsPerHour) exampleLines.add(TextComponent {
            it.add("Kills per hour: 6,000", killColor)
        })

        if (config.showSorrowsPerHour) exampleLines.add(TextComponent {
            it.add("Sorrows per hour: 50", dropColor)
            it.add(" (+0.50%)", marginColor)
        })

        if (config.showVoltasPerHour) exampleLines.add(TextComponent {
            it.add("Voltas per hour: 50", dropColor)
            it.add(" (+0.50%)", marginColor)
        })

        if (config.showPlasmasPerHour) exampleLines.add(TextComponent {
            it.add("Plasmas per hour: 50", dropColor)
            it.add(" (+0.50%)", marginColor)
        })

        if (config.showBootsPerHour) exampleLines.add(TextComponent {
            it.add("Ghostly boots per hour: 50", dropColor)
            it.add(" (+0.50%)", marginColor)
        })

        if (config.showCoinDropsPerHour) exampleLines.add(TextComponent {
            it.add("1m coins per hour: 50", dropColor)
            it.add(" (+0.50%)", marginColor)
        })

        if (config.showAverageMf) exampleLines.add(TextComponent {
            it.add("Average MF: 215.33", mfColor)
        })

        if (config.showAverageXp) exampleLines.add(TextComponent {
            it.add("Average XP: 183.33", xpColor)
        })

        if (config.showXpPerHour) exampleLines.add(TextComponent {
            it.add("XP per hour: 1,100,000", xpColor)
        })

        if (config.showSessionXp) exampleLines.add(TextComponent {
            it.add("Total XP: 1,100,000", xpColor)
        })

        if (config.showTime) exampleLines.add(TextComponent {
            it.add("Time: 1h 0m 0s", killColor)
        })
    }

    @Transient private var ticks = 0
    @Subscribe
    fun onTick(event: TickEvent) {
        if (event.stage != Stage.START) return
        ticks++
        if (ticks % 10 != 0) return
        ticks = 0

        refreshLines()
    }

    init {
        EventManager.INSTANCE.register(this)
    }
}