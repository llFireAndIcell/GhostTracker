package me.fireandice.ghosttracker.hud.elements

import org.polyfrost.polyui.dsl.DrawableDSL

interface HudLine {

    /**
     * Unscaled width
     */
    val width: Float

    /**
     * Unscaled height
     */
    val height: Float

    /**
     * Function that draws the hud line, and calculates width and height (should not factor in scale)
     * @return True if the element was drawn, false otherwise
     */
    fun draw(polyUI: DrawableDSL.Master, x: Float, y: Float, scale: Float): Boolean
}
