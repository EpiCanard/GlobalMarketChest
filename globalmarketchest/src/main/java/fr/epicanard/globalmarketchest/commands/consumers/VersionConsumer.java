package fr.epicanard.globalmarketchest.commands.consumers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import org.bukkit.command.CommandSender;

/**
 * Command that show the version of the plugin.
 *
 * Command : /globalmarketchest version
 * Permission: globalmarketchest.commands
 */
public class VersionConsumer implements CommandConsumer {

  /**
   * Method called when consumer is executed
   * 
   * @param node Command node
   * @param command Command executed
   * @param sender Command's executor (player or console)
   * @param args Arguments of command
   */
  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    PlayerUtils.sendMessage(sender, GlobalMarketChest.plugin.getDescription().getFullName());
    return true;
  }
}
