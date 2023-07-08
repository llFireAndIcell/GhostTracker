package me.fireandice.ghosttracker.hud

interface TextComponent {
    /**
     * UNSCALED width
     */
    val width: Float
    var shouldDraw: Boolean
    fun draw(x: Float, y: Float, scale: Float)
}