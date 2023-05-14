package me.fireandice.ghosttracker.command

import me.fireandice.ghosttracker.GhostConfig
import me.fireandice.ghosttracker.tracker.GhostStats
import me.fireandice.ghosttracker.tracker.GhostTimer
import net.minecraft.command.ICommandSender

object MainCommand : CommandWrapper("ghost") {

    override fun processCommand(sender: ICommandSender?, args: Array<String>?) {
        if (args.isNullOrEmpty()) {
            GhostConfig.openGui()
            return
        }

        when (args[0]) {
            "start" -> GhostTimer.start()
            "stop", "pause" -> GhostTimer.pause()
            "clear", "reset" -> GhostTimer.clear()
            "cleargen", "resetgen" -> GhostStats.reset()
            else -> GhostConfig.openGui()
        }
    }
}