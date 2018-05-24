package fr.epicanard.globalmarketchest.gui.shops;

import org.bukkit.inventory.Inventory;

public class ShopCreationLink extends ShopInterface {

  public ShopCreationLink(Inventory inv) {
    super(inv);
    if (this.paginator != null) {
      this.paginator.setLoadConsumer(pag -> {});
    }
  }

  @Override
  public void load() {
    super.load();
  }

  @Override
  public void unload() {
  }
}
