package me.fireandice.ghosttracker.command

import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.tracker.GhostTimer
import net.minecraft.command.ICommandSender

object MainCommand : AbstractCommand("ghost") {

    override fun processCommand(sender: ICommandSender?, args: Array<String>?) {
        if (args.isNullOrEmpty()) {
            GhostConfig.openGui()
            return
        }

        when (args[0]) {
            "start" -> GhostTimer.start()
            "pause", "stop" -> GhostTimer.pause()
            "reset", "clear" -> GhostTimer.reset()
            "stats" -> if (args.size >= 2) when (args[1]) {
                "reset", "clear" -> GhostTracker.resetStats()
            }
            else -> GhostConfig.openGui()
        }
    }
}