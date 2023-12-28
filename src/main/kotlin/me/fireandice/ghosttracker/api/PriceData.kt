package me.fireandice.ghosttracker.api

import cc.polyfrost.oneconfig.utils.NetworkUtils
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import me.fireandice.ghosttracker.GhostTracker

object PriceData {

    private const val USER_AGENT: String = "GhostTracker/${GhostTracker.VERSION}"
    private const val URL: String = "https://sky.shiiyu.moe/api/v2/bazaar"
    private const val PRICE_KEY = "buyPrice"
    private const val SORROW_ITEM_ID = "SORROW"
    private const val VOLTA_ITEM_ID = "VOLTA"
    private const val PLASMA_ITEM_ID = "PLASMA"

    var sorrowPrice = 0f
    var voltaPrice = 0f
    var plasmaPrice = 0f
    val bootsPrice = 77_777f

    fun fetchPrices() {
        val json: JsonObject

        try {
            json = NetworkUtils.getJsonElement(URL, USER_AGENT, 5_000, false) as JsonObject
        } catch (e: JsonParseException) {
            return
        } catch (e: JsonSyntaxException) {
            return
        }

        sorrowPrice = json.getPrice(SORROW_ITEM_ID)
        voltaPrice = json.getPrice(VOLTA_ITEM_ID, 1_111f)
        plasmaPrice = json.getPrice(PLASMA_ITEM_ID, 20_000f)
    }

    private fun JsonObject.getPrice(key: String, min: Float = Float.POSITIVE_INFINITY): Float {
        return this[key].asJsonObject[PRICE_KEY].asFloat.coerceAtLeast(min)
    }
}