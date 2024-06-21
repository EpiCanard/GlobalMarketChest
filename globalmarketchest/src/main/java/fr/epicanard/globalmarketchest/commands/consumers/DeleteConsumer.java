package fr.epicanard.globalmarketchest.commands.consumers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

import static fr.epicanard.globalmarketchest.utils.Option.forAll;

/**
 * Delete a shop from command line
 *
 * Command : /globalmarketchest delete <group> [coord]
 * Permission : globalmarketchest.commands.delete
 * Arguments :
 *  - group : Name of shop
 *  - coord : Coordonate of shops
 */
public class DeleteConsumer implements CommandConsumer {

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
    try {
      Boolean isConsole = !(sender instanceof Player);
      Boolean isAdmin = !isConsole && Permissions.CMD_ADMIN_DELETE.isSetOn(sender);
      Location argLoc = (args.length > 1) ? WorldUtils.getLocationFromString(args[1], null) : null;
      List<ShopInfo> shops = GlobalMarketChest.plugin.shopManager.getShops().stream()
        .filter(shop -> shop.getGroup().equals(args[0])
           && (isConsole || isAdmin || shop.getOwner().equals(((Player)sender).getUniqueId().toString()))
           && forAll(shop.getTpLocation(), loc -> args.length < 2 ||  WorldUtils.compareLocations(loc, argLoc)))
        .collect(Collectors.toList());
      if (shops.size() == 0) {
        PlayerUtils.sendMessage(sender, String.format("%s%s %s", LangUtils.get("ErrorMessages.UnknownShop"), args[0], (args.length > 1) ? args[1] : ""));
        return false;
      }

      shops.stream().forEach(GlobalMarketChest.plugin.shopManager::deleteShop);
      PlayerUtils.sendMessageConfig(sender, "InfoMessages.ShopDeleted");
    } catch (NumberFormatException e) {
      return node.invalidCommand(sender, command);
    }
    return true;
  }
}
