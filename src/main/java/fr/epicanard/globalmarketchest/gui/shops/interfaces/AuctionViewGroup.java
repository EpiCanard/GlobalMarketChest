package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NewAuction;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;

public class AuctionViewGroup extends ShopInterface {

  public AuctionViewGroup(InventoryGUI inv) {
    super(inv);

    this.paginator.setLoadConsumer(pag -> {
      ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);
      String category = this.inv.getTransactionValue(TransactionKey.CATEGORY);
      
      GlobalMarketChest.plugin.auctionManager.getItemByCategory(shop.getGroup(), category, its -> {
        pag.setItemStacks(pag.getSubList(its));
      });
    });
    this.paginator.setClickConsumer(pos -> {
      if (pos >= this.paginator.getItemstacks().size())
        return;
      ItemStack item = this.paginator.getItemstacks().get(pos);
      this.inv.getTransaction().put(TransactionKey.AUCTIONITEM, item);
      this.inv.loadInterface("AuctionViewList");
    });

    this.actions.put(0, new PreviousInterface(() -> {
      this.inv.getTransaction().remove(TransactionKey.CATEGORY);
    }));
    this.actions.put(53, new NewAuction());
  }

  @Override
  public void load() {
    super.load();
  }

  @Override
  public void unload() {
  }
}
