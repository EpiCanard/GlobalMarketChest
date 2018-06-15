package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.function.Consumer;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.CategoryHandler;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NewAuction;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class CategoryView extends ShopInterface {

  public CategoryView(InventoryGUI inv) {
    super(inv);
    this.actions.put(53, new NewAuction());
  }

  @Override
  public void load() {
    super.load();
    Consumer<InventoryGUI> callable = new NextInterface("AuctionViewGroup");

    CategoryHandler h = GlobalMarketChest.plugin.getCatHandler();
    String[] cat = h.getCategories().toArray(new String[0]);
    for (int i = 0; i < cat.length; i++) {
      int pos = Utils.toPos(i % 5 + 2, (i / 5) * 2 + 2);
      final int j = i;
      this.actions.put(pos, in -> {
        this.inv.getTransaction().put(TransactionKey.CATEGORY, cat[j]);
        callable.accept(in);
      });
      this.inv.getInv().setItem(pos, ItemStackUtils.setItemStackMeta(h.getDisplayItem(cat[i]), h.getDisplayName(cat[i])));
    }
  }

  @Override
  public void unload() {
  }
}
