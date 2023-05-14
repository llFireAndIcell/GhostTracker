package me.fireandice.ghosttracker.utils

import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import net.minecraft.util.StringUtils

val mc = UMinecraft.getMinecraft()

fun String.stripColorCodes(): String = StringUtils.stripControlCodes(this)