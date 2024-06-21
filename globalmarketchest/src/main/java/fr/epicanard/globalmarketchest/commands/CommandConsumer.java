package fr.epicanard.globalmarketchest.commands;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandConsumer {

  /**
   * Method called when consumer is executed
   * 
   * @param node Command node
   * @param command Command executed
   * @param sender Command's executor (player or console)
   * @param args Arguments of command
   */
  Boolean accept(CommandNode node, String command, CommandSender sender, String[] args);
}
