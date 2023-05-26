package me.fireandice.ghosttracker

import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.PreShutdownEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import me.fireandice.ghosttracker.command.MainCommand
import me.fireandice.ghosttracker.tracker.GhostListener
import me.fireandice.ghosttracker.tracker.GhostStats
import me.fireandice.ghosttracker.tracker.GhostTimer
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import me.fireandice.ghosttracker.utils.mc
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
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

    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"
    val PREFIX = "${ChatColor.AQUA}${ChatColor.BOLD}GhostTracker ${ChatColor.DARK_GRAY}»${ChatColor.RESET}"

    private val modDir = File(File(UMinecraft.getMinecraft().mcDataDir, "config"), "GhostTracker")
    var statsFile: File = File(modDir, "GhostStats.json")

    val ghostStats = GhostStats()

    @EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
        modDir.mkdirs()
        if (statsFile.createNewFile()) ghostStats.save()
        else ghostStats.load()
    }

    @EventHandler
    fun onInit(event: FMLInitializationEvent) {
        GhostConfig

        arrayOf(
            this,
            GhostListener
        ).forEach { MinecraftForge.EVENT_BUS.register(it) }

        // registering to the OneConfig event handler to use the PreShutDownEvent
        EventManager.INSTANCE.register(this)

        ClientCommandHandler.instance.registerCommand(MainCommand)
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (mc.theWorld == null || mc.theWorld.scoreboard == null) return

        ScoreboardUtils.checkLocations()
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) = GhostTimer.pause(false)

    @Subscribe
    fun onClose(event: PreShutdownEvent) = ghostStats.save()
}