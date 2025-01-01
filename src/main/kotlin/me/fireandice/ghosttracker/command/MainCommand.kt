package me.fireandice.ghosttracker.command

import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.tracker.GhostTimer
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Command
import org.polyfrost.oneconfig.utils.v1.dsl.openUI

@Command("ghost")
object MainCommand {

    @Command
    fun main() {
        GhostConfig.openUI()
        return
    }

    @Command(greedy = true)
    fun controlTimer(action: Array<String>) {
        when (action.joinToString(" ")) {
            "start" -> GhostTimer.start()
            "pause", "stop" -> GhostTimer.pause()
            "reset", "clear" -> GhostTimer.reset()
            "stats reset" -> GhostTracker.resetStats()
        }
    }
}
