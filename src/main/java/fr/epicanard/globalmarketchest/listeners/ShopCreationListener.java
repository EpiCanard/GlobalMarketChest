package fr.epicanard.globalmarketchest.listeners;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.WorldDoesntExist;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.InterfaceType;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.ranks.RankProperties;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import static fr.epicanard.globalmarketchest.utils.LangUtils.format;

/**
 * Listener for creation process
 */
public class ShopCreationListener implements Listener {

  @EventHandler
  public void onChangeSign(SignChangeEvent event) {
    final Player player = event.getPlayer();

    try {
      if (!Permissions.GS_CREATESHOP.isSetOn(player) || !WorldUtils.isAllowedWorld(event.getBlock().getWorld().getName()))
        return;
    } catch (WorldDoesntExist e) {
      LoggerUtils.warn(e.getMessage());
      return;
    }

    if (event.getLine(0).equals(ShopType.GLOBALSHOP.getFirstLineToCreate())) {
      event.setLine(0, ShopType.GLOBALSHOP.getErrorDisplayName());
      final RankProperties playerRankProperties = GlobalMarketChest.plugin.getRanksLoader().getPlayerProperties(player);
      if (playerRankProperties.canCreateShop(player)) {
        this.openCreationShopInterface(player, event);
      } else {
        PlayerUtils.sendMessage(player, format("ErrorMessages.MaxGlobalShop", "maxGlobalShop", playerRankProperties.getMaxGlobalShopByPlayer()));
      }
    }
  }

  /**
   * Open shop interface to create it
   *
   * @param player Player that create the shop
   * @param event  Sign event
   */
  private void openCreationShopInterface(Player player, SignChangeEvent event) {
    final ShopInfo shop = new ShopInfo(
        -1,
        player.getUniqueId().toString(),
        ShopType.GLOBALSHOP,
        event.getBlock().getLocation(),
        null,
        event.getBlock().getLocation().add(0.5, 0, 0.5),
        ShopUtils.generateName()
    );
    final InventoryGUI inv = new InventoryGUI(player);

    inv.getTransaction().put(TransactionKey.SHOP_INFO, shop);
    inv.getTransaction().put(TransactionKey.SIGN_LOCATION, event.getBlock().getLocation());
    GlobalMarketChest.plugin.inventories.addInventory(player.getUniqueId(), inv);
    inv.open();
    inv.loadInterface(InterfaceType.SHOP_CREATION_SELECT_TYPE);
  }
}
