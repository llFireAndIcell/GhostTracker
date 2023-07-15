package me.fireandice.ghosttracker.utils

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UMinecraft

val FONT_HEIGHT by lazy { UMinecraft.getFontRenderer().FONT_HEIGHT }

fun String.stripControlCodes(): String = ChatColor.stripControlCodes(this) ?: ""
