package me.fireandice.ghosttracker.tracker

import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import java.util.Scanner

object PurseListener {

    // closing paren can get cut off with enough digits. I think a 10b purse would break this but uhhh just use bank
    private val PURSE_PATTERN = "(?<purse>\\d+)(?: \\(\\+(?<scavenger>\\d+)\\)?)?".toPattern()
    private val COMBO_LOST_PATTERN = "Your Kill Combo has expired! You reached a [\\d,]+ Kill Combo!".toRegex()
    private var previousPurse = -1
    private var previousKills = -1
    private var previousScavenger = -1

    /**
     * Called in `EventListener.onTickStart()`
     */
    fun onTick() {
        val purseString = ScoreboardUtils.getPurse() ?: return

        val scanner = Scanner(purseString)

        val kills = GhostTracker.ghostStats.kills
        val purse: Int = scanner.nextInt()
        val scavenger: Int = if (scanner.hasNextInt()) scanner.nextInt()
        else -1

        // if first tick after joining sb
        if (previousPurse == -1 || previousKills == -1) {
            previousPurse = purse
            previousKills = kills
            return
        }

        val purseGained = purse - previousPurse
        val killsGained = kills - previousKills
        if (purseGained * 1.1 > killsGained * scavenger) return // increase is too large; likely from another source

        GhostTracker.ghostStats.scavenger += purseGained
        if (GhostTimer.isTracking) GhostTimer.stats.scavenger += purseGained

        previousPurse = purse
        previousKills = kills

        GhostTracker.logger.info("Purse increased by $purseGained ($scavenger scavenger)") // TODO debug
    }

    /**
     * Called in `EventListener.onChat()`
     */
    fun detectLostCombo(message: String) {
        if (message.matches(COMBO_LOST_PATTERN)) previousScavenger = -1
    }
}