package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.CategoryHandler;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class GlobalShop extends ShopInterface {

  public GlobalShop(InventoryGUI inv) {
    super(inv);
  }

  @Override
  public void load() {
    super.load();

    CategoryHandler h = new CategoryHandler((YamlConfiguration)GlobalMarketChest.plugin.getConfigLoader().getCategories());
    String[] cat = h.getCategories().toArray(new String[0]);
    for (int i = 0; i < cat.length; i++) {
      this.inv.getInv().setItem(Utils.toPos(i % 5 + 2, (i / 5) * 2 + 2), ItemStackUtils.setItemStackMeta(h.getDisplayItem(cat[i]), h.getDisplayName(cat[i])));
    }
  }

  @Override
  public void unload() {
  }
}
