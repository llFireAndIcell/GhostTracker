package me.fireandice.ghosttracker.tracker

import cc.polyfrost.oneconfig.utils.dsl.mc
import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.tracker.GhostDrops.*
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import me.fireandice.ghosttracker.utils.stripControlCodes
import net.minecraftforge.client.event.ClientChatReceivedEvent
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

object GhostListener {

    private val RARE_DROP_PATTERN = "§r§6§lRARE DROP! §r§9(?<drop>[A-Za-z ]+) §r§b\\(\\+§r§b(?<mf>\\d+)% §r§b✯ Magic Find§r§b\\)§r".toPattern()
    private const val COIN_DROP_MESSAGE = "§r§eThe ghost's death materialized §r§61,000,000 coins §r§efrom the mists!§r"
    private val COMBAT_XP_PATTERN = "\\+(?<gained>[\\d.]+) Combat \\((?<progress>.+)\\)".toPattern()
    private val numberFormat: NumberFormat = NumberFormat.getInstance(Locale.US)
    private var previousXp = -1f

    /**
     * Called in `EventListener.onChat()`
     */
    fun onChat(event: ClientChatReceivedEvent) {
        if (!ScoreboardUtils.inDwarvenMines ||
            mc.thePlayer.posY > 100 ||
            !(event.type == 0.toByte() ||
            event.type == 1.toByte())) return

        val message = event.message.formattedText

        // Detecting if 1m coins dropped
        if (COIN_DROP_MESSAGE == message) {
            GhostTracker.ghostStats.coinsCount++
            if (GhostTimer.isTracking) GhostTimer.stats.coinsCount++
            return
        }

        // Detecting one of the various normal rng drops
        val matcher = RARE_DROP_PATTERN.matcher(message)

        if (matcher.matches()) {
            val drop = GhostDrops.get(matcher.group("drop")) ?: return
            val mf = numberFormat.parse(matcher.group("mf")).toInt()
            trackDrops(drop, mf)
        }
    }

    /**
     * Some logic was taken from https://www.chattriggers.com/modules/v/GhostCounterV3.
     * Called in `EventListener.onChat()`
     */
    fun onActionBar(event: ClientChatReceivedEvent) {
        if (event.type != 2.toByte()) return

        val message = event.message.unformattedText.stripControlCodes()
        val matcher = COMBAT_XP_PATTERN.matcher(message)

        if (matcher.find()) {
            val xpGained = numberFormat.parse(matcher.group("gained")).toFloat()

            var progress: String = matcher.group("progress")
            if (progress.endsWith("/0")) progress = progress.substring(0, progress.length - 2)

            val newXp = numberFormat.parse(progress).toFloat()

            // avoids tracking non ghost kills but still saves the xp value
            if (!ScoreboardUtils.inDwarvenMines || mc.thePlayer.posY > 100) {
                previousXp = newXp
                return
            }

            // if there are no previously tracked kills
            if (previousXp == -1f) trackKills(1, xpGained)
            else {
                val actualXpGained = newXp - previousXp
                // if you gain a bestiary level and gain 1m xp it might count all of those as kills, hence the
                // coerceAtMost (15 was an arbitrary choice)
                val killsGained = (actualXpGained / xpGained).roundToInt().coerceAtMost(15)

                // xpGained * killsGained is more accurate and avoids rounding error
                if (previousXp != 0f && killsGained >= 0) trackKills(killsGained, xpGained * killsGained)
            }
            previousXp = newXp
        }
    }

    private fun trackDrops(drop: GhostDrops, magicFind: Int) {
        val ghostStats = GhostTracker.ghostStats
        val timerStats = GhostTimer.stats

        // coin drops are already handled before this method is called
        when (drop) {
            Sorrow -> ghostStats.sorrowCount++
            Volta -> ghostStats.voltaCount++
            Plasma -> ghostStats.plasmaCount++
            Boots -> ghostStats.bootsCount++
            else -> {}
        }
        ghostStats.totalMf += magicFind
        ghostStats.mfDropCount++

        if (GhostTimer.isTracking) {
            when (drop) {
                Sorrow -> timerStats.sorrowCount++
                Volta -> timerStats.voltaCount++
                Plasma -> timerStats.plasmaCount++
                Boots -> timerStats.bootsCount++
                else -> {}
            }
            timerStats.totalMf += magicFind
            timerStats.mfDropCount++
        }
    }

    private fun trackKills(killsGained: Int, xpGained: Float) {
        GhostTracker.ghostStats.kills += killsGained
        GhostTracker.ghostStats.totalXp += xpGained

        if (GhostTimer.isTracking) {
            GhostTimer.stats.kills += killsGained
            GhostTimer.stats.totalXp += xpGained
        }
    }
}