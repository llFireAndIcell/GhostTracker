package me.fireandice.ghosttracker.utils

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import java.io.File

val MOD_DIR = File(File(UMinecraft.getMinecraft().mcDataDir, "config"), "GhostTracker")
const val PREFIX = "§b§lGhostTracker§r§8 »§r"
val FONT_HEIGHT by lazy { UMinecraft.getFontRenderer().FONT_HEIGHT }

fun String.stripControlCodes(): String = ChatColor.stripControlCodes(this) ?: ""
