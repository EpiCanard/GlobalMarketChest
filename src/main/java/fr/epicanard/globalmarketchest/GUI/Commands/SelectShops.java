package fr.epicanard.globalmarketchest.gui.commands;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.configuration.ConfigLoader;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.buttons.Button;
import fr.epicanard.globalmarketchest.gui.buttons.ButtonLeave;

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
