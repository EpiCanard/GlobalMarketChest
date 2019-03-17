package fr.epicanard.globalmarketchest.commands.consumers;

import org.bukkit.command.CommandSender;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

/**
 * Command that show the version of the plugin.
 *
 * Command : /globalmarketchest version
 * Permission: globalmarketchest.commands
 */
public class VersionConsumer implements CommandConsumer {

  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    PlayerUtils.sendMessage(sender, GlobalMarketChest.plugin.getDescription().getFullName());
    return true;
  }
}