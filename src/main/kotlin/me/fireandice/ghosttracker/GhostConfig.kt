package me.fireandice.ghosttracker

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import me.fireandice.ghosttracker.hud.GhostHud
import me.fireandice.ghosttracker.hud.TimerHud
import me.fireandice.ghosttracker.tracker.GhostStats
import me.fireandice.ghosttracker.tracker.GhostTimer

object GhostConfig : Config(Mod(GhostTracker.NAME, ModType.SKYBLOCK), "GhostConfig.json") {

    // SESSION TIMER (this one is listed first because the controls need to be more easily accessible
    @Button(
        name = "Start/resume timer",
        text = "Start",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var startButton = Runnable { GhostTimer.start() }
    @Button(
        name = "Reset timer",
        text = "Reset",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var resetButton = Runnable { GhostTimer.clear() }
    @Button(
        name = "Pause session",
        text = "Pause",
        category = "Session Timer",
        subcategory = "Control Panel"
    )
    var pauseButton = Runnable { GhostTimer.pause() }

    @HUD(
        name = "Session timer HUD",
        category = "Session Timer",
        subcategory = "HUD Settings"
    )
    var timerHud = TimerHud()

    // STAT TRACKER
    @Button(
        name = "Reset stats",
        text = "Reset",
        category = "Stat Tracker",
        subcategory = "Control Panel"
    )
    var genResetButton = Runnable { GhostStats.reset() }

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
    var showAverageXp = true
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

    init {
        initialize()
    }
}
