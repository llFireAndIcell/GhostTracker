package me.fireandice.ghosttracker.tracker

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UChat
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSyntaxException
import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.MOD_DIR
import me.fireandice.ghosttracker.PREFIX
import me.fireandice.ghosttracker.utils.gson
import java.io.File
import java.lang.ClassCastException

object GhostTimer {

    private var startTime = -1L     // stores the time of the last start or unpause
    private var totalTime = 0L      // stores elapsed time up to the last pause
    val elapsedTime: Long           // "stores" the real elapsed time of the timed session
        get() = totalTime + if (isTracking) System.currentTimeMillis() - startTime else 0

    var isTracking = false
    val isPaused
        get() = !isTracking && totalTime != 0L

    var stats = GhostStats()
    var file = File(MOD_DIR, "GhostTimer.json")

    fun start(message: Boolean = true) {
        if (isTracking) {
            if (message) UChat.chat("$PREFIX ${ChatColor.GREEN}Session timer is already running")
            return
        }
        startTime = System.currentTimeMillis()
        isTracking = true
        if (message) UChat.chat("$PREFIX ${ChatColor.GREEN}Session timer started")
    }

    fun pause(message: Boolean = true) {
        if (!isTracking) {
            if (message) UChat.chat("$PREFIX ${ChatColor.YELLOW}Session timer is already inactive")
            return
        }
        totalTime += System.currentTimeMillis() - startTime
        isTracking = false
        if (message) UChat.chat("$PREFIX ${ChatColor.YELLOW}Session timer paused")
    }

    fun reset(message: Boolean = true) {
        isTracking = false
        stats.reset()
        startTime = -1L
        totalTime = 0L
        if (message) UChat.chat("$PREFIX ${ChatColor.RED}Session timer reset")
    }

    fun save() {
        val jsonObj = stats.toJson().apply { add("time", JsonPrimitive(elapsedTime)) }
        val jsonString = gson.toJson(jsonObj)
        file.bufferedWriter().use { it.write(jsonString) }
        GhostTracker.logger.debug("Timer stats saved")
    }

    fun load() {
        pause(false)
        try {
            val jsonString = file.bufferedReader().use { it.readText() }
            val jsonObject: JsonObject = gson.fromJson(jsonString, JsonObject::class.java)

            stats.fromJson(jsonObject)
            totalTime = jsonObject["time"].asLong
        } catch (e: JsonSyntaxException) {
            GhostTracker.logger.error("Couldn't parse timer stats file")
        } catch (e: ClassCastException) {
            GhostTracker.logger.error("Time couldn't be cast to long")
        } catch (_: Exception) {
        }

        GhostTracker.logger.debug("Timer stats loaded")
    }
}