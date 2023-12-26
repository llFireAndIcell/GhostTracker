package me.fireandice.ghosttracker

import me.fireandice.ghosttracker.tracker.GhostListener
import me.fireandice.ghosttracker.tracker.GhostTimer
import me.fireandice.ghosttracker.tracker.PurseListener
import me.fireandice.ghosttracker.utils.ScoreboardUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

object EventListener {

    @SubscribeEvent
    fun onTickStart(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return

        GhostTracker.autoSave()
        ScoreboardUtils.checkLocations()
        PurseListener.onTick()
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) = GhostTimer.pause(false)

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        GhostListener.onChat(event)
        GhostListener.onActionBar(event)
    }
}