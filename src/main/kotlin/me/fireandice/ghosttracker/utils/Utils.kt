package me.fireandice.ghosttracker.utils

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.utils.dsl.mc
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats

val FONT_HEIGHT by lazy { UMinecraft.getFontRenderer().FONT_HEIGHT }
val gson: Gson = GsonBuilder().setPrettyPrinting().create()
val inGhostArea get() = ScoreboardUtils.inDwarvenMines && mc.thePlayer.posY <= 100

fun String.stripControlCodes(): String = ChatColor.stripControlCodes(this) ?: ""

/**
 * Creates a new [StringBuilder], passes it as the block receiver, and returns it as a [String]
 * @return The result of `StringBuilder::toString()`
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
            // bottom left
            pos(x, y + height, 0.0)
                .tex((u * f).toDouble(), ((v + height.toFloat()) * g).toDouble())
                .endVertex()
            // bottom right
            pos(x + width, y + height, 0.0)
                .tex(((u + width.toFloat()) * f).toDouble(), ((v + height.toFloat()) * g).toDouble())
                .endVertex()
            // top right
            pos(x + width, y, 0.0)
                .tex(((u + width.toFloat()) * f).toDouble(), (v * g).toDouble())
                .endVertex()
            // top left
            pos(x, y, 0.0)
                .tex((u * f).toDouble(), (v * g).toDouble())
                .endVertex()
        }
        draw()
    }
}

/**
 * Mainly for use in for-loops, to iterate backwards without mutating the list
 */
fun <T> Iterable<T>.reverseIterator(): Iterator<T> {
    val list = this.toList()

    return object : Iterator<T> {
        private var current: Int = list.size
        override fun hasNext() = current > 0
        override fun next() = list[--current]
    }
}
