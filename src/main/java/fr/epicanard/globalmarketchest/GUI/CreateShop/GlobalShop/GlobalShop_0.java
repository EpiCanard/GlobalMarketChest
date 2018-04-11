package fr.epicanard.globalmarketchest.GUI.CreateShop.GlobalShop;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.GUI.InventoryGUI;

public class GlobalShop_0 extends InventoryGUI {

  public GlobalShop_0() {
    super("Creation of Global Shop", 4);
    
    YamlConfiguration file = GlobalMarketChest.plugin.getConfigLoader().getLanguages();
    if (file != null) {
      String title = file.getString("GUITitle.CreationShop.GlobalShop");
      if (title != null)
        this.setName(title);
    }
    this.setLeave(true);
  }
}
