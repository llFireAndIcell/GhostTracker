package me.fireandice.ghosttracker.utils

import net.minecraft.util.StringUtils

fun String.stripColorCodes(): String = StringUtils.stripControlCodes(this)