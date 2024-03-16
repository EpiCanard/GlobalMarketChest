package fr.epicanard.globalmarketchest.listeners;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
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
    final ShopType type = ShopType.fromLineToCreate(event.getLine(0));

    if (type == null)
      return;

    final Player player = event.getPlayer();

    try {
      if (!WorldUtils.isAllowedWorld(event.getBlock().getWorld().getName()))
        return;
    } catch (WorldDoesntExist e) {
      LoggerUtils.warn(e.getMessage());
      return;
    }

    event.setLine(0, type.getErrorDisplayName());

    if (canCreateShop(type, player))
      this.openCreationShopInterface(type, player, event);
  }

  private Boolean canCreateShop(ShopType type, Player player) {
    if (type == ShopType.GLOBALSHOP) {
      final RankProperties playerRankProperties = GlobalMarketChest.plugin.getRanksLoader().getPlayerProperties(player);
      final Boolean canCreate = playerRankProperties.canCreateShop(player);
      if (!canCreate)
        PlayerUtils.sendMessage(player, format("ErrorMessages.MaxGlobalShop", "maxGlobalShop", playerRankProperties.getMaxGlobalShopByPlayer()));
      return canCreate;
    }

    return true;
  }

  /**
   * Open shop interface to create it
   *
   * @param type Type of shop
   * @param player Player that create the shop
   * @param event  Sign event
   */
  private void openCreationShopInterface(ShopType type, Player player, SignChangeEvent event) {
    final ShopInfo shop = new ShopInfo(
        player.getUniqueId().toString(),
        type,
        event.getBlock().getLocation(),
        event.getBlock().getLocation().add(0.5, 0, 0.5),
        ShopUtils.generateName()
    );
    final InventoryGUI inv = new InventoryGUI(player);

    inv.getTransaction().put(TransactionKey.SHOP_INFO, shop);
    inv.getTransaction().put(TransactionKey.SIGN_LOCATION, event.getBlock().getLocation());
    GlobalMarketChest.plugin.inventories.addInventory(player.getUniqueId(), inv);
    inv.open();
    inv.getTransaction().put(TransactionKey.HAS_RETURN, false);
    inv.loadInterface(InterfaceType.SHOP_CREATION_MODE);
  }
}
