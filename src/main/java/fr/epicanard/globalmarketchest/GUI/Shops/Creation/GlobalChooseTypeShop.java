package fr.epicanard.globalmarketchest.gui.shops.creation;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.configuration.ConfigLoader;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.buttons.ButtonLeave;
import fr.epicanard.globalmarketchest.utils.Utils;

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
