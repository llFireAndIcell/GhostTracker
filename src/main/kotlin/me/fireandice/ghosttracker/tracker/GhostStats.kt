package me.fireandice.ghosttracker.tracker

import com.google.gson.JsonObject
import me.fireandice.ghosttracker.api.PriceData
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.tracker.GhostDrops.*
import me.fireandice.ghosttracker.utils.gson
import java.text.DecimalFormat

data class GhostStats(
    var sorrowCount: Int = 0,
    var voltaCount: Int = 0,
    var plasmaCount: Int = 0,
    var bootsCount: Int = 0,
    var coinsCount: Int = 0,
    var kills: Int = 0,
    var totalMf: Int = 0,
    var mfDropCount: Int = 0,
    var totalXp: Float = 0f,
    var scavenger: Int = 0,
) {

    val totalValue: Int
        get() = (sorrowCount * PriceData.sorrowPrice +
                voltaCount * PriceData.voltaPrice +
                plasmaCount * PriceData.plasmaPrice +
                bootsCount * PriceData.bootsPrice +
                coinsCount * 1_000_000 +
                scavenger).toInt()

    @Transient
    var dirty = false

    private fun getAverageMf(): Float? {
        if (mfDropCount > 0) return totalMf.toFloat() / mfDropCount
        return null
    }

    fun getAverageMf(format: DecimalFormat): String {
        val mf = getAverageMf() ?: return "-"
        return format.format(mf)
    }

    fun getAverageXp(format: DecimalFormat): String {
        if (kills == 0) return "-"
        return format.format(totalXp / kills)
    }

    private fun getRelativeDifference(drop: GhostDrops): Float? {
        if (kills == 0) return null
        var chanceModifier = 1f

        val actual = when (drop) {
            Sorrow -> {
                chanceModifier += (getAverageMf() ?: 0f) / 100
                chanceModifier += GhostConfig.lootingLevel.toFloat() * 0.15f
                sorrowCount
            }

            Volta -> {
                chanceModifier += (getAverageMf() ?: 0f) / 100
                chanceModifier += GhostConfig.lootingLevel.toFloat() * 0.15f
                voltaCount
            }

            Plasma -> {
                chanceModifier += (getAverageMf() ?: 0f) / 100
                chanceModifier += GhostConfig.lootingLevel.toFloat() * 0.15f
                plasmaCount
            }

            Boots -> {
                chanceModifier += (getAverageMf() ?: 0f) / 100
                chanceModifier += GhostConfig.luckLevel.toFloat() * 0.05f
                bootsCount
            }

            Coins -> coinsCount
        }
        if (actual == 0) return null    // this would display "-100.00%" which I don't really want

        val theoretical = kills * drop.baseChance * chanceModifier

        return (actual - theoretical) / theoretical
    }

    fun getPercentDifference(drop: GhostDrops, format: DecimalFormat): String {
        val diff = getRelativeDifference(drop) ?: return ""
        val percentString = format.format(diff * 100)
        if (diff >= 0) return " (+$percentString%)"
        return " ($percentString%)"    // it already puts the - sign there if it's negative
    }

    fun reset() {
        sorrowCount = 0
        voltaCount = 0
        plasmaCount = 0
        bootsCount = 0
        coinsCount = 0
        kills = 0
        totalMf = 0
        mfDropCount = 0
        totalXp = 0f
        scavenger = 0
    }

    fun toJson(): String? = gson.toJson(this)

    companion object {
        fun fromJson(json: JsonObject): GhostStats = gson.fromJson(json, GhostStats::class.java)
    }
}
