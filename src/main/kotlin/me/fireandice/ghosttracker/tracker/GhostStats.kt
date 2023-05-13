package me.fireandice.ghosttracker.tracker

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UChat
import com.google.gson.*
import me.fireandice.ghosttracker.GhostTracker
import java.lang.Exception

object GhostStats {

    var sorrowCount: Int = 0
    var voltaCount: Int = 0
    var plasmaCount: Int = 0
    var bootsCount: Int = 0
    var coinsCount: Int = 0
    var kills: Int = 0
    var totalMf: Int = 0
    var mfDropCount: Int = 0
    var totalXp: Float = 0f

    fun load() {
        try {
            val reader = GhostTracker.statsFile.bufferedReader()
            val jsonString = reader.readText()
            reader.close()
            val gson = Gson()
            val jsonObject: JsonObject = gson.fromJson(jsonString, JsonObject::class.java)

            sorrowCount = jsonObject["sorrowCount"]?.asInt ?: 0
            voltaCount = jsonObject["voltaCount"]?.asInt ?: 0
            plasmaCount = jsonObject["plasmaCount"]?.asInt ?: 0
            bootsCount = jsonObject["bootsCount"]?.asInt ?: 0
            coinsCount = jsonObject["coinsCount"]?.asInt ?: 0
            kills = jsonObject["kills"]?.asInt ?: 0
            totalMf = jsonObject["totalMf"]?.asInt ?: 0
            mfDropCount = jsonObject["mfDropCount"]?.asInt ?: 0
            totalXp = jsonObject["totalXp"]?.asFloat ?: 0f
        } catch (_: Exception) {
        }
    }

    fun save() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonObj = JsonObject()
        jsonObj.add("sorrowCount", JsonPrimitive(sorrowCount))
        jsonObj.add("voltaCount", JsonPrimitive(voltaCount))
        jsonObj.add("plasmaCount", JsonPrimitive(plasmaCount))
        jsonObj.add("bootsCount", JsonPrimitive(bootsCount))
        jsonObj.add("coinsCount", JsonPrimitive(coinsCount))
        jsonObj.add("kills", JsonPrimitive(kills))
        jsonObj.add("totalMf", JsonPrimitive(totalMf))
        jsonObj.add("mfDropCount", JsonPrimitive(mfDropCount))
        jsonObj.add("totalXp", JsonPrimitive(totalXp))

        val jsonString = gson.toJson(jsonObj)
        val writer = GhostTracker.statsFile.bufferedWriter()
        writer.write(jsonString)
        writer.close()
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

        UChat.chat("${GhostTracker.PREFIX} ${ChatColor.RED}Main tracker reset")
    }
}