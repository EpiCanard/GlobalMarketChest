package fr.epicanard.globalmarketchest.utils;

import fr.epicanard.globalmarketchest.utils.reflection.VersionSupportUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility class for ItemStacks
 */
@UtilityClass
public class ItemStackUtils {

  /**
   * Define items that are damageable for 1.12
   */
  private List<String> DAMAGEABLE_1_12 = Arrays.asList(
    "ANVIL", "FLINT_AND_STEEL", "FISHING_ROD", "SHIELD", "ELYTRA", "BOW", "SHEARS",
    "_HOE", "_SWORD", "_SPADE", "_PICKAXE", "_AXE",
    "_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS"
  );

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
    String itemName = ConfigUtils.getString(path);
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
   * Set display name of item
   *
   * @param item        ItemStack used
   * @param displayName Name displayed on the item
   *
   * @return return the itemstack in param
   */
  public ItemStack setItemStackDisplayName(ItemStack item, String displayName) {
    if (item == null)
      return null;

    ItemMeta met = item.getItemMeta();
    if (met != null) {
      met.setDisplayName((displayName == null) ? " " : Utils.toColor(displayName));
      item.setItemMeta(met);
    }
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
      if (b[i] != null)
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
  public Boolean isDamaged_1_12(ItemStack item) {
    final Predicate<String> match = damageable -> item.getType().name().matches(String.format("(.*)%s(.*)", damageable));
    return ItemStackUtils.DAMAGEABLE_1_12.stream().anyMatch(match) && item.getDurability() > 0;
  }

  /**
   * Define if the item is damaged
   *
   * @param item ItemStack to define if it is damaged
   * @return
   */
  public Boolean isDamaged_Latest(ItemStack item) {
    return ((Damageable) item.getItemMeta()).getDamage() > 0 && item.getType() != Material.getMaterial("PLAYER_HEAD");
  }

  /**
   * Define if the item is damaged
   *
   * @param item ItemStack to define if it is damaged
   * @return
   */
  public Boolean isDamaged(ItemStack item) {
    return Version.isEqualsTo(Version.V1_12) ? isDamaged_1_12(item) : isDamaged_Latest(item);
  }

  /**
   * Define if the block is black listed or not
   *
   * @param item Item to define if blacklisted
   * @return If item is blacklisted
   */
  public Boolean isBlacklisted(final ItemStack item) {
    final String mk = ItemStackUtils.getMinecraftKey(item);
    final List<String> itemLore = item.getItemMeta().getLore();
    final List<String> blacklistLore = ConfigUtils.getStringList("ItemsBlacklist.Lores");
    return ConfigUtils.getStringList("ItemsBlacklist.Items").contains(mk) || matchLore(itemLore, blacklistLore);
  }

  /**
   * Match if one of itemLore contains one of blacklistLore element
   *
   * @param itemLore Item lore list
   * @param blacklistLore Blacklist lore
   * @return If two lore list match
   */
  private Boolean matchLore(final List<String> itemLore, final List<String> blacklistLore) {
    if (itemLore != null && blacklistLore != null) {
      for (String lore : itemLore) {
        for (String bl : blacklistLore) {
          if (lore.contains(bl))
            return true;
        }
      }
    }
    return false;
  }
}
