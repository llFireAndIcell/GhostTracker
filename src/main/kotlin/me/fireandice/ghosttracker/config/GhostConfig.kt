package me.fireandice.ghosttracker.config

import me.fireandice.ghosttracker.GhostTracker
import me.fireandice.ghosttracker.tracker.GhostTimer
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.input.KeybindHelper
import org.polyfrost.universal.UKeyboard

@Suppress("unused", "MemberVisibilityCanBePrivate")
object GhostConfig : KtConfigNotNull("GhostConfig.json", GhostTracker.NAME, Category.HYPIXEL) {

    //<editor-fold desc="General settings">

    //<editor-fold desc="General">
    var showEverywhere by switch(
        false,
        "Show everywhere",
        "Show everywhere, instead of only in dwarven mines"
    )

    var showIcons by switch(true, "Show icons", "Show a small icon to the left of every hud line")
    var abbreviate by switch(false, "Abbreviate text", "Reduce text on each hud line")

    /**
     * Originally `showPrefixes` was tied to the config option, but I updated the description of the config option and
     * inverted the value to logically match the new description. Instead of changing it everywhere in the code, I just
     * tied `abbreviate` to the config option and invert it
     */
    val showPrefixes get() = !abbreviate

    var showMargins by switch(
        true,
        "Show margins",
        "Show the percent difference between drops you've received and the mathematical average"
    )
    //</editor-fold>

    //<editor-fold desc="Enchants">
    var lootingLevel by slider(0f, 10f, 5f, "Looting level")
    var luckLevel by slider(0f, 10f, 7f, "Luck level")
    //</editor-fold>

    //<editor-fold desc="Price fetching">
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

    var priceFrequency by slider(5f, 120f, 20f, "Fetch frequency (minutes)", "The frequency that api data is refreshed")
    //</editor-fold>

    //<editor-fold desc="Colors">
    var killColor by color(rgba(85, 255, 255), "Kill color") // aqua
    var dropColor by color(rgba(85, 85, 255), "Drop color") // blue
    var marginColor by color(rgba(85, 85, 85), "Percent Difference Color") // dark gray
    var mfColor by color(rgba(255, 170, 0), "Magic find color") // gold
    var xpColor by color(rgba(255, 85, 85), "Combat XP color") // red
    var timeColor by color(rgba(85, 255, 255), "Time color") // aqua
    var coinColor by color(rgba(255, 170, 0), "Money color") // gold
    var pauseColor by color(rgba(85, 85, 85), "Pause indicator color") // dark gray
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="Stat tracker settings">

    //<editor-fold desc="Control panel">

    // TODO: create an entirely separate gui for controls; it doesn't make sense in the config tbh. Have a keybind and
    //  command to open that gui
//    @Button(
//        title = "Reset stats",
//        text = "Reset",
//        category = "Stat Tracker",
//        subcategory = "Control Panel"
//    )
//    var tracker_resetButton = Runnable { GhostTracker.resetStats() }

    var tracker_resetKb by keybind(
        KeybindHelper.builder()
            .keys(UKeyboard.KEY_NONE)
            .does(GhostTracker::resetStats)
            .build(),
        "Reset stats"
    )
    //</editor-fold>

    //<editor-fold desc="Display info">
    var tracker_kills by switch(true, "Show kill count")
    var tracker_sorrow by switch(true, "Show sorrow count")
    var tracker_volta by switch(true, "Show volta count")
    var tracker_plasma by switch(true, "Show plasma count")
    var tracker_boots by switch(true, "Show ghostly boots count")
    var tracker_coins by switch(true, "Show 1m coin drop count")
    var tracker_mf by switch(true, "Show average magic find")
    var tracker_averageXp by switch(true, "Show average combat XP")
    var tracker_totalXp by switch(true, "Show total combat XP")
    var tracker_scavenger by switch(true, "Show scavenger coins")
    var tracker_totalMoney by switch(true, "Show total money")
    //</editor-fold>

    // TODO: huds work completely different. They aren't part of the config anymore
//    @HUD(
//        name = "Stats HUD",
//        category = "Stat Tracker",
//        subcategory = "HUD Settings"
//    )
//    var tracker_hud = GhostHud()
    //</editor-fold>

    //<editor-fold desc="Session timer settings">

    //<editor-fold desc="Control panel">
    var timer_resetKb by keybind(
        KeybindHelper.builder()
            .keys(UKeyboard.KEY_NONE)
            .does(GhostTimer::reset)
            .build(),
        "Reset timer keybind"
    )

    var pauseKb by keybind(
        KeybindHelper.builder()
            .keys(UKeyboard.KEY_NONE)
            .does {
                if (GhostTimer.isTracking) GhostTimer.pause()
                else GhostTimer.start()
            }
            .build(),
        "Start/pause timer keybind"
    )
    //</editor-fold>

    //<editor-fold desc="Display info">
    // TODO: migrate the rest of these
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

    //</editor-fold>
}
