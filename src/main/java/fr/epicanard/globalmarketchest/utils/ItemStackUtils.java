package fr.epicanard.globalmarketchest.utils;

import java.util.ArrayList;
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
    ItemUtils.hideMeta(item);
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

  public String getMinecraftKey(ItemStack item) {
    net.minecraft.server.v1_12_R1.ItemStack it = CraftItemStack.asNMSCopy(item);
    MinecraftKey mk = Item.REGISTRY.b(it.getItem());
    return mk.b() + ":" + mk.getKey();
  }

  /**
   * Set ItemMeta to the specific item
   * 
   * @param item        ItemStack used
   * @param displayName Name displayed on the item
   * @param lore        Lore of the item to add (List)
   * 
   * @return return the itemstack in param
   */
  public ItemStack setItemStackMeta(ItemStack item, String displayName, List<String> lore) {
    if (item == null)
      return null;

    ItemMeta met = item.getItemMeta();
    if (met != null)
      met.setDisplayName((displayName == null) ? " " : Utils.toColor(displayName));
    item.setItemMeta(ItemStackUtils.setMetaLore(met, lore));
    return item;
  }

  /**
   * Set lore on item meta
   * 
   * @param meta
   * @param lore
   * 
   * @return return the meta in param
   */
  private ItemMeta setMetaLore(ItemMeta meta, List<String> lore) {
    if (meta != null && lore != null) {
      lore = lore.stream().map(element ->  Utils.toColor(element)).collect(Collectors.toList());
      meta.setLore(lore);
    }
    return meta;
  }

  /**
   * Set lore on itemstack
   * 
   * @param item
   * @param lore
   * 
   * @return return item in param
   */
  public ItemStack setItemStackLore(ItemStack item, List<String> lore) {
    if (item != null)
      item.setItemMeta(ItemStackUtils.setMetaLore(item.getItemMeta(), lore));
    return item;
  }

  /**
   * Add lore to existing lore on itemstack
   * 
   * @param item
   * @param lore
   * 
   * @return return item in param
   */
  public ItemStack addItemStackLore(ItemStack item, List<String> lore) {
    if (item != null && lore != null) {
      ItemMeta meta = item.getItemMeta();
      List<String> loreDup = new ArrayList<>();
      if (meta.getLore() != null)
        loreDup.addAll(meta.getLore());
      loreDup.addAll(lore);
      item.setItemMeta(ItemStackUtils.setMetaLore(meta, loreDup));
    }
    return item;
  }
  /**
   * Merge two itemstack array in one
   * 
   * @param a first array
   * @param b second array
   * 
   * @return return first array instance but merged
   */
  public ItemStack[] mergeArray(ItemStack[] a, ItemStack[] b) {
    for (int i = 0; i < a.length && i < b.length; i++)
      if (b[i] != null && !b[i].equals(Utils.getBackground()))
        a[i] = b[i];
    return a;
  }
}