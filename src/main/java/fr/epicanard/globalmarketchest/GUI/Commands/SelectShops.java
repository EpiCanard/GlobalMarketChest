package fr.epicanard.globalmarketchest.GUI.Commands;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.Configuration.ConfigLoader;
import fr.epicanard.globalmarketchest.GUI.InventoryGUI;
import fr.epicanard.globalmarketchest.GUI.Buttons.Button;
import fr.epicanard.globalmarketchest.GUI.Buttons.ButtonLeave;

public class SelectShops extends InventoryGUI {

  public SelectShops(String typ) {
    super("Select a shop", 6);
    ConfigLoader conf = GlobalMarketChest.plugin.getConfigLoader();
    if (conf != null && conf.getLanguages() != null && conf.getLanguages().getString("GUITitle.SelectShop") != null)
      super.setName(conf.getLanguages().getString("GUITitle.SelectShop"));
    super.buildInterface();
    
    //Button but = (Button)new ButtonLeave(8);
    //super.addButton(but);
    //but.addToGUI(this);
    ItemStack[] items = GlobalMarketChest.plugin.interfaces.get(typ);
    for (int i = 0; i < 54; i++) {
    	this.getInventory().setItem(i, items[i]);
    }
  }
}
