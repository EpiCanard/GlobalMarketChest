package fr.epicanard.globalmarketchest.gui;

import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import lombok.Getter;

public class CategoryHandler {
  YamlConfiguration config;
  @Getter
  Set<String> categories;
  
  public CategoryHandler(YamlConfiguration conf) {
    this.config = conf;
    this.categories = this.config.getKeys(false);
  }
  
  public String[] getItems(String category) {
    List<String> lst = this.config.getStringList(category + ".Items");
    return (lst != null) ? (String[]) lst.toArray() : null;
  }

  public String getDisplayName(String category) {
    return this.config.getString(category + ".DisplayName");
  }

  public ItemStack getDisplayItem(String category) {
    String item = this.config.getString(category + ".DisplayItem");
    if (item == null)
      item = "minecraft:barrier";
    
    ItemStack it = ItemStackUtils.getItemStack(item);
    return (it == null) ? ItemStackUtils.getItemStack("minecraft:barrier") : it;
  }
  
  public String getCategory(ItemStack item) {
    return "Unclassified";
  }
  
  public Boolean isCategory(String category) {
    if (this.categories == null)
      return false;
    return this.categories.contains(category);
  }
}
