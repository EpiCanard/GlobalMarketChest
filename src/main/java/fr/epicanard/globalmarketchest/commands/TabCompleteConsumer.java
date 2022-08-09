package fr.epicanard.globalmarketchest.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

@FunctionalInterface
public interface TabCompleteConsumer {
  /**
   * Method called for tab completion
   * 
   * @param sender Command's executor (player or console)
   * @param args Arguments of command
   */
  List<String> apply(CommandSender player, String[] args);
}
