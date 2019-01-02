package fr.epicanard.globalmarketchest.commands;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandConsumer {
  Boolean accept(CommandNode node, String command, CommandSender sender, String[] args);
}