package fr.epicanard.globalmarketchest.commands.consumers;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;

/**
 * Teleport a player to a physical shop
 *
 * Command : /globalmarketchest tp <groupName> <coord>
 * Permission: globalmarketchest.commands.list.detail.tp
 */
public class TPConsumer implements CommandConsumer {

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
      List<ShopInfo> shops = GlobalMarketChest.plugin.shopManager.getShops().stream()
        .filter(shop -> shop.getGroup().equals(args[0]) && WorldUtils.compareLocations(shop.getSignLocation(), WorldUtils.getLocationFromString(args[1], null))).collect(Collectors.toList());
      if (shops.size() == 0) {
        PlayerUtils.sendMessage(sender, String.format("%s%s %s", LangUtils.get("ErrorMessages.UnknownShop"), args[0], args[1]));
        return false;
      }
      Location loc = shops.get(0).getSignLocation().clone().add(0.5, 0, 0.5);
      ((Player)sender).teleport(loc);
    } catch(NumberFormatException e) {
      return node.invalidCommand(sender, command);
    }
    return true;
  }
}