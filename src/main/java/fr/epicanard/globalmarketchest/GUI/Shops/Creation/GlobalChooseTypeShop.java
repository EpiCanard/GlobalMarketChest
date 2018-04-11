package fr.epicanard.globalmarketchest.GUI.Shops.Creation;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.Configuration.ConfigLoader;
import fr.epicanard.globalmarketchest.GUI.InventoryGUI;
import fr.epicanard.globalmarketchest.GUI.Buttons.ButtonLeave;
import fr.epicanard.globalmarketchest.Utils.Utils;

public class GlobalChooseTypeShop extends InventoryGUI {

  public GlobalChooseTypeShop() {
    super(null, 4);
    ConfigLoader conf = GlobalMarketChest.plugin.getConfigLoader();
    if (conf != null && conf.getLanguages() != null && conf.getLanguages().getString("GUITitle.ChooseTypeShop") != null)
      super.setName(conf.getLanguages().getString("GUITitle.ChooseTypeShop"));
    super.buildInterface();

    super.addButton(new ButtonLeave(Utils.toPos(8, 0)), true);

  }

}
