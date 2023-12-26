package fr.epicanard.globalmarketchest.commands.consumers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.exceptions.WorldDoesntExist;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.InterfaceType;
import fr.epicanard.globalmarketchest.ranks.RankProperties;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static fr.epicanard.globalmarketchest.utils.LangUtils.format;

/**
 * Open a shop from command line
 *
 * Command : /globalmarketchest create [shop]
 * Permission : globalmarketchest.commands.open
 * Arguments :
 *  - shop : Name of shop. If not filled in an interface will pop up
 */
public class CreateConsumer implements CommandConsumer {

  /**
   * Method called when consumer is executed
   *
   * @param node Command node
   * @param command Command executed
   * @param sender Command's executor (player or console)
   * @param args Arguments of command
   */
  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    final ShopType shopType = ShopType.GLOBALSHOP;
    final String shopName = (args.length >= 1) ? args[0] : null;

    final Player player = (Player) sender;

    try {
      if (!WorldUtils.isAllowedWorld(player.getLocation().getWorld().getName())) {
        PlayerUtils.sendMessageConfig(player, "ErrorMessages.WorldNotAllowed");
        return false;
      }
    } catch (WorldDoesntExist e) {
      LoggerUtils.warn(e.getMessage());
      return false;
    }

    final RankProperties playerRankProperties = GlobalMarketChest.plugin.getRanksLoader().getPlayerProperties(player);
    if (playerRankProperties.canCreateShop(player)) {
      this.openCreationShopInterface(player, shopType, shopName);
    } else {
      PlayerUtils.sendMessage(player, format("ErrorMessages.MaxGlobalShop", "maxGlobalShop", playerRankProperties.getMaxGlobalShopByPlayer()));
      return false;
    }

    return true;
  }

  /**
   * Open shop interface to create it
   *
   * @param player Player that create the shop
   * @param event  Sign event
   */
  private void openCreationShopInterface(Player player, ShopType shopType, String shopName) {
    final ShopInfo shop = new ShopInfo(
        -1,
        player.getUniqueId().toString(),
        shopType,
        null,
        null,
        player.getLocation(),
        (shopName != null) ? shopName : ShopUtils.generateName()
    );
    final InventoryGUI inv = new InventoryGUI(player);

    inv.getTransaction().put(TransactionKey.SHOP_INFO, shop);
    inv.getTransaction().put(TransactionKey.SIGN_LOCATION, player.getLocation());
    GlobalMarketChest.plugin.inventories.addInventory(player.getUniqueId(), inv);
    inv.open();
    inv.loadInterface(InterfaceType.SHOP_CREATION_TYPE);
  }
}
