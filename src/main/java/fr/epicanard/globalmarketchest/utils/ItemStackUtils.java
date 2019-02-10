package fr.epicanard.globalmarketchest.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.Reflection.VersionSupportUtils;
import lombok.experimental.UtilityClass;

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

    ItemStack item = VersionSupportUtils.getInstance().getItemStack(spec[0]);

    if (item != null) {
      if (spec.length > 1)
        item.setDurability(Short.parseShort(spec[1]));
      ItemUtils.hideMeta(item);
    }

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
   * Get minecraft key from item
   *
   * @param item ItemStack
   * @return minecraft key in string
   */
  public String getMinecraftKey(ItemStack item) {
    return VersionSupportUtils.getInstance().getMinecraftKey(item);
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

  /**
   * Get max stack size
   * If amount is greater than the maxstacksize of the item it return the maxstacksize
   *
   * @param item ItemStack
   * @param amount Amount to set
   * @return The max stack size
   */
  public Integer getMaxStack(ItemStack item, Integer amount) {
    return (amount > item.getMaxStackSize()) ? item.getMaxStackSize() : amount;
  }

  /**
   * Get the item name for an itemstack
   *
   * @param item Itemstack to get item name
   * @return Item display name
   */
  public String getItemStackDisplayName(ItemStack item) {
    return VersionSupportUtils.getInstance().getItemStackDisplayName(item);
  }

  /**
   * Create n itemStack to don't overload maxItemStackSize
   *
   * @param item Reference itemstack
   * @param amount Number of items in total
   * @return The item splitted to don't exceed maxStackSize
   */
  public ItemStack[] splitStack(ItemStack item, Integer amount) {
    List<ItemStack> items = new ArrayList<>();
    int i = amount;
    ItemStack tmp;
    Integer max;

    while (i > 0) {
      tmp = item.clone();
      max = ItemStackUtils.getMaxStack(item, i);
      tmp.setAmount(max);
      items.add(tmp);
      i -= max;
      if (max == 0)
        break;
    }

    return items.toArray(new ItemStack[0]);
  }

  /**
   * Define if the item is damaged
   * 
   * @param item ItemStack to define if it is damaged
   * @return
   */
  public Boolean isDamaged(ItemStack item) {
    return ((Damageable)item.getItemMeta()).getDamage() > 0;
  }

  /**
   * Define if the block is black listed or not
   * 
   * @param item Item to define if blacklisted
   * @return
   */
  public Boolean isBlacklisted(ItemStack item) {
    String mk = ItemStackUtils.getMinecraftKey(item);
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getStringList("ItemsBlacklist").contains(mk);
  }
}