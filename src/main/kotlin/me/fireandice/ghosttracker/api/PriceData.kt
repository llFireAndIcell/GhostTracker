package me.fireandice.ghosttracker.api

import cc.polyfrost.oneconfig.utils.NetworkUtils
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.config.GhostConfig

object PriceData {

    private const val USER_AGENT: String = "GhostTracker/${GhostTracker.VERSION}"
    // example url: https://sky.coflnet.com/api/bazaar/SORROW/history/day
    private const val BASE_URL: String = "https://sky.coflnet.com/api/bazaar"
    private const val TIMEOUT = 5_000
    private const val PRICE_KEY = "buy"

    private const val SORROW_ITEM_ID = "SORROW"
    private const val SORROW_NPC = 13f
    private const val VOLTA_ITEM_ID = "VOLTA"
    private const val VOLTA_NPC = 1_111f
    private const val PLASMA_ITEM_ID = "PLASMA"
    private const val PLASMA_NPC = 20_000f

    var sorrowPrice: Float = 0f
    var voltaPrice: Float = 0f
    var plasmaPrice: Float = 0f
    val bootsPrice = 77_777f

    fun fetchPrices() {
        val sorrowData = getApiData(SORROW_ITEM_ID)
        val voltaData = getApiData(VOLTA_ITEM_ID)
        val plasmaData = getApiData(PLASMA_ITEM_ID)

        if (sorrowData != null) sorrowPrice = getAverage(sorrowData).coerceAtLeast(SORROW_NPC)
        else GhostTracker.logger.error("Failed to fetch sorrow price")

        if (voltaData != null) voltaPrice = getAverage(voltaData).coerceAtLeast(VOLTA_NPC)
        else GhostTracker.logger.error("Failed to fetch volta price")

        if (plasmaData != null) plasmaPrice = getAverage(plasmaData).coerceAtLeast(PLASMA_NPC)
        else GhostTracker.logger.error("Failed to fetch plasma price")
    }

    private fun getApiData(itemId: String): JsonArray? {
        var response: JsonArray? = null
        val url = "$BASE_URL/$itemId/history/${GhostConfig.priceTimespanString()}"
        try {
            response = NetworkUtils.getJsonElement(
                url,
                USER_AGENT,
                TIMEOUT,
                false
            ).asJsonArray
        } catch (e: IllegalStateException) {
            GhostTracker.logger.error("Failed to get json array from $url")
        }

        return response
    }

    private fun getAverage(jsonArray: JsonArray): Float {
        var total = 0f
        var count = 0

        for (jsonElement: JsonElement in jsonArray) {
            try {
                total += (jsonElement as JsonObject)[PRICE_KEY].asFloat
            } catch (e: ClassCastException) {
                GhostTracker.logger.error("Couldn't parse price data as a float")
                continue
            }
            count++
        }

        return total / count
    }
}