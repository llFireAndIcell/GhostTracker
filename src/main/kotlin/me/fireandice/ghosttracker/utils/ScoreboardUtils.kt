package me.fireandice.ghosttracker.utils

import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScorePlayerTeam

/**
 * Modified from https://github.com/inglettronald/DulkirMod under the GNU Affero General Public License v3.0
 */
object ScoreboardUtils {

    /**
     * Only updates after [ScoreboardUtils.checkLocations] is called
     */
    var inSkyblock = false

    /**
     * Only updates after [ScoreboardUtils.checkLocations] is called
     */
    var inDwarvenMines = false

    private val locations: Array<String> = arrayOf(
        "Dwarven Village", "Miner's Guild", "Palace Bridge", "Royal Palace", "Puzzler", "Grand Library",
        "Barracks of Heroes", "Royal Mines", "Cliffside Veins", "Forge Basin", "The Forge", "Rampart's Quarry",
        "Far Reserve", "Upper Mines", "Goblin Burrows", "Great Ice Wall", "Aristocrat Passage", "Hanging Court",
        "Divan's Gateway", "Lava Springs", "The Mist", "Dwarven Mines", "Gates to the Mines", "The Lift"
    )

    private fun getLines(): MutableList<String> {
        if (mc.thePlayer == null || mc.theWorld == null) return emptyList<String>().toMutableList()

        val scoreboard = mc.thePlayer.worldScoreboard
        val sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1)
        val scores: List<Score> = ArrayList(scoreboard.getSortedScores(sidebarObjective))
        val lines: MutableList<String> = ArrayList()

        for (score in scores.reverseIterator()) {
            val scoreplayerteam1 = scoreboard.getPlayersTeam(score.playerName)
            val line = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score.playerName)
            lines.add(line)
        }
        return lines
    }

    /**
     * Called in `EventListener.onTickStart()`
     */
    private fun inSkyblock(): Boolean {
        if (mc.theWorld == null
            || mc.thePlayer == null
            || mc.isSingleplayer
            || mc.thePlayer.clientBrand?.contains("hypixel", true) == false
        ) return false
        val objective = mc.thePlayer.worldScoreboard.getObjectiveInDisplaySlot(1) ?: return false
        return objective.displayName.stripControlCodes().contains("skyblock", true)
    }

    /**
     * Called in `EventListener.onTickStart()`
     */
    fun checkLocations() {
        if (!inSkyblock()) {
            inSkyblock = false
            inDwarvenMines = false
            return
        }

        inSkyblock = true

        val lines = getLines()

        if (lines.size < 5) {
            inDwarvenMines = false
            return
        }

        var line = lines.getOrNull(4)

        if (line != null) {
            line = line.stripControlCodes()
            for (loc in locations) if (loc in line) {
                inDwarvenMines = true
                return
            }
        }
    }

    /**
     * Returns a string of coins in purse. Includes the scavenger coin gain if present. Removes any characters that
     * aren't numbers or spaces.
     */
    fun getPurse(): String? {
        if (!inSkyblock) return null

        val lines = getLines()
        for (line in lines) if (line.contains("Purse|Piggy".toRegex())) {
            return line.stripControlCodes().filter {
                it.isDigit() || it == ' '
            }.trim()
        }

        return null
    }
}