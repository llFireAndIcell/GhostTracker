package me.fireandice.ghosttracker.utils

import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScorePlayerTeam

/**
 * Taken and modified from https://github.com/inglettronald/DulkirMod
 */
object ScoreboardUtils {

    var inDwarvenMines = false
    var inMists = false

    fun getLines(): MutableList<String> {
        if (mc.thePlayer == null || mc.theWorld == null) return emptyList<String>().toMutableList()

        val scoreboard = mc.thePlayer.worldScoreboard
        val sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1)
        val scores: List<Score> = ArrayList(scoreboard.getSortedScores(sidebarObjective))
        val lines: MutableList<String> = ArrayList()

        for (i in scores.indices.reversed()) {
            val score = scores[i]
            val scoreplayerteam1 = scoreboard.getPlayersTeam(score.playerName)
            val line = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score.playerName)
            lines.add(line)
        }
        return lines
    }

    fun inSkyblock(): Boolean {
        if (mc.theWorld == null || mc.thePlayer == null) return false
        if (mc.isSingleplayer) return false
        if (mc.thePlayer.clientBrand?.contains("hypixel", true) == false) return false
        val objective = mc.thePlayer.worldScoreboard.getObjectiveInDisplaySlot(1) ?: return false
        return objective.displayName.stripColorCodes().contains("skyblock", true)
    }

    private val locations: Array<String> = arrayOf(
        "Dwarven Village", "Miner's Guild", "Palace Bridge", "Royal Palace", "Puzzler", "Grand Library",
        "Barracks of Heroes", "Royal Mines", "Cliffside Veins", "Forge Basin", "The Forge", "Rampart's Quarry",
        "Far Reserve", "Upper Mines", "Goblin Burrows", "Great Ice Wall", "Aristocrat Passage", "Hanging Court",
        "Divan's Gateway", "Lava Springs", "The Mist", "Dwarven Mines", "Gates to the Mines", "The Lift"
    )

    fun checkLocations() {
        if (!inSkyblock()) {
            inDwarvenMines = false
            inMists = false
        }

        val lines = getLines()

        if (lines.size < 5) {
            inDwarvenMines = false
            inMists = false
        }

        var line = lines.getOrNull(4)

        if (line != null) {
            line = line.stripColorCodes()

            if (line.contains("The Mist", true)) {
                inDwarvenMines = true
                inMists = true
                return
            }
            for (loc in locations) if (line.contains(loc, true)) inDwarvenMines = true
        }
    }
}