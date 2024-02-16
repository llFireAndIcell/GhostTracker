package me.fireandice.ghosttracker.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.renderer.TextRenderer.TextType
import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.hud.GhostHud
import me.fireandice.ghosttracker.hud.TimerHud
import me.fireandice.ghosttracker.tracker.GhostTimer

@Suppress("unused")
object GhostConfig : Config(Mod(GhostTracker.NAME, ModType.SKYBLOCK), "GhostConfig.json") {

    //<editor-fold desc="General settings">
    @Switch(
        name = "Show everywhere",
        description = "Show everywhere, instead of only in dwarven mines",
        category = "General"
    )
    var showEverywhere = false
    @Deprecated("Not actually deprecated, just shouldn't be used",
        ReplaceWith("GhostConfig.shadow()"),
        DeprecationLevel.WARNING
    )
    @Dropdown(
        name = "Shadow type",
        options = ["None", "Shadow", "Full shadow"],
        category = "General"
    )
    var shadow = 1
    @Switch(
        name = "Show icons",
        description = "Show a small icon to the left of every hud line",
        category = "General"
    )
    var showIcons = true
    @Switch(
        name = "Show prefixes",
        description = "Show text before every hud line (and after the icon) that describes it",
        category = "General"
    )
    var showPrefixes = true
    @Switch(
        name = "Show margins",
        description = "Show the percent difference between drops you've received and the mathematical average",
        category = "General"
    )
    var showMargins = true

    @Number(
        name = "Looting level",
        category = "General",
        subcategory = "Enchants",
        min = 0f,
        max = 10f,  // in case they add more levels or something idk
        step = 1
    )
    var lootingLevel = 5
    @Number(
        name = "Luck level",
        category = "General",
        subcategory = "Enchants",
        min = 0f,
        max = 10f,  // in case they add more levels or something idk
        step = 1
    )
    var luckLevel = 7

    @Dropdown(
        name = "Price timespan",
        category = "General",
        subcategory = "Price Fetching",
        description = "The timespan of price data to use and average out",
        options = ["Hour", "Day", "Week"]
    )
    var priceTimespan: Int = 2
    @Number(
        name = "Fetch frequency (minutes)",
        category = "General",
        subcategory = "Price Fetching",
        description = "The frequency that api data is refreshed",
        min = 5f,
        max = 120f,
        step = 1
    )
    var priceFrequency: Int = 20

    @Color(
        name = "Kill color",
        category = "General",
        subcategory = "Colors"
    )
    var killColor = OneColor(85, 255, 255)      // aqua
    @Color(
        name = "Drop color",
        category = "General",
        subcategory = "Colors"
    )
    var dropColor = OneColor(85, 85, 255)       // blue
    @Color(
        name = "Percent difference color",
        category = "General",
        subcategory = "Colors"
    )
    var marginColor = OneColor(85, 85, 85)      // dark gray
    @Color(
        name = "Magic find color",
        category = "General",
        subcategory = "Colors"
    )
    var mfColor = OneColor(255, 170, 0)         // gold
    @Color(
        name = "Combat XP color",
        category = "General",
        subcategory = "Colors"
    )
    var xpColor = OneColor(255, 85, 85)         // red
    @Color(
        name = "Time color",
        category = "General",
        subcategory = "Colors"
    )
    var timeColor = OneColor(85, 255, 255)      // aqua
    @Color(
        name = "Money color",
        category = "General",
        subcategory = "Colors"
    )
    var coinColor = OneColor(255, 170, 0)       // gold
    @Color(
        name = "Pause indicator color",
        category = "General",
        subcategory = "Colors"
    )
    var pauseColor = OneColor(85, 85, 85)       // dark gray
    //</editor-fold>

    //<editor-fold desc="Stat tracker settings">
    @Button(
        name = "Reset stats",
        text = "Reset",
        category = "Stat Tracker",
        subcategory = "Control Panel"
    )
    var tracker_resetButton = Runnable { GhostTracker.resetStats() }
    @KeyBind(
        name = "Stat reset keybind",
        category = "Stat Tracker",
        subcategory = "Control Panel"
    )
    var tracker_resetKb = OneKeyBind()

    @Switch(
        name = "Show kill count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_kills = true
    @Switch(
        name = "Show sorrow count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_sorrow = true
    @Switch(
        name = "Show volta count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_volta = true
    @Switch(
        name = "Show plasma count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_plasma = true
    @Switch(
        name = "Show ghostly boots count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_boots = true
    @Switch(
        name = "Show 1m coin drop count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_coins = true
    @Switch(
        name = "Show average magic find",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_mf = true
    @Switch(
        name = "Show average combat XP",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_averageXp = true
    @Switch(
        name = "Show total combat XP",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_totalXp = true
    @Switch(
        name = "Show total money",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var tracker_totalMoney = true

    @HUD(
        name = "Stats HUD",
        category = "Stat Tracker",
        subcategory = "HUD Settings"
    )
    var tracker_hud = GhostHud()
    //</editor-fold>

    //<editor-fold desc="Session timer settings">
    @Button(
        name = "Reset timer",
        text = "Reset",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var timer_resetButton = Runnable { GhostTimer.reset() }
    @KeyBind(
        name = "Reset timer keybind",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var timer_resetKb = OneKeyBind()
    @Button(
        name = "Start/resume timer",
        text = "Start",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var startButton = Runnable { GhostTimer.start() }
    @KeyBind(
        name = "Start/pause timer keybind",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var pauseKb = OneKeyBind()
    @Button(
        name = "Pause timer",
        text = "Pause",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var pauseButton = Runnable { GhostTimer.pause() }

    @Switch(
        name = "Show kills per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_kills = true
    @Switch(
        name = "Show sorrows per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_sorrow = true
    @Switch(
        name = "Show voltas per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_volta = true
    @Switch(
        name = "Show plasmas per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_plasma = true
    @Switch(
        name = "Show ghostly boots per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_boots = true
    @Switch(
        name = "Show 1m coins per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_coins = true
    @Switch(
        name = "Show average magic find",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_mf = true
    @Switch(
        name = "Show average combat XP",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_averageXp = true
    @Switch(
        name = "Show combat XP per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_xpRate = true
    @Switch(
        name = "Show money per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_moneyRate = true
    @Switch(
        name = "Show session time",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var timer_time = true

    @HUD(
        name = "Session timer HUD",
        category = "Session Timer",
        subcategory = "HUD Settings"
    )
    var timer_hud = TimerHud()
    //</editor-fold>

    init {
        initialize()
        registerKeyBind(tracker_resetKb) { GhostTracker.resetStats() }
        registerKeyBind(timer_resetKb) { GhostTimer.reset() }
        registerKeyBind(pauseKb) {
            if (GhostTimer.isTracking) GhostTimer.pause()
            else GhostTimer.start()
        }
    }

    @Suppress("DEPRECATION")
    fun shadow() = when (shadow) {
        1 -> TextType.SHADOW
        2 -> TextType.FULL
        else -> TextType.NONE
    }

    fun priceTimespanString(): String = when (priceTimespan) {
        0 -> "hour"
        1 -> "day"
        else -> "week"
    }
}