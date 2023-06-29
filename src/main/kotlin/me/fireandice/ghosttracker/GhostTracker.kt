package me.fireandice.ghosttracker

import cc.polyfrost.oneconfig.libs.universal.ChatColor
import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.utils.dsl.mc
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.fireandice.ghosttracker.command.MainCommand
import me.fireandice.ghosttracker.event.GhostListener
import me.fireandice.ghosttracker.tracker.GhostStats
import me.fireandice.ghosttracker.tracker.GhostTimer
import me.fireandice.ghosttracker.utils.PREFIX
import me.fireandice.ghosttracker.utils.ScoreboardUtils
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

    val modDir = File(File(UMinecraft.getMinecraft().mcDataDir, "config"), "GhostTracker")
    private var statsFile = File(modDir, "GhostStats.json")
    private var lastSave: Long = -1L
    val ghostStats = GhostStats()

    @EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
        modDir.mkdirs()

        if (statsFile.createNewFile()) this.save()
        else this.load()

        if (GhostTimer.file.createNewFile()) GhostTimer.save()
        else GhostTimer.load()
    }

    @EventHandler
    fun onInit(event: FMLInitializationEvent) {
        GhostConfig

        arrayOf(
            this,
            GhostListener
        ).forEach { MinecraftForge.EVENT_BUS.register(it) }

        ClientCommandHandler.instance.registerCommand(MainCommand)

        Runtime.getRuntime().addShutdownHook(Thread {
            this.save()
            GhostTimer.save()
        })
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return

        // auto save every 5 minutes
        if (lastSave == -1L || System.currentTimeMillis() - lastSave > 300_000) {
            this.save()
            GhostTimer.save()
            lastSave = System.currentTimeMillis()
        }

        if (mc.theWorld == null || mc.theWorld.scoreboard == null) return
        ScoreboardUtils.checkLocations()
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) = GhostTimer.pause(false)

    fun resetStats(message: Boolean = true) {
        ghostStats.reset()
        if (message) UChat.chat("$PREFIX ${ChatColor.RED}Main tracker reset")
    }

    private fun save() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(ghostStats.toJson())
        statsFile.bufferedWriter().use { it.write(jsonString) }
    }

    private fun load() {
        try {
            val jsonString = statsFile.bufferedReader().use { it.readText() }
            Gson().fromJson(jsonString, JsonObject::class.java).also { ghostStats.fromJson(it) }
        } catch (_: Exception) {
        }
    }
}