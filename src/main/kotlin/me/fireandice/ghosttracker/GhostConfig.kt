package me.fireandice.ghosttracker

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import me.fireandice.ghosttracker.hud.GhostHud
import me.fireandice.ghosttracker.hud.SessionHud
import me.fireandice.ghosttracker.tracker.GhostStats
import me.fireandice.ghosttracker.tracker.SessionTracker

object GhostConfig : Config(Mod(GhostTracker.NAME, ModType.SKYBLOCK), "GhostConfig.json", true) {

    // SESSION TRACKER (this one is listed first because the controls need to be more easily accessible
    @Button(
        name = "Start/resume session",
        text = "Start",
        category = "Session Tracker",
        subcategory = "Control Panel"
    )
    var startButton = Runnable { SessionTracker.start() }
    @Button(
        name = "Reset session",
        text = "Reset",
        category = "Session Tracker",
        subcategory = "Control Panel"
    )
    var resetButton = Runnable { SessionTracker.clear() }
    @Button(
        name = "Stop/pause session",
        text = "Pause",
        category = "Session Tracker",
        subcategory = "Control Panel"
    )
    var pauseButton = Runnable { SessionTracker.pause() }

    @HUD(
        name = "Session tracker HUD",
        category = "Session Tracker",
        subcategory = "HUD Settings"
    )
    var sessionHud = SessionHud()

    // GENERAL TRACKER
    @Button(
        name = "Reset stats",
        text = "Reset",
        category = "General Tracker",
        subcategory = "Control Panel"
    )
    var genResetButton = Runnable { GhostStats.reset() }

    @Switch(
        name = "Show kill count",
        category = "General Tracker",
        subcategory = "Display Settings"
    )
    var showKills = true
    @Switch(
        name = "Show sorrow count",
        category = "General Tracker",
        subcategory = "Display Settings"
    )
    var showSorrow = true
    @Switch(
        name = "Show volta count",
        category = "General Tracker",
        subcategory = "Display Settings"
    )
    var showVolta = true
    @Switch(
        name = "Show plasma count",
        category = "General Tracker",
        subcategory = "Display Settings"
    )
    var showPlasma = true
    @Switch(
        name = "Show ghostly boots count",
        category = "General Tracker",
        subcategory = "Display Settings"
    )
    var showBoots = true
    @Switch(
        name = "Show 1m coin drop count",
        category = "General Tracker",
        subcategory = "Display Settings"
    )
    var showCoins = true
    @Switch(
        name = "Show average magic find",
        category = "General Tracker",
        subcategory = "Display Settings"
    )
    var showMf = true
    @Switch(
        name = "Show average combat XP",
        category = "General Tracker",
        subcategory = "Display Settings"
    )
    var showAverageXp = true
    @Switch(
        name = "Show total combat XP",
        category = "General Tracker",
        subcategory = "Display Settings"
    )
    var showTotalXp = true

    @HUD(
        name = "Stats HUD",
        category = "General Tracker",
        subcategory = "HUD Settings"
    )
    var ghostHud = GhostHud()

    fun init() {
        initialize()
    }
}
