package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NewAuction;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

public class AuctionViewList extends ShopInterface {
  private List<AuctionInfo> auctions = new ArrayList<>();

  public AuctionViewList(InventoryGUI inv) {
    super(inv);
 
    this.paginator.setLoadConsumer(pag -> {
      ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);
      ItemStack item = this.inv.getTransactionValue(TransactionKey.AUCTIONITEM);
      this.setIcon(item);

      GlobalMarketChest.plugin.auctionManager.getAuctionsByItem(shop.getGroup(), item, auctions -> {
        this.auctions = auctions;
        pag.setItemStacks(pag.getSubList(DatabaseUtils.toItemStacks(auctions, (itemstack, auction) -> {
          ItemStackUtils.setItemStackLore(itemstack, this.getLore(auction));
        })));
      });
    });

    this.paginator.setClickConsumer(this::selectAuction);

    this.actions.put(0, new PreviousInterface());
    this.actions.put(53, new NewAuction());
  }

  private List<String> getLore(AuctionInfo auction) {
    List<String> lore = new ArrayList<>();
    
    double price = BigDecimal.valueOf(auction.getPrice()).multiply(BigDecimal.valueOf(auction.getAmount())).doubleValue();
    lore.add(String.format("&7%s : &6%s", LangUtils.get("Divers.Quantity"), auction.getAmount()));
    lore.add(String.format("&7%s : &6%s", LangUtils.get("Divers.UnitPrice"), auction.getPrice()));
    lore.add(String.format("&7%s : &6%s", LangUtils.get("Divers.TotalPrice"), price));
    lore.add(String.format("&7%s : &6%s", LangUtils.get("Divers.Seller"), PlayerUtils.getOfflinePlayer(UUID.fromString(auction.getPlayerStarter())).getName()));
    return lore;
  }

  private void selectAuction(Integer pos) {
    if (pos >= this.auctions.size())
      return;
    AuctionInfo auction = this.auctions.get(pos);
    this.inv.getTransaction().put(TransactionKey.AUCTIONINFO, auction);
    if (!auction.getPlayerStarter().equals(PlayerUtils.getUUIDToString(this.inv.getPlayer())))
      this.inv.loadInterface("EditAuction");
    else
      this.inv.loadInterface("BuyAuction");
  }
}
