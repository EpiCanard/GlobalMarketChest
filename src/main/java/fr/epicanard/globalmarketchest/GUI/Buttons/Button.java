package fr.epicanard.globalmarketchest.GUI.Buttons;

import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.Configuration.ConfigLoader;
import fr.epicanard.globalmarketchest.GUI.InventoryGUI;
import fr.epicanard.globalmarketchest.Utils.Utils;

public abstract class Button {
  private ItemStack item;
  private String name;
  private List<String> description;
  private int pos;

  public Button(String configName, int pos) {
    this.pos = pos;
    ConfigLoader conf = GlobalMarketChest.plugin.getConfigLoader();

    this.item = Utils.getItemStack(conf.getConfig().getString("Interfaces.Buttons." + configName));
    if (this.item == null)
      this.item = Utils.getItemStack("minecraft:barrier");
    
    this.name = conf.getLanguages().getString("Buttons." + configName + ".Name");
    this.name = this.name.replaceAll("&", "§");
    String desc = conf.getLanguages().getString("Buttons." + configName + ".Description");
    
    if (desc != null) {
      desc = desc.replaceAll("&", "§");
      this.description = Arrays.asList(desc.split(";"));
      Utils.setItemStackMeta(this.item, this.name, this.description);
    }
    else {
      Utils.setItemStackMeta(this.item, this.name);      
    }
  }
  
  public Button(ItemStack item, String name, List<String> description, int pos) {
    this.item = item;
    this.name = name;
    this.description = description;
    this.pos = pos;
    Utils.setItemStackMeta(this.item, this.name, this.description);
  }

  public void addToGUI(InventoryGUI gui) {
    gui.setItemTo(this.pos, this.item);
  }
  
  public String getName() {
    return this.name;
  }

  public List<String> getDescription() {
    return this.description;
  }
  
  public int getPosition() {
    return this.pos;
  }

  public abstract void onButtonClick();
}
