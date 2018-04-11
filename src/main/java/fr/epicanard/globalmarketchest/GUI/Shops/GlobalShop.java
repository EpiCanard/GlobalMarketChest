package fr.epicanard.globalmarketchest.GUI.Shops;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.Configuration.ConfigLoader;
import fr.epicanard.globalmarketchest.GUI.InventoryGUI;
import fr.epicanard.globalmarketchest.GUI.Buttons.ButtonLeave;
import fr.epicanard.globalmarketchest.GUI.Buttons.ButtonNewAuction;
import fr.epicanard.globalmarketchest.GUI.Buttons.ButtonOverview;
import fr.epicanard.globalmarketchest.GUI.Buttons.ButtonSearch;
import fr.epicanard.globalmarketchest.Utils.Utils;

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
