package me.fireandice.ghosttracker.config

import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.hud.GhostHud
import me.fireandice.ghosttracker.hud.TimerHud
import me.fireandice.ghosttracker.tracker.GhostTimer
import org.polyfrost.oneconfig.api.config.v1.KtConfig
import org.polyfrost.oneconfig.api.config.v1.annotations.Button
import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.config.v1.annotations.Keybind
import org.polyfrost.oneconfig.api.config.v1.annotations.Number
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindHelper
import org.polyfrost.polyui.input.KeyBinder
import org.polyfrost.polyui.utils.rgba

@Suppress("unused")
object GhostConfig : KtConfig("GhostConfig.json", GhostTracker.NAME, Category.HYPIXEL) {

    //<editor-fold desc="General settings">
    //<editor-fold> desc="General">
    var showEverywhere by switch(
        false,
        "Show everywhere",
        "Show everywhere, instead of only in dwarven mines"
    )

    private val shadowOption by dropdown(
        arrayOf("None", "Shadow", "Full shadow"),
        1,
        "Shadow type"
    )

    /**
     * Use this one
     */
    val shadow: TextType
        get() = when (shadowOption) {
            1 -> TextType.SHADOW
            2 -> TextType.FULL
            else -> TextType.NONE
        }

    var showIcons by switch(true, "Show icons", "Show a small icon to the left of every hud line")

    @Switch(
        title = "Abbreviate text",
        description = "Reduce text on each hud line",
        category = "General"
    )
    var abbreviate = false

    // originally `showPrefixes` was tied to the config option, but I updated the description of the config option and
    // inverted the value to logically match the new description. Instead of changing it everywhere in the code, I just
    // tied `abbreviate` to the config option and invert it
    val showPrefixes get() = !abbreviate

    @Switch(
        title = "Show margins",
        description = "Show the percent difference between drops you've received and the mathematical average",
        category = "General"
    )
    var showMargins = true
    //</editor-fold>

    //<editor-fold> desc="Enchants">
    @Number(
        title = "Looting level",
        category = "General",
        subcategory = "Enchants",
        min = 0f,
        max = 10f,  // in case they add more levels or something idk
    )
    var lootingLevel = 5

    @Number(
        title = "Luck level",
        category = "General",
        subcategory = "Enchants",
        min = 0f,
        max = 10f,  // in case they add more levels or something idk
    )
    var luckLevel = 7
    //</editor-fold>

    //<editor-fold> desc="Price fetching">
    private val priceTimespanOption by dropdown(
        arrayOf("Hour", "Day", "Week"),
        2,
        "Price timespan",
        "The timespan of price data to use and average out"
    )

    /**
     * Use this one
     */
    val priceTimespan: String
        get() = when (priceTimespanOption) {
            0 -> "hour"
            1 -> "day"
            else -> "week"
        }

    @Number(
        title = "Fetch frequency (minutes)",
        category = "General",
        subcategory = "Price Fetching",
        description = "The frequency that api data is refreshed",
        min = 5f,
        max = 120f,
    )
    var priceFrequency: Int = 20
    //</editor-fold>

    //<editor-fold> desc="Colors">
    var killColor by color(rgba(85, 255, 255), "Kill color") // aqua

    var dropColor by color(rgba(85, 85, 255), "Drop color") // blue

    @Color(
        title = "Percent difference color",
        category = "General",
        subcategory = "Colors"
    )
    var marginColor = OneColor(85, 85, 85) // dark gray

    @Color(
        title = "Magic find color",
        category = "General",
        subcategory = "Colors"
    )
    var mfColor = OneColor(255, 170, 0) // gold

    @Color(
        title = "Combat XP color",
        category = "General",
        subcategory = "Colors"
    )
    var xpColor = OneColor(255, 85, 85)         // red

    @Color(
        title = "Time color",
        category = "General",
        subcategory = "Colors"
    )
    var timeColor = OneColor(85, 255, 255)      // aqua

