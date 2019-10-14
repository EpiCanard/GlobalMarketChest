package fr.epicanard.globalmarketchest.gui.shops.baseinterfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.exceptions.WarnException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.ReturnBack;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import org.bukkit.inventory.ItemStack;

public abstract class UndoAuction extends ShopInterface {

  public UndoAuction(InventoryGUI inv) {
    super(inv);
  }

  /**
   * Undo the selected auction
   *
   * @param i Inventory
   * @param getItems Define if the player get back items or not
   */
  protected void undoAuction(InventoryGUI i, Boolean getItems) {
    final AuctionInfo auction = i.getTransactionValue(TransactionKey.AUCTION_INFO);

    final ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());
    item.setAmount(auction.getAmount());
    try {
      if (getItems)
      PlayerUtils.hasEnoughPlaceWarn(i.getPlayer().getInventory(), item);
      if (GlobalMarketChest.plugin.auctionManager.undoAuction(auction.getId(), auction.getPlayerStarter())) {
        if (getItems)
        i.getPlayer().getInventory().addItem(ItemStackUtils.splitStack(item, auction.getAmount()));
        PlayerUtils.sendMessageConfig(i.getPlayer(), "InfoMessages.UndoAuction");
        ReturnBack.execute(null, i);
      } else
      throw new WarnException("CantUndoAuction");
    } catch (WarnException e) {
      i.getWarn().warn(e.getMessage(), 49);
    }
  }

  /**
   * Undo the selected auction
   *
   * @param i Inventory
   */
  protected void undoAuction(InventoryGUI i) {
    this.undoAuction(i, true);
  }
}