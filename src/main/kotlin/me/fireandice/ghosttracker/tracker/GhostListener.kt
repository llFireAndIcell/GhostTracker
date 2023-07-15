package me.fireandice.ghosttracker.tracker

import cc.polyfrost.oneconfig.utils.dsl.mc
import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import me.fireandice.ghosttracker.utils.stripControlCodes
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.roundToInt

object GhostListener {

    private val RARE_DROP_PATTERN = Pattern.compile("§r§6§lRARE DROP! §r§9(?<drop>[A-Za-z ]+) §r§b\\(\\+§r§b(?<mf>\\d+)% §r§b✯ Magic Find§r§b\\)§r")
    private const val COIN_DROP_MESSAGE = "§r§eThe ghost's death materialized §r§61,000,000 coins §r§efrom the mists!§r"
    private val COMBAT_XP_PATTERN = Pattern.compile("\\+(?<gained>[\\d.]+) Combat \\((?<progress>.+)\\)")
    private val numberFormat: NumberFormat = NumberFormat.getInstance(Locale.US)
    private var prevValue = -1f

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!ScoreboardUtils.inDwarvenMines || mc.thePlayer.posY > 100 || !(event.type == 0.toByte() || event.type == 1.toByte())) return
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
     * Some logic was taken from https://www.chattriggers.com/modules/v/GhostCounterV3
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onActionBar(event: ClientChatReceivedEvent) {
        if (event.type != 2.toByte()) return

        val message = event.message.unformattedText.stripControlCodes()
        val matcher = COMBAT_XP_PATTERN.matcher(message)

        if (matcher.find()) {
            val xpGained = numberFormat.parse(matcher.group("gained")).toFloat()

            var progress: String = matcher.group("progress")
            if (progress.endsWith("/0")) progress = progress.substring(0, progress.length - 2)

            val newValue = numberFormat.parse(progress).toFloat()

            // avoids tracking non ghost kills but still saves the xp value
            if (!ScoreboardUtils.inDwarvenMines || mc.thePlayer.posY > 100) {
                prevValue = newValue
                return
            }

            // if there are no previously tracked kills
            if (prevValue == -1f) trackKills(1, xpGained)
            else {
                val actualXpGained = newValue - prevValue
                // if you gain a bestiary level and gain 1m xp it might count all of those as kills, hence the
                // coerceAtMost (15 was an arbitrary choice)
                val killsGained = (actualXpGained / xpGained).roundToInt().coerceAtMost(15)

                if (prevValue != 0f && killsGained >= 0) trackKills(killsGained, xpGained * killsGained)  // xpGained * killsGained is more accurate and avoids rounding error
            }
            prevValue = newValue
        }
    }

    private fun trackDrops(drop: GhostDrops, magicFind: Int) {
        val ghostStats = GhostTracker.ghostStats
        val timerStats = GhostTimer.stats

        // coin drops are already handled before this method is called
        when (drop) {
            GhostDrops.Sorrow -> ghostStats.sorrowCount++
            GhostDrops.Volta -> ghostStats.voltaCount++
            GhostDrops.Plasma -> ghostStats.plasmaCount++
            GhostDrops.Boots -> ghostStats.bootsCount++
            else -> {}
        }
        ghostStats.totalMf += magicFind
        ghostStats.mfDropCount++

        if (GhostTimer.isTracking) {
            when (drop) {
                GhostDrops.Sorrow -> timerStats.sorrowCount++
                GhostDrops.Volta -> timerStats.voltaCount++
                GhostDrops.Plasma -> timerStats.plasmaCount++
                GhostDrops.Boots -> timerStats.bootsCount++
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