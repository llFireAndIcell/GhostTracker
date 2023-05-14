package me.fireandice.ghosttracker.tracker

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UChat
import me.fireandice.ghosttracker.GhostTracker
import kotlin.properties.Delegates

object GhostTimer {

    private var startTime by Delegates.notNull<Long>()  //  stores the time of the last start OR unpause
    private var totalTime: Long = 0
    var sessionStats = SessionStats
    var isTracking = false
    var isPaused = false

    fun start() {
        startTime = System.currentTimeMillis()
        isTracking = true
        isPaused = false
        UChat.chat("${GhostTracker.PREFIX} ${ChatColor.GREEN}Session tracker started")
    }

    fun pause() {
        totalTime += System.currentTimeMillis() - startTime
        isPaused = true
        UChat.chat("${GhostTracker.PREFIX} ${ChatColor.YELLOW}Session tracker paused")
    }

    fun clear() {
        isTracking = false
        isPaused = false
        SessionStats.reset()
        totalTime = 0
        UChat.chat("${GhostTracker.PREFIX} ${ChatColor.RED}Session tracker reset")
    }

    fun elapsedTime(): Long {
        return totalTime + if (!isPaused) System.currentTimeMillis() - startTime else 0
    }

    object SessionStats {
        var kills: Int = 0
        var totalMf: Int = 0
        var mfDropCount: Int = 0
        var totalXp: Float = 0f

        fun reset() {
            kills = 0
            totalMf = 0
            mfDropCount = 0
            totalXp = 0f
        }
    }
}