package me.fireandice.ghosttracker.tracker

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UChat
import me.fireandice.ghosttracker.GhostTracker

object GhostTimer {

    private var startTime = -1L  // stores the time of the last start or unpause
    private var totalTime = 0L
    var stats = GhostStats()
    var isTracking = false  // if timer is currently running

    fun start(message: Boolean = true) {
        if (isTracking) {
            UChat.chat("${GhostTracker.PREFIX} ${ChatColor.GREEN}Session timer is already running")
            return
        }
        startTime = System.currentTimeMillis()
        isTracking = true
        if (message) UChat.chat("${GhostTracker.PREFIX} ${ChatColor.GREEN}Session timer started")
    }

    fun pause(message: Boolean = true) {
        if (!isTracking) {
            UChat.chat("${GhostTracker.PREFIX} ${ChatColor.YELLOW}Session timer is already inactive")
            return
        }
        totalTime += System.currentTimeMillis() - startTime
        isTracking = false
        if (message) UChat.chat("${GhostTracker.PREFIX} ${ChatColor.YELLOW}Session timer paused")
    }

    fun clear(message: Boolean = true) {
        isTracking = false
        stats.reset()
        startTime = -1L
        totalTime = 0L
        if (message) UChat.chat("${GhostTracker.PREFIX} ${ChatColor.RED}Session timer reset")
    }

    fun elapsedTime(): Long {
        if (startTime == -1L) return 0
        return totalTime + if (isTracking) System.currentTimeMillis() - startTime else 0
    }
}