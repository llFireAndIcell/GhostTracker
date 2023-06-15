package me.fireandice.ghosttracker.hud

interface TextComponent {
    var width: Float
    fun draw(x: Float, y: Float, scale: Float)
}