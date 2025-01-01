package me.fireandice.ghosttracker.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.fireandice.ghosttracker.GhostTracker
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.universal.ChatColor

val gson: Gson = GsonBuilder().setPrettyPrinting().create()
val inGhostArea get() = ScoreboardUtils.inDwarvenMines && mc.thePlayer.posY <= 100

/**
 * A non-nullable version of [ChatColor.stripControlCodes]
 */
fun String.stripControlCodes(): String = ChatColor.stripControlCodes(this) ?: this

/**
 * Creates an [Iterator] that iterates over a list backwards, without mutating the list
 */
fun <T> Iterable<T>.reverseIterator(): Iterator<T> {
    val list = this.toList()

    return object : Iterator<T> {
        private var current: Int = list.size
        override fun hasNext() = current > 0
        override fun next() = list[--current]
    }
}

fun logError(message: String) = GhostTracker.logger.error(message)
fun logError(message: String, throwable: Throwable) = GhostTracker.logger.error(message, throwable)
fun logInfo(message: String) = GhostTracker.logger.info(message)
