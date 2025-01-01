package me.fireandice.ghosttracker.tracker

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import me.fireandice.ghosttracker.MOD_DIR
import me.fireandice.ghosttracker.PREFIX
import me.fireandice.ghosttracker.utils.gson
import me.fireandice.ghosttracker.utils.logError
import me.fireandice.ghosttracker.utils.logInfo
import org.polyfrost.universal.ChatColor
import org.polyfrost.universal.UChat
import java.io.File

object GhostTimer {

    private var startTime = -1L     // stores the time of the last start or unpause
    private var totalTime = 0L      // stores elapsed time up to the last pause
    val elapsedTime: Long           // elapsed time of the timed session
        get() = totalTime + if (isTracking) System.currentTimeMillis() - startTime else 0

    var isTracking = false
    val isPaused
        get() = !isTracking && totalTime != 0L

    var stats = GhostStats()
    const val FILE_NAME = "GhostTimer.json"
    var file = File(MOD_DIR, FILE_NAME)

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
        val jsonObject = JsonParser().parse(stats.toJson()).asJsonObject.apply {
            addProperty("time", elapsedTime)
        }
        file.bufferedWriter().use { it.write(gson.toJson(jsonObject)) }
        logInfo("Timer stats saved")
    }

    fun load() {
        pause(false)
        try {
            val jsonString = file.bufferedReader().use { it.readText() }
            val jsonObject: JsonObject = gson.fromJson(jsonString, JsonObject::class.java)

            stats = GhostStats.fromJson(jsonObject)
            totalTime = jsonObject["time"].asLong
        } catch (_: JsonSyntaxException) {
            logError("Couldn't parse timer stats file")
        } catch (_: ClassCastException) {
            logError("Time couldn't be cast to long")
        } catch (e: Exception) {
            logError("Error parsing timer stats", e)
        }

        logInfo("Timer stats loaded")
    }
}
