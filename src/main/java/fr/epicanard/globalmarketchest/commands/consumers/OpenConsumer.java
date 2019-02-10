package fr.epicanard.globalmarketchest.commands.consumers;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

/**
 * Open a shop from command line
 */
public class OpenConsumer implements CommandConsumer {
  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }
    Player player = (Player) sender;

    if (args.length < 1) {
      return node.invalidCommand(sender, command);
    }
    List<ShopInfo> shops = GlobalMarketChest.plugin.shopManager.getShops();
    List<ShopInfo> match = shops.stream().filter(e -> e.getGroup().equals(args[0])).collect(Collectors.toList());

    if (match.size() == 0) {
      PlayerUtils.sendMessage(player, LangUtils.get("ErrorMessages.UnknownShop") + args[0]);
      return false;
    }

    InventoryGUI inv = new InventoryGUI(player);
    GlobalMarketChest.plugin.inventories.addInventory(player.getUniqueId(), inv);
    inv.getTransaction().put(TransactionKey.SHOPINFO, match.get(0));
    inv.open();
    inv.loadInterface("CategoryView");
    return true;
  }
}