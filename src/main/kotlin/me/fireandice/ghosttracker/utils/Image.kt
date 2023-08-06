package me.fireandice.ghosttracker.utils

import net.minecraft.util.ResourceLocation

data class Image(
    val location: ResourceLocation,
    val width: Number,
    val height: Number
) {
    override fun toString(): String = "${location.resourceDomain}${location.resourcePath}"
}