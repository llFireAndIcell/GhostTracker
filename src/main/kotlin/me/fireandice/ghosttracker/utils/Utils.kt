package me.fireandice.ghosttracker.utils

import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import net.minecraft.util.StringUtils
import kotlin.math.pow
import kotlin.math.roundToInt

val mc = UMinecraft.getMinecraft()

/**
 * Rounds a number to any particular digit
 * @param digits The power of the digit you want to round to, e.g. the 100's place would be 2 and the 1/1000's place
 * would be -3
 * @return The rounded number as a float
 */
fun Float.roundToDigits(digits: Int): Float {
    if (digits < 0) return (this * 10f.pow((-digits))).roundToInt() / 10f.pow(-digits)
    return (this / 10f.pow(digits)).roundToInt() * 10f.pow(digits)
}

fun String.stripColorCodes(): String = StringUtils.stripControlCodes(this)