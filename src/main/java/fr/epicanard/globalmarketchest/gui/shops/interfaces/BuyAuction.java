package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.exceptions.WarnException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.actions.ReturnBack;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

public class BuyAuction extends ShopInterface {

  public BuyAuction(InventoryGUI inv) {
    super(inv);

    this.isTemp = true;
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);
    ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());
    this.setIcon(item);
    this.actions.put(0, new PreviousInterface());
    this.actions.put(22, this::buyAuction);
  }

  /**
   * Renew the selected auction to current date
   * 
   * @param i
   */
  private void buyAuction(InventoryGUI i) {
    AuctionInfo auction = i.getTransactionValue(TransactionKey.AUCTIONINFO);
    ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());

    item.setAmount(auction.getAmount());

    try {
      if (GlobalMarketChest.plugin.economy.getMoneyOfPlayer(i.getPlayer().getUniqueId()) < auction.getTotalPrice())
        throw new WarnException("NotEnoughMoney");
      PlayerUtils.hasEnoughPlaceWarn(i.getPlayer().getInventory(), item);
      if (!GlobalMarketChest.plugin.auctionManager.buyAuction(auction.getId(), i.getPlayer()))
        throw new WarnException("CantBuyAuction");
      i.getPlayer().getInventory().addItem(item);

      GlobalMarketChest.plugin.economy.exchangeMoney(i.getPlayer().getUniqueId(), UUID.fromString(auction.getPlayerStarter()), auction.getTotalPrice());
      ReturnBack.execute(null, i);
    } catch (WarnException e) {
      i.getWarn().warn(e.getMessage(), 49);
    }
  }
}
