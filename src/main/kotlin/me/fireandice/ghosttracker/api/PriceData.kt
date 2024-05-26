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
    private const val VOLTA_ITEM_ID = "VOLTA"
    private const val PLASMA_ITEM_ID = "PLASMA"

    private const val SORROW_NPC = 13f
    private const val VOLTA_NPC = 1_111f
    private const val PLASMA_NPC = 20_000f

    var sorrowPrice: Float = SORROW_NPC
    var voltaPrice: Float = VOLTA_NPC
    var plasmaPrice: Float = PLASMA_NPC
    @Suppress("ConstPropertyName") // I want the name to be consistent with the others
    const val bootsPrice = 77_777f

    private var lastFetch: Long = -1L

    /**
     * Updates price data. Called in `EventListener.onTickStart()`
     */
    fun fetchPrices() {
        val time = System.currentTimeMillis()
        if (time - lastFetch <= GhostConfig.priceFrequency.toLong() * 60_000) return

        lastFetch = time

        val sorrowJson = getApiData(SORROW_ITEM_ID)
        val voltaJson = getApiData(VOLTA_ITEM_ID)
        val plasmaJson = getApiData(PLASMA_ITEM_ID)

        if (sorrowJson != null) {
            sorrowPrice = getAverage(sorrowJson).coerceAtLeast(SORROW_NPC)
            GhostTracker.logger.info("Fetched sorrow price: $sorrowPrice")
        }
        else GhostTracker.logger.error("Failed to fetch sorrow price")

        if (voltaJson != null) {
            voltaPrice = getAverage(voltaJson).coerceAtLeast(VOLTA_NPC)
            GhostTracker.logger.info("Fetched volta price: $voltaPrice")
        }
        else GhostTracker.logger.error("Failed to fetch volta price")

        if (plasmaJson != null) {
            plasmaPrice = getAverage(plasmaJson).coerceAtLeast(PLASMA_NPC)
            GhostTracker.logger.info("Fetched plasma price: $plasmaPrice")
        }
        else GhostTracker.logger.error("Failed to fetch plasma price")
    }

    /**
     * Makes the api request and gets the response as a json array
     */
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