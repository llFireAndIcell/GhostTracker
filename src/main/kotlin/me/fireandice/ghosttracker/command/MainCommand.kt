package me.fireandice.ghosttracker.command

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UChat
import me.fireandice.ghosttracker.GhostConfig
import me.fireandice.ghosttracker.GhostTracker
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
            "cleargen", "resetgen" -> {
                GhostTracker.ghostStats.reset()
                UChat.chat("${GhostTracker.PREFIX} ${ChatColor.RED}Main tracker reset")
            }
            else -> GhostConfig.openGui()
        }
    }
}