package me.fireandice.ghosttracker.test

import me.fireandice.ghosttracker.api.PriceData
import me.fireandice.ghosttracker.config.GhostConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

object PriceDataTest {

    @Test
    fun testFetchPrices() {
        val oldPriceFrequency = GhostConfig.priceFrequency
        GhostConfig.priceFrequency = 0
        PriceData.fetchPrices()

        Assertions.assertFalse(PriceData.sorrowPrice < 13f, "Sorrow price shouldn't be lower than npc price")
        Assertions.assertFalse(PriceData.voltaPrice < 1_111f, "Volta price shouldn't be lower than npc price")
        Assertions.assertFalse(PriceData.plasmaPrice < 20_000f, "Plasma price shouldn't be lower than npc price")

        GhostConfig.priceFrequency = oldPriceFrequency
    }
}