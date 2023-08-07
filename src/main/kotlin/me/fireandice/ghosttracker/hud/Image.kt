package me.fireandice.ghosttracker.hud

import me.fireandice.ghosttracker.GhostTracker
import net.minecraft.util.ResourceLocation

class Image private constructor(
    val location: ResourceLocation,
    val width: Number,
    val height: Number
) {

    companion object {
        val Kills = Image(ResourceLocation(GhostTracker.MODID, "kills.png"), 160, 160)
        val Sorrow = Image(ResourceLocation(GhostTracker.MODID, "sorrow.png"), 16, 16)
        val Volta = Image(ResourceLocation(GhostTracker.MODID, "volta.png"), 185, 185)
        val Plasma = Image(ResourceLocation(GhostTracker.MODID, "plasma.png"), 185, 185)
        val Boots = Image(ResourceLocation(GhostTracker.MODID, "ghostly boots.png"), 160, 160)
        val Coins = Image(ResourceLocation(GhostTracker.MODID, "coins.png"), 185, 185)
        val MagicFind = Image(ResourceLocation(GhostTracker.MODID, "magic find.png"), 160, 160)
        val CombatXp = Image(ResourceLocation(GhostTracker.MODID, "xp.png"), 500, 500)
        val Time = Image(ResourceLocation(GhostTracker.MODID, "time.png"), 16, 16)
    }

    override fun toString(): String = "${location.resourceDomain}:${location.resourcePath}"
}