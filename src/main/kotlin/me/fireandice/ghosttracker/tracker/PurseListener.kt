package me.fireandice.ghosttracker.tracker

import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import me.fireandice.ghosttracker.utils.inGhostArea

object PurseListener {

//    private val PURSE_PATTERN = "(?<purse>\\d+)(?: \\(\\+(?<scavenger>\\d+)\\)?)?".toPattern()
//    private val COMBO_LOST_PATTERN = "Your Kill Combo has expired! You reached a [\\d,]+ Kill Combo!".toRegex()
    private var previousPurse: Int? = null

    /**
     * Called in `EventListener.onTickStart()`
     */
    fun onTick() {
        val purseString = ScoreboardUtils.getPurse() ?: return

        val split = purseString.split(" ")

        val purse: Int
        try {
            purse = split[0].toInt()
        } catch (e: NumberFormatException) {
            GhostTracker.logger.error("Couldn't parse purse string as int")
            return
        }

        val scavenger: Int
        try {
            if (split.size > 1) scavenger = split[1].toInt()
            else return
        } catch (e: NumberFormatException) {
            GhostTracker.logger.error("Couldn't parse scavenger string as int")
            return
        }

        if (previousPurse == null) { // first tick after joining sb
            previousPurse = purse
            return
        }
        if (!inGhostArea) { // avoids tracking non ghost kills but still saves the purse value
            previousPurse = purse
            return
        }
        if (scavenger == 1) return // coin talisman

        val purseGained = purse - previousPurse!!
        if (purseGained > 15 * scavenger) return // more than 15 kills worth; probably from another source

        GhostTracker.ghostStats.scavenger += purseGained
        if (GhostTimer.isTracking) GhostTimer.stats.scavenger += purseGained

        previousPurse = purse
    }
}