package me.fireandice.ghosttracker

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.elements.BasicOption
import me.fireandice.ghosttracker.hud.GhostHud
import me.fireandice.ghosttracker.hud.TimerHud
import me.fireandice.ghosttracker.tracker.GhostTimer

object GhostConfig : Config(Mod(GhostTracker.NAME, ModType.SKYBLOCK), "GhostConfig.json") {

    // GENERAL SETTINGS
    @Switch(
        name = "Show everywhere",
        description = "Show everywhere, instead of only in dwarven mines",
        category = "General"
    )
    var showEverywhere = false

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
        name = "Pause indicator color",
        category = "General",
        subcategory = "Colors"
    )
    var pauseColor = OneColor(85, 85, 85)       // dark gray

    // STAT TRACKER
    @Button(
        name = "Reset stats",
        text = "Reset",
        category = "Stat Tracker",
        subcategory = "Control Panel"
    )
    var genResetButton = Runnable { GhostTracker.resetStats() }
    @KeyBind(
        name = "Stat reset keybind",
        category = "Stat Tracker",
        subcategory = "Control Panel"
    )
    var resetStatsKb = OneKeyBind()

    @Switch(
        name = "Show kill count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var showKills = true
    @Switch(
        name = "Show sorrow count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var showSorrow = true
    @Switch(
        name = "Show volta count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var showVolta = true
    @Switch(
        name = "Show plasma count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var showPlasma = true
    @Switch(
        name = "Show ghostly boots count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var showBoots = true
    @Switch(
        name = "Show 1m coin drop count",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var showCoins = true
    @Switch(
        name = "Show percent difference",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var showMargins = true
    @Switch(
        name = "Show average magic find",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var showMf = true
    @Switch(
        name = "Show average combat XP",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var showXp = true
    @Switch(
        name = "Show total combat XP",
        category = "Stat Tracker",
        subcategory = "Display Information"
    )
    var showTotalXp = true

    @HUD(
        name = "Stats HUD",
        category = "Stat Tracker",
        subcategory = "HUD Settings"
    )
    var ghostHud = GhostHud()

    // SESSION TIMER
    @Button(
        name = "Reset timer",
        text = "Reset",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var resetButton = Runnable { GhostTimer.reset() }
    @KeyBind(
        name = "Reset timer keybind",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var resetTimerKb = OneKeyBind()
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
    var showKillsPerHour = true
    @Switch(
        name = "Show sorrows per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var showSorrowsPerHour = true
    @Switch(
        name = "Show voltas per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var showVoltasPerHour = true
    @Switch(
        name = "Show plasmas per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var showPlasmasPerHour = true
    @Switch(
        name = "Show ghostly boots per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var showBootsPerHour = true
    @Switch(
        name = "Show 1m coins per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var showCoinDropsPerHour = true
    @Switch(
        name = "Show percent difference",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var showTimerMargins = true
    @Switch(
        name = "Show average magic find",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var showTimerMf = true
    @Switch(
        name = "Show average combat XP",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var showTimerXp = true
    @Switch(
        name = "Show combat XP per hour",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var showXpPerHour = true
    @Switch(
        name = "Show total combat XP",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var showTotalTimerXp = true
    @Switch(
        name = "Show session time",
        category = "Session Timer",
        subcategory = "Display Information"
    )
    var showTime = true

    @HUD(
        name = "Session timer HUD",
        category = "Session Timer",
        subcategory = "HUD Settings"
    )
    var timerHud = TimerHud()

    init {
        initialize()
        registerKeyBind(resetStatsKb) { GhostTracker.resetStats() }
        registerKeyBind(resetTimerKb) { GhostTimer.reset() }
        registerKeyBind(pauseKb) {
            if (GhostTimer.isTracking) GhostTimer.pause()
            else GhostTimer.start()
        }

        arrayOf(
            "showKills",
            "showSorrow",
            "showVolta",
            "showPlasma",
            "showBoots",
            "showCoins",
            "showMf",
            "showXp",
            "showTotalXp"
        ).forEach { addListener(it) { ghostHud.onConfigUpdate(it, optionNames[it]?.get() as Boolean) } }

        arrayOf(
            "showKillsPerHour",
            "showSorrowsPerHour",
            "showVoltasPerHour",
            "showPlasmasPerHour",
            "showBootsPerHour",
            "showCoinDropsPerHour",
            "showTimerMf",
            "showTimerXp",
            "showXpPerHour",
            "showTotalTimerXp",
            "showTime"
        ).forEach { addListener(it) { timerHud.onConfigUpdate(it, optionNames[it]?.get() as Boolean) } }
    }
}
