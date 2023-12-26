package me.fireandice.ghosttracker.tracker

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import me.fireandice.ghosttracker.config.GhostConfig
import me.fireandice.ghosttracker.tracker.GhostDrops.*
import java.text.DecimalFormat

class GhostStats {

    // for json parsing
    private var stats: MutableMap<String, Number> = mutableMapOf(
        "sorrowCount" to 0,
        "voltaCount" to 0,
        "plasmaCount" to 0,
        "bootsCount" to 0,
        "coinsCount" to 0,
        "kills" to 0,
        "totalMf" to 0,
        "mfDropCount" to 0,
        "totalXp" to 0f,
        "scavenger" to 0
    )

    var sorrowCount: Int by stats
    var voltaCount: Int by stats
    var plasmaCount: Int by stats
    var bootsCount: Int by stats
    var coinsCount: Int by stats
    var kills: Int by stats
    var totalMf: Int by stats
    var mfDropCount: Int by stats
    var totalXp: Float by stats
    var scavenger: Int by stats

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
    }

    fun toJson() = JsonObject().apply { for (stat in stats) add(stat.key, JsonPrimitive(stat.value)) }

    fun fromJson(json: JsonObject) {
        for (stat in stats) {
            val jsonElement = json[stat.key] ?: continue
            try {
                stats[stat.key] = jsonElement.asFloat
            } catch (_: Exception) {
            }
        }
    }
}