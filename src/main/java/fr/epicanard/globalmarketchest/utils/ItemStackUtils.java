package fr.epicanard.globalmarketchest.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;

/**
 * Utility class for ItemStacks
 */
@UtilityClass
public class ItemStackUtils {
  /**
   * Get the itemstack from the specify key
   * 
   * @param name Minecraft item name (ex: minecraft:chest)
   * @return ItemStack created from minecraft key
   */
  public ItemStack getItemStack(String name) {
    if (name == null)
      return null;

    String[] spec = name.split("/");
    MinecraftKey mk = new MinecraftKey(spec[0]);
    if (Item.REGISTRY.get(mk) == null)
      return null;
    ItemStack item = CraftItemStack.asNewCraftStack(Item.REGISTRY.get(mk));
    if (spec.length > 1)
      item.setDurability(Short.parseShort(spec[1]));
    return item;
  }

  /**
   * Get the item name from config file and then get the itemstack
   * 
   * @param path Path to item name in config
   * @return ItemStack created from minecraft key
   */
  public ItemStack getItemStackFromConfig(String path) {
    String itemName = GlobalMarketChest.plugin.getConfigLoader().getConfig().getString(path);
    return ItemStackUtils.getItemStack(itemName);
  }

  /**
   * Set ItemMeta to the specific item
   * 
   * @param item        ItemStack used
   * @param displayName Name displayed on the item
   * @param lore        Lore of the item to add (List)
   */
  private ItemStack setItemMeta(ItemStack item, String displayName, List<String> lore) {
    if (item == null)
      return null;

    ItemMeta met = item.getItemMeta();
    met.setDisplayName((displayName == null) ? " " : Utils.toColor(displayName));
    if (lore != null) {
      lore = lore.stream().map(element ->  Utils.toColor(element)).collect(Collectors.toList());
      met.setLore(lore);
    }
    item.setItemMeta(met);
    return item;
  }

  /**
   * Set ItemMeta to the specific item
   * 
   * @param item        ItemStack used
   * @param displayName Name displayed on the item
   */
  public ItemStack setItemStackMeta(ItemStack item, String displayName) {
    return setItemMeta(item, displayName, null);
  }

  /**
   * Set ItemMeta to the specific item
   * 
   * @param item        ItemStack used
   * @param displayName Name displayed on the item
   * @param lore        Lore of the item to add (List)
   */
  public ItemStack setItemStackMeta(ItemStack item, String displayName, List<String> lore) {
    return setItemMeta(item, displayName, lore);
  }

  /**
   * Set ItemMeta to the specific item
   * 
   * @param item        ItemStack used
   * @param displayName Name displayed on the item
   * @param lore        Lore of the item to add (String)
   */
  public ItemStack setItemStackMeta(ItemStack item, String displayName, String lore) {
    if (lore == null)
      return setItemMeta(item, displayName, null);
    return setItemMeta(item, displayName, Arrays.asList(lore.split(";")));
  }

  /**
   * Set ItemMeta to the specific item
   * 
   * @param item        ItemStack used
   * @param displayName Name displayed on the item
   * @param lore        Lore of the item to add (String[])
   */
  public ItemStack setItemStackMeta(ItemStack item, String displayName, String[] lore) {
    if (lore == null)
      return setItemMeta(item, displayName, null);
    return setItemMeta(item, displayName, Arrays.asList(lore));
  }
}