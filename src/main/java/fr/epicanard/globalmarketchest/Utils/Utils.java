package fr.epicanard.globalmarketchest.Utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;

public class Utils {
  
  public static Boolean isId(String name) {
    String[] spec = name.split("/");
    if (spec[0].matches("^[0-9]*$")) {
      if (spec.length > 1)
        return spec[1].matches("^[0-9]*$");
      return true;
    }
    return false;
  }
  
  public static ItemStack getItemStack(String name) {
    if (name == null)
      return null;
    return (Utils.isId(name)) ? Utils.getItemStackById(name) : Utils.getItemStackByName(name);
  }
  
  public static String toColor(String toChange) {
  	return toChange.replaceAll("&", "§");
  }
  
  public static ItemStack getItemStackByName(String name) {
    String[] spec = name.split("/");
    MinecraftKey mk = new MinecraftKey(spec[0]);
    if (Item.REGISTRY.get(mk) == null)
      return null;
    ItemStack item = CraftItemStack.asNewCraftStack(Item.REGISTRY.get(mk));
    if (spec.length > 1)
      item.setDurability(Short.parseShort(spec[1]));
    return item;
  }
  
  public static ItemStack getItemStackById(String name) {
    String[] spec = name.split("/");
    if (Item.REGISTRY.getId(Integer.parseInt(spec[0])) == null)
      return null;
    ItemStack item = CraftItemStack.asNewCraftStack(Item.REGISTRY.getId(Integer.parseInt(spec[0])));
    if (spec.length > 1)
      item.setDurability(Short.parseShort(spec[1]));
    return item;
  }

  private static ItemStack setItemMeta(ItemStack item, String displayName, List<String> lore) {
    if (item == null)
      return null;
    ItemMeta met = item.getItemMeta();
    met.setDisplayName((displayName == null) ? " " : toColor(displayName));
    
    if (lore != null) {
      for (int i = 0; i < lore.size(); i++) {
      	lore.set(i, toColor(lore.get(i)));
      }
      met.setLore(lore);
    }
    item.setItemMeta(met);
    return item;  	
  }
  
  public static ItemStack setItemStackMeta(ItemStack item, String displayName) {
  	return setItemMeta(item, displayName, null);
  }

  public static ItemStack setItemStackMeta(ItemStack item, String displayName, List<String> lore) {
  	return setItemMeta(item, displayName, lore);
  }
  
  public static ItemStack setItemStackMeta(ItemStack item, String displayName, String lore) {
    if (lore == null)
      return setItemMeta(item, displayName, null);
    return setItemMeta(item, displayName, Arrays.asList(lore.split(";")));
  }

  public static ItemStack setItemStackMeta(ItemStack item, String displayName, String[] lore) {
    if (lore == null)
      return setItemMeta(item, displayName, null);
    return setItemMeta(item, displayName, Arrays.asList(lore));
  }

  public static int toPos(int x, int y) {
   return y * 9 + x;
  }
}
