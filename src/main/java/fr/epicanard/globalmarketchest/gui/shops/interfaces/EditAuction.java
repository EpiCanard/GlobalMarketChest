package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.exceptions.WarnException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.actions.ReturnBack;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopInterface;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

public class EditAuction extends ShopInterface {

  public EditAuction(InventoryGUI inv) {
    super(inv);

    this.isTemp = true;
    this.actions.put(0, new PreviousInterface());
    this.actions.put(33, this::renewAuction);
    this.actions.put(29, this::undoAuction);
  }

  @Override
  public void load() {
    super.load();
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);
    this.setIcon(ItemStackUtils.addItemStackLore(DatabaseUtils.deserialize(auction.getItemMeta()), auction.getLore(AuctionLoreConfig.OWN)));
  }

  /**
   * Renew the selected auction to current date
   *
   * @param i
   */
  private void renewAuction(InventoryGUI i) {
    final AuctionInfo auction = i.getTransactionValue(TransactionKey.AUCTIONINFO);

    if (GlobalMarketChest.plugin.auctionManager.renewAuction(auction.getId()) == true) {
      PlayerUtils.sendMessageConfig(i.getPlayer(), "InfoMessages.RenewAuction");
      ReturnBack.execute(null, this.inv);
    } else
      i.getWarn().warn("CantRenewAuction", 49);
  }

  /**
   * Undo the selected auction
   *
   * @param i
   */
  private void undoAuction(InventoryGUI i) {
    final AuctionInfo auction = i.getTransactionValue(TransactionKey.AUCTIONINFO);

    final ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());
    item.setAmount(auction.getAmount());
    try {
      PlayerUtils.hasEnoughPlaceWarn(i.getPlayer().getInventory(), item);
      if (GlobalMarketChest.plugin.auctionManager.undoAuction(auction.getId(), auction.getPlayerStarter()) == true) {
        i.getPlayer().getInventory().addItem(ItemStackUtils.splitStack(item, auction.getAmount()));
        PlayerUtils.sendMessageConfig(i.getPlayer(), "InfoMessages.UndoAuction");
        ReturnBack.execute(null, i);
      } else
        throw new WarnException("CantUndoAuction");
    } catch (WarnException e) {
      i.getWarn().warn(e.getMessage(), 49);
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.AUCTIONINFO);
  }
}
