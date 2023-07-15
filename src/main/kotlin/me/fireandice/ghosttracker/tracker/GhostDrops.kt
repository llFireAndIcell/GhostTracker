package me.fireandice.ghosttracker.tracker

enum class GhostDrops(val baseChance: Float) {
    Sorrow(0.0012f), Volta(0.002f), Plasma(0.001f), Boots(0.0001f), Coins(0.0001f);

    companion object {
        fun get(name: String): GhostDrops? {
            // 'Coins' is excluded from this because it has no formal name, so this method isn't useful for it
            return when (name) {
                "Sorrow" -> Sorrow
                "Volta" -> Volta
                "Plasma" -> Plasma
                "Ghostly Boots" -> Boots
                else -> null
            }
        }
    }
}