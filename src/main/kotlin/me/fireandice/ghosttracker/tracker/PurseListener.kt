package me.fireandice.ghosttracker.tracker

import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.utils.ScoreboardUtils

object PurseListener {

    private val PURSE_PATTERN = "(?<purse>[\\d,]+)( \\(\\+(?<scavenger>[\\d,]+)\\))?".toPattern()
    private var previousPurse = -1
    private var previousKills = -1

    fun onTick() {
        val purseString = ScoreboardUtils.getPurse() ?: return

        val matcher = PURSE_PATTERN.matcher(purseString)
        if (!matcher.matches()) return
        // if there is no scavenger coin increase then ignore any changes to purse
        if (matcher.group("scavenger") == null) return

        val kills = GhostTracker.ghostStats.kills
        val purse: Int
        val scavenger: Int

        try {
            purse = matcher.group("purse").toInt()
            scavenger = matcher.group("scavenger").toInt()
        } catch (e: NumberFormatException) {
            return
        }

        if (previousPurse == -1 || previousKills == -1) {
            previousPurse = purse
            previousKills = kills
            return
        }

        val purseGained = purse - previousPurse
        val killsGained = kills - previousKills
        if (purseGained > killsGained * scavenger) return   // increase is too large; likely from another source

        GhostTracker.ghostStats.scavenger += purseGained
        if (GhostTimer.isTracking) GhostTimer.stats.scavenger += purseGained
    }
}