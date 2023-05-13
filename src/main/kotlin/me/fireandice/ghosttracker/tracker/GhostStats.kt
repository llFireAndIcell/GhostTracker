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
            val gson = Gson()
            val map: Map<*, *>? = gson.fromJson(reader, Map::class.java)

            if (map != null) {
//                sorrowCount = map["sorrowCount"] as Int
//                voltaCount = map["voltaCount"] as Int
//                plasmaCount = map["plasmaCount"] as Int
//                bootsCount = map["bootsCount"] as Int
//                coinsCount = map["coinsCount"] as Int
//                kills = map["kills"] as Int
//                totalMf = map["totalMf"] as Int
//                mfDropCount = map["mfDropCount"] as Int
//                totalXp = map["totalXp"] as Float

                for (entry in map) {
                    val key = entry.key as String
                    val value = entry.value as Number
                    
                    // attempts storing the value in the field with the same name, ignoring it if there is none
                    try {
                        this.javaClass.getField(key).set(this, value)
                    } catch (_: NoSuchFieldException) {
                    }
                }
            }
            reader.close()
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