package me.fireandice.ghosttracker.utils

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UMinecraft

val PREFIX = "${ChatColor.AQUA}${ChatColor.BOLD}GhostTracker${ChatColor.DARK_GRAY} »${ChatColor.RESET}"
val FONT_HEIGHT = UMinecraft.getFontRenderer().FONT_HEIGHT

fun String.stripControlCodes(): String = ChatColor.stripControlCodes(this) ?: ""
