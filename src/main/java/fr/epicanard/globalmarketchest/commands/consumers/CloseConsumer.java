package fr.epicanard.globalmarketchest.commands.consumers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Close the shop of a player from command line
 * 
 * Command : /globalmarketchest close <player>
 * Permission: globalmarketchest.admin.commands.close
 */
public class CloseConsumer implements CommandConsumer {

  /**
   * Method called when consumer is executed
   * 
   * @param node Command node
   * @param command Command executed
   * @param sender Command's executor (player or console)
   * @param args Arguments of command
   */
  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    if (args.length < 1) {
      return node.invalidCommand(sender, command);
    }

    final Player player = GlobalMarketChest.plugin.getServer().getPlayer(args[0]);

    if (player == null) {
      PlayerUtils.sendMessageConfig(sender, "ErrorMessages.PlayerDoesntExist");
      return false;
    }

    GlobalMarketChest.plugin.inventories.removeInventory(player.getUniqueId());
    return true;
  }
}
