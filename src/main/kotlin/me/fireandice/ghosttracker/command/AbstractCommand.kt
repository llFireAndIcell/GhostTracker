package me.fireandice.ghosttracker.command

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

/**
 * A class to simplify the process of creating new commands. Commands should implement this class. The functionality of
 * the command is in [processCommand]
 */
abstract class AbstractCommand(
    private val name: String,
    private val aliases: List<String> = emptyList()
) : CommandBase() {

    final override fun getCommandName(): String = name
    final override fun getCommandAliases(): List<String> = aliases
    final override fun getRequiredPermissionLevel() = 0

    override fun getCommandUsage(sender: ICommandSender?): String = "/$commandName"

    abstract override fun processCommand(sender: ICommandSender?, args: Array<String>?)
}