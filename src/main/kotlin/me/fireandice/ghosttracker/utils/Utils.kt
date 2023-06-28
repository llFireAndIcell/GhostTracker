package me.fireandice.ghosttracker.utils

import cc.polyfrost.oneconfig.libs.universal.ChatColor

val PREFIX = "${ChatColor.AQUA}${ChatColor.BOLD}GhostTracker${ChatColor.DARK_GRAY} »${ChatColor.RESET}"

fun String.stripColorCodes(): String = ChatColor.stripColorCodes(this) ?: ""
fun String.stripControlCodes(): String = ChatColor.stripControlCodes(this) ?: ""
