package fr.epicanard.globalmarketchest.commands.consumers;

import org.bukkit.command.CommandSender;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;

/**
 * Command that reload the plugin.
 *
 * Command : /globalmarketchest reload
 * Permission: globalmarketchest.commands.reload
 */
public class ReloadConsumer implements CommandConsumer {

  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    GlobalMarketChest.plugin.reload(sender);
    return true;
  }
}