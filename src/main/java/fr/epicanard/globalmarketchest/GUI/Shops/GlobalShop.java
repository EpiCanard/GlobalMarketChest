package fr.epicanard.globalmarketchest.gui.shops;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.configuration.ConfigLoader;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.buttons.ButtonLeave;
import fr.epicanard.globalmarketchest.gui.buttons.ButtonNewAuction;
import fr.epicanard.globalmarketchest.gui.buttons.ButtonOverview;
import fr.epicanard.globalmarketchest.gui.buttons.ButtonSearch;
import fr.epicanard.globalmarketchest.utils.Utils;

public class GlobalShop extends InventoryGUI {

  public GlobalShop() {
    super(null, 6);

    super.buildInterface();
    
    super.addButton(new ButtonLeave(Utils.toPos(8, 0)), true);
    super.addButton(new ButtonOverview(Utils.toPos(0, 5)), true);
    super.addButton(new ButtonNewAuction(Utils.toPos(8, 5)), true);
    super.addButton(new ButtonSearch(Utils.toPos(0, 1)), true);

    
    ConfigLoader conf = GlobalMarketChest.plugin.getConfigLoader();
    String ic = conf.getConfig().getString("Sign.Appearance.GlobalShop.DisplayItem");
    System.out.println(ic);
    super.setIcon(Utils.getItemStack(ic));
  }
}
