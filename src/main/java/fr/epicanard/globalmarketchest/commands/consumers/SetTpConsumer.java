package fr.epicanard.globalmarketchest.commands.consumers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static fr.epicanard.globalmarketchest.utils.Option.exists;

/**
 * Set the teleport position of a specific physical shop with current player position
 *
 * Command : /globalmarketchest settp <groupName> <coord>
 * Permission: globalmarketchest.commands.list.detail.settp
 */
public class SetTpConsumer implements CommandConsumer {

  /**
   * Method called when consumer is executed
   *
   * @param node Command node
   * @param command Command executed
   * @param sender Command's executor (player or console)
   * @param args Arguments of command
   */
  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    if (!(sender instanceof Player) || args.length < 2) {
      return node.invalidCommand(sender, command);
    }

    try {
      Location argLoc = WorldUtils.getLocationFromString(args[1], null);
      List<ShopInfo> shops = GlobalMarketChest.plugin.shopManager.getShops().stream()
        .filter(shop -> shop.getGroup().equals(args[0]) && shop.getExists() && exists(shop.getTpLocation(), loc -> WorldUtils.compareLocations(loc, argLoc)))
        .collect(Collectors.toList());
      if (shops.size() == 0) {
        PlayerUtils.sendMessage(sender, String.format("%s%s %s", LangUtils.get("ErrorMessages.UnknownShop"), args[0], args[1]));
        return false;
      }
      GlobalMarketChest.plugin.shopManager.setTpLocation(shops.get(0), ((Player)sender).getLocation());
      PlayerUtils.sendMessageConfig(sender, "InfoMessages.TeleportLocationUpdated");
    } catch (NumberFormatException e) {
      return node.invalidCommand(sender, command);
    }
    return true;
  }
}
