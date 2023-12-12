package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.auctions.StatusAuction;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.InterfaceType;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.actions.ReturnBack;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.UndoAuction;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import org.bukkit.inventory.ItemStack;

public class EditAuction extends UndoAuction {

  public EditAuction(InventoryGUI inv) {
    super(inv);
    this.inv.getTransaction().put(TransactionKey.AUCTION_LORE_CONFIG, AuctionLoreConfig.OWN);

    this.isTemp = true;
    this.actions.put(0, new PreviousInterface());
    this.actions.put(33, this::renewAuction);
    this.actions.put(29, this::undoAuction);


    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    final ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());

    if (ShulkerBoxContent.isShulker(item))
      this.togglerManager.setTogglerWithAction(inv.getInv(), 49, this.actions, new NextInterface(InterfaceType.SHULKER_BOX_CONTENT));
  }

  @Override
  public void load() {
    super.load();
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    this.setIcon(ItemStackUtils.addItemStackLore(
        DatabaseUtils.deserialize(auction.getItemMeta()), auction.getLore(AuctionLoreConfig.OWN, this.inv.getPlayer())
    ));
  }

  /**
   * Renew the selected auction to current date
   *
   * @param i
   */
  private void renewAuction(InventoryGUI i) {
    final AuctionInfo auction = i.getTransactionValue(TransactionKey.AUCTION_INFO);
    final Integer maxAuctionNumber = this.inv.getPlayerRankProperties().getMaxAuctionByPlayer();
    final Integer playerAuctions = this.inv.getTransactionValue(TransactionKey.PLAYER_AUCTIONS);
    final Integer expirationDays = i.getPlayerRankProperties().getNumberDaysExpiration();

    if ((auction.getStatus() == StatusAuction.IN_PROGRESS || playerAuctions + 1 <= maxAuctionNumber)
        && GlobalMarketChest.plugin.auctionManager.renewAuction(auction.getId(), expirationDays)) {
      PlayerUtils.sendMessageConfig(i.getPlayer(), "InfoMessages.RenewAuction");
      ReturnBack.execute(null, this.inv);
    } else {
      i.getWarn().warn("CantRenewAuction", 49);
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.AUCTION_INFO);
    this.inv.getTransaction().remove(TransactionKey.AUCTION_LORE_CONFIG);
  }
}
