package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;

public class CreateAuctionPrice extends ShopInterface {

  public CreateAuctionPrice(InventoryGUI inv) {
    super(inv);
    this.actions.put(0, new PreviousInterface());
  }

  @Override
  public void load() {
    super.load();
  }

  @Override
  public void unload() {
  }
}
