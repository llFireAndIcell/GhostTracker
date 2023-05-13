package me.fireandice.ghosttracker

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import me.fireandice.ghosttracker.command.MainCommand
import me.fireandice.ghosttracker.tracker.GhostListener
import me.fireandice.ghosttracker.tracker.GhostStats
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import me.fireandice.ghosttracker.utils.mc
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.io.File

@Mod(
    modid = GhostTracker.MODID,
    name = GhostTracker.NAME,
    version = GhostTracker.VERSION,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
object GhostTracker {

    var config: GhostConfig = GhostConfig
    private val modDir = File(File(UMinecraft.getMinecraft().mcDataDir, "config"), "GhostTracker")
    lateinit var statsFile: File

    @EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
        modDir.mkdirs()
        statsFile = File(modDir, "GhostStats.json")
        if (statsFile.createNewFile()) GhostStats.save()
        else GhostStats.load()

        ClientCommandHandler.instance.registerCommand(MainCommand)
    }

    @EventHandler
    fun onInit(event: FMLInitializationEvent) {
        config.init()
        arrayOf(
            this,
            GhostListener
        ).forEach { MinecraftForge.EVENT_BUS.register(it) }
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (mc.theWorld == null || mc.theWorld.scoreboard == null) return

        ScoreboardUtils.checkLocations()
    }

    private var ticks = 0
    @SubscribeEvent
    fun saveEveryMinute(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        ticks++
        if (ticks % 1200 != 0) return

        GhostStats.save()
        ticks = 0
    }

    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"
    val PREFIX = "${ChatColor.AQUA}${ChatColor.BOLD}GhostTracker ${ChatColor.DARK_GRAY}»${ChatColor.RESET}"
}