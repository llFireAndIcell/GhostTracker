package me.fireandice.ghosttracker.tracker

import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.utils.ScoreboardUtils

object PurseListener {

    // closing paren can get cut off with enough digits. I think a 10b purse would break this but uhhh just use bank
    private val PURSE_PATTERN = "(?<purse>[\\d,]+)( \\(\\+(?<scavenger>[\\d,]+)\\)?)?".toPattern()
    private val COMBO_LOST_PATTERN = "Your Kill Combo has expired! You reached a [\\d,] Kill Combo!".toRegex()
    private var previousPurse = -1
    private var previousKills = -1
    private var previousScavenger = -1

    fun onTick() {
        val purseString = ScoreboardUtils.getPurse() ?: return

        val matcher = PURSE_PATTERN.matcher(purseString)
        if (!matcher.matches()) return

        val kills = GhostTracker.ghostStats.kills
        val purse: Int
        val scavenger: Int

        try {
            purse = matcher.group("purse").toInt()
            val scavengerMatch = matcher.group("scavenger")

            scavenger =
                if (previousScavenger == -1 && scavengerMatch == null) previousScavenger
                else scavengerMatch?.toInt() ?: return

        } catch (e: NumberFormatException) {
            GhostTracker.logger.error("Couldn't parse purse or scavenger number")
            return
        }

        // if first tick after joining sb
        if (previousPurse == -1 || previousKills == -1) {
            previousPurse = purse
            previousKills = kills
            return
        }

        val purseGained = purse - previousPurse
        val killsGained = kills - previousKills
        if (purseGained > killsGained * scavenger) return // increase is too large; likely from another source

        GhostTracker.ghostStats.scavenger += purseGained
        if (GhostTimer.isTracking) GhostTimer.stats.scavenger += purseGained

        previousPurse = purse
        previousKills = kills

        GhostTracker.logger.info("Purse increased by $purseGained ($scavenger scavenger)") // TODO debug
    }

    fun detectLostCombo(message: String) {
        if (message.matches(COMBO_LOST_PATTERN)) previousScavenger = -1
    }
}