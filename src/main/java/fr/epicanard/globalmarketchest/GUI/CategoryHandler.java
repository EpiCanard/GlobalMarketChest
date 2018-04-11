package fr.epicanard.globalmarketchest.GUI;

import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;

public class CategoryHandler {
  YamlConfiguration config;
  String[] categories;
  
  public CategoryHandler(YamlConfiguration conf) {
    this.config = conf;
    
    Set<String> keys = this.config.getKeys(false);
    this.categories = keys.toArray(new String[0]);
  }

  public String[] getCategories() {
    return this.categories;
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
    if (item == null) {
      item = "minecraft:barrier";
    }
    ItemStack it = this.getItemStack(item);
    return (it == null) ? this.getItemStack("minecraft:barrier") : it;
  }

  public ItemStack getItemStack(String name) {
    String[] spec = name.split("/");
    MinecraftKey mk = new MinecraftKey(spec[0]);
    if (Item.REGISTRY.get(mk) == null)
      return null;
    ItemStack item = CraftItemStack.asNewCraftStack(Item.REGISTRY.get(mk));
    if (spec.length > 1)
      item.setDurability(Short.parseShort(spec[1]));
    return item;
  }
  
  public String getCategory(ItemStack item) {
    return "Unclassified";
  }
  
  public Boolean isCategory(String category) {
    if (this.categories == null)
      return false;
    for(int i = 0; i < this.categories.length; i++) {
      if (this.categories[i] == category)
        return true;
    }
    return false;
  }
}
