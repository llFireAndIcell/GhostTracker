package me.fireandice.ghosttracker.tracker

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
    private val COIN_DROP_MESSAGE = "§r§eThe ghost's death materialized §r§61,000,000 coins §r§efrom the mists!§r"
    private val COMBAT_XP_PATTERN = Pattern.compile("\\+(?<gained>[\\d.]+) Combat \\((?<progress>.+)\\)")
    private var prevValue = 0f

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!ScoreboardUtils.inMists || event.type !in 0..1) return
        val message = event.message.formattedText

        // Detecting one of the various normal rng drops
        val matcher = RARE_DROP_PATTERN.matcher(message)

        if (matcher.matches()) {

            when (matcher.group("drop")) {
                "Sorrow" -> GhostStats.sorrowCount++
                "Volta" -> GhostStats.voltaCount++
                "Plasma" -> GhostStats.plasmaCount++
                "Ghostly Boots" -> GhostStats.bootsCount++
                else -> return  // do not count magic find of non-ghost drops
            }
            val numberFormat = NumberFormat.getInstance(Locale.US)
            val mf = numberFormat.parse(matcher.group("mf"))
            GhostStats.totalMf += mf.toInt()
            GhostStats.mfDropCount++

            if (SessionTracker.isTracking && !SessionTracker.isPaused) {
                SessionTracker.sessionStats.totalMf += mf.toInt()
                SessionTracker.sessionStats.mfDropCount++
            }
        }

        // Detecting if 1m coins dropped
        if (COIN_DROP_MESSAGE == message) GhostStats.coinsCount++
    }

    /**
     * Most of the logic was taken from https://www.chattriggers.com/modules/v/GhostCounterV3
     */
    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    fun onActionBar(event: ClientChatReceivedEvent) {
        if (!ScoreboardUtils.inMists || event.type != 2.toByte()) return
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
            val actualXpGained = newValue - prevValue
            val killsGained = (actualXpGained / gained).roundToInt()

            if (prevValue != 0f && killsGained >= 0) {
                GhostStats.kills += killsGained
                GhostStats.totalXp += actualXpGained

                if (SessionTracker.isTracking && !SessionTracker.isPaused) {
                    SessionTracker.sessionStats.kills += killsGained
                    SessionTracker.sessionStats.totalXp += actualXpGained
                }
            }
            prevValue = newValue
        }
    }
}