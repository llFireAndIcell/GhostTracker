package me.fireandice.ghosttracker.hud.elements

interface HudLine {

    val width: Float
    val height: Float

    fun draw(x: Float, y: Float, scale: Float): Boolean
}