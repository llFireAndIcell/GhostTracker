package me.fireandice.ghosttracker.utils

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats

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

/**
 * An implementation similar to [Gui.drawModalRectWithCustomSizedTexture], but accepts [Double]s in place of [Int]s to
 * allow more granularity when rendering
 */
fun drawTexturedRect(x: Double, y: Double, u: Float, v: Float, width: Double, height: Double, textureWidth: Float, textureHeight: Float) {
    val f: Float = 1.0f / textureWidth
    val g: Float = 1.0f / textureHeight
    Tessellator.getInstance().apply {
        worldRenderer.apply {
            begin(7, DefaultVertexFormats.POSITION_TEX)
            pos(x, y + height, 0.0).tex((u * f).toDouble(), ((v + height.toFloat()) * g).toDouble()).endVertex()
            pos(x + width, y + height, 0.0).tex(((u + width.toFloat()) * f).toDouble(), ((v + height.toFloat()) * g).toDouble()).endVertex()
            pos(x + width, y, 0.0).tex(((u + width.toFloat()) * f).toDouble(), (v * g).toDouble()).endVertex()
            pos(x, y, 0.0).tex((u * f).toDouble(), (v * g).toDouble()).endVertex()
        }
        draw()
    }
}
