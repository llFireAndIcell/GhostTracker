package me.fireandice.ghosttracker.hud

interface TextComponent {
    var width: Float
    var shouldDraw: Boolean
    fun draw(x: Float, y: Float, scale: Float)
}