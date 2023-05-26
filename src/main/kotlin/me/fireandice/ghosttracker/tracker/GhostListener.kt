package me.fireandice.ghosttracker.tracker

import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import me.fireandice.ghosttracker.utils.stripColorCodes
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
    private var prevValue = -1f

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!ScoreboardUtils.inDwarvenMines || !(event.type == 0.toByte() || event.type == 1.toByte())) return
        val message = event.message.formattedText

        // Detecting if 1m coins dropped
        if (COIN_DROP_MESSAGE == message) {
            GhostTracker.ghostStats.coinsCount++
            if (GhostTimer.isTracking) GhostTimer.stats.coinsCount++
            return
        }

        // Detecting one of the various normal rng drops
        val matcher = RARE_DROP_PATTERN.matcher(message)
        val drop: GhostDrops

        if (matcher.matches()) {
            val dropStr = matcher.group("drop")
            drop = when (dropStr) {
                "Sorrow" -> GhostDrops.SORROW
                "Volta" -> GhostDrops.VOLTA
                "Plasma" -> GhostDrops.PLASMA
                "Ghostly Boots" -> GhostDrops.BOOTS
                else -> return  // do not count magic find of non-ghost drops
            }
            val numberFormat = NumberFormat.getInstance(Locale.US)
            val mf = numberFormat.parse(matcher.group("mf")).toInt()

            trackDrops(drop, mf)
        }
    }

    /**
     * Some of the logic was taken from https://www.chattriggers.com/modules/v/GhostCounterV3
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onActionBar(event: ClientChatReceivedEvent) {
        if (!ScoreboardUtils.inDwarvenMines || event.type != 2.toByte()) return
        val message = event.message.unformattedText.stripColorCodes()

        // Detecting kills
        val matcher = COMBAT_XP_PATTERN.matcher(message)

        // Detecting skill xp gain message
        if (matcher.find()) {
            val numberFormat = NumberFormat.getInstance(Locale.US)
            val gained = numberFormat.parse(matcher.group("gained")).toFloat()

            var progress: String = matcher.group("progress")
            if (Regex("[\\d,]+/0").matches(progress)) progress = progress.substring(0, progress.length - 2)

            val newValue = numberFormat.parse(progress).toFloat()
            // if there are no previously tracked kills (during this minecraft instance)
            if (prevValue == -1f) trackKills(1, gained)
            else {
                val actualXpGained = newValue - prevValue
                val killsGained = (actualXpGained / gained).roundToInt()

                if (prevValue != 0f && killsGained >= 0) trackKills(killsGained, gained * killsGained)  // gained * killsGained is more accurate and avoids rounding error
            }
            prevValue = newValue
        }
    }

    private fun trackDrops(drop: GhostDrops, magicFind: Int) {
        val ghostStats = GhostTracker.ghostStats
        val timerStats = GhostTimer.stats

        // drop will never be a coin drop but it has to be added
        when (drop) {
            GhostDrops.SORROW -> ghostStats.sorrowCount++
            GhostDrops.VOLTA -> ghostStats.voltaCount++
            GhostDrops.PLASMA -> ghostStats.plasmaCount++
            GhostDrops.BOOTS -> ghostStats.bootsCount++
            GhostDrops.COINS -> {}
        }

        if (GhostTimer.isTracking) when (drop) {
            GhostDrops.SORROW -> timerStats.sorrowCount++
            GhostDrops.VOLTA -> timerStats.voltaCount++
            GhostDrops.PLASMA -> timerStats.plasmaCount++
            GhostDrops.BOOTS -> timerStats.bootsCount++
            GhostDrops.COINS -> {}
        }

        ghostStats.totalMf += magicFind
        ghostStats.mfDropCount++

        if (GhostTimer.isTracking) {
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