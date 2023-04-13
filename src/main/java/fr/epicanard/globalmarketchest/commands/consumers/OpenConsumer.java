package fr.epicanard.globalmarketchest.commands.consumers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.gui.actions.InterfaceType;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Open a shop from command line
 *
 * Command : /globalmarketchest open <shop>
 * Permission: globalmarketchest.commands.open
 *
 * Command : /globalmarketchest open <shop> [player]
 * Permission: globalmarketchest.admin.commands.open
 */
public class OpenConsumer implements CommandConsumer {

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

    if (!(sender instanceof Player) && args.length <= 1) {
      PlayerUtils.sendMessage(sender, "&c" + LangUtils.get("ErrorMessages.PlayerOnly"));
      return false;
    }

    Player player;

    if (args.length >= 2 && Permissions.CMD_ADMIN_OPEN.isSetOn(sender)) {
      player = GlobalMarketChest.plugin.getServer().getPlayer(args[1]);
      if (player == null) {
        PlayerUtils.sendMessageConfig(sender, "ErrorMessages.PlayerDoesntExist");
        return false;
      }
    } else {
      player = (Player) sender;
    }

    final List<ShopInfo> shops = GlobalMarketChest.plugin.shopManager.getShops();
    final List<ShopInfo> match = shops.stream().filter(e -> e.getGroup().equals(args[0])).collect(Collectors.toList());

    if (match.size() == 0) {
      PlayerUtils.sendMessage(sender, LangUtils.get("ErrorMessages.UnknownShop") + args[0]);
      return false;
    }

    ShopUtils.openShop(player, match.get(0), inv -> inv.loadInterface(InterfaceType.CATEGORY_VIEW));
    return true;
  }
}
