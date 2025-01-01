package me.fireandice.ghosttracker.hud

import me.fireandice.ghosttracker.GhostTracker
import net.minecraft.util.ResourceLocation
import org.polyfrost.polyui.data.PolyImage

object Images {
    val Kills = PolyImage(ResourceLocation(GhostTracker.MODID, "kills.png").resourcePath)
    val Sorrow = PolyImage(ResourceLocation(GhostTracker.MODID, "sorrow.png").resourcePath)
    val Volta = PolyImage(ResourceLocation(GhostTracker.MODID, "volta.png").resourcePath)
    val Plasma = PolyImage(ResourceLocation(GhostTracker.MODID, "plasma.png").resourcePath)
    val Boots = PolyImage(ResourceLocation(GhostTracker.MODID, "ghostly-boots.png").resourcePath)
    val Coins = PolyImage(ResourceLocation(GhostTracker.MODID, "coin-drop.png").resourcePath)
    val MagicFind = PolyImage(ResourceLocation(GhostTracker.MODID, "magic-find.png").resourcePath)
    val CombatXp = PolyImage(ResourceLocation(GhostTracker.MODID, "combat.png").resourcePath)
    val Time = PolyImage(ResourceLocation(GhostTracker.MODID, "time.png").resourcePath)
    val Money = PolyImage(ResourceLocation(GhostTracker.MODID, "money.png").resourcePath)
}