    @Color(
        title = "Money color",
        category = "General",
        subcategory = "Colors"
    )
    var coinColor = OneColor(255, 170, 0)       // gold

    @Color(
        title = "Pause indicator color",
        category = "General",
        subcategory = "Colors"
    )
    var pauseColor = OneColor(85, 85, 85)       // dark gray
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="Stat tracker settings">
    //<editor-fold> desc="Control panel">
    @Button(
        title = "Reset stats",
        text = "Reset",
        category = "Stat Tracker",
        subcategory = "Control Panel"
    )
    var tracker_resetButton = Runnable { GhostTracker.resetStats() }

    @Keybind(
        title = "Stat reset keybind",
        category = "Stat Tracker",
        subcategory = "Control Panel"
    )
    var tracker_resetKb = KeybindHelper().does(GhostTracker::resetStats).register()
    //</editor-fold>

    //<editor-fold> desc="Display info">
    @Switch(
        title = "Show kill count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_kills = true

    @Switch(
        title = "Show sorrow count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_sorrow = true

    @Switch(
        title = "Show volta count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_volta = true

    @Switch(
        title = "Show plasma count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_plasma = true

    @Switch(
        title = "Show ghostly boots count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_boots = true

    @Switch(
        title = "Show 1m coin drop count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_coins = true

    @Switch(
        title = "Show average magic find",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_mf = true

    @Switch(
        title = "Show average combat XP",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_averageXp = true

    @Switch(
        title = "Show total combat XP",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_totalXp = true

    @Switch(
        title = "Show scavenger coins",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_scavenger = true

    @Switch(
        title = "Show total money",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_totalMoney = true
    //</editor-fold>

    @HUD(
        name = "Stats HUD",
        category = "Stat Tracker",
        subcategory = "HUD Settings"
    )
    var tracker_hud = GhostHud()
    //</editor-fold>

    //<editor-fold desc="Session timer settings">
    //<editor-fold> desc="Control panel">
    @Button(
        title = "Reset timer",
        text = "Reset",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var timer_resetButton = Runnable { GhostTimer.reset() }

    @Keybind(
        title = "Reset timer keybind",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var timer_resetKb = OneKeyBind()

    @Button(
        title = "Start/resume timer",
        text = "Start",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var startButton = Runnable { GhostTimer.start() }

    @Keybind(
        title = "Start/pause timer keybind",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var pauseKb = KeyBinder

    @Button(
        title = "Pause timer",
        text = "Pause",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var pauseButton = Runnable { GhostTimer.pause() }
    //</editor-fold>

    //<editor-fold> desc="Display info">
    @Switch(
        title = "Show kills per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_kills = true

    @Switch(
        title = "Show sorrows per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_sorrow = true

    @Switch(
        title = "Show voltas per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_volta = true

    @Switch(
        title = "Show plasmas per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_plasma = true

    @Switch(
        title = "Show ghostly boots per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_boots = true

    @Switch(
        title = "Show 1m coins per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_coins = true

    @Switch(
        title = "Show average magic find",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_mf = true

    @Switch(
        title = "Show average combat XP",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_averageXp = true

    @Switch(
        title = "Show combat XP per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_xpRate = true

    @Switch(
        title = "Show scavenger coins",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_scavenger = true

    @Switch(
        title = "Show money per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_moneyRate = true

    @Switch(
        title = "Show session time",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_time = true
    //</editor-fold>

    @HUD(
        title = "Session timer HUD",
        category = "Session Timer",
        subcategory = "HUD Settings"
    )
    var timer_hud = TimerHud()
    //</editor-fold>

    init {
        registerKeyBind(tracker_resetKb, GhostTracker::resetStats)
        registerKeyBind(timer_resetKb, GhostTimer::reset)
        registerKeyBind(pauseKb) {
            if (GhostTimer.isTracking) GhostTimer.pause()
            else GhostTimer.start()
        }
    }
}
