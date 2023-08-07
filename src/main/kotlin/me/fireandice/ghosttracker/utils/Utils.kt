package me.fireandice.ghosttracker.utils

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import com.google.gson.Gson
import com.google.gson.GsonBuilder

val FONT_HEIGHT by lazy { UMinecraft.getFontRenderer().FONT_HEIGHT }

val gson: Gson = GsonBuilder().setPrettyPrinting().create()

fun String.stripControlCodes(): String = ChatColor.stripControlCodes(this) ?: ""

/**
 * Creates a new [StringBuilder], passes it as the block receiver, and returns it as a [String]
 * @return The result of [StringBuilder::toString()]
 */
inline fun stringBuilder(block: StringBuilder.() -> Unit): String {
    val builder = StringBuilder()
    builder.block()
    return builder.toString()
}
