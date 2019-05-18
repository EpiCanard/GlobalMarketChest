package fr.epicanard.globalmarketchest.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.experimental.UtilityClass;

/**
 * Utility Class about Item
 */
@UtilityClass
public class ItemUtils {

  /**
   * Add glow effect to item
   * Add Curse Binding effect and hide enchants
   * 
   * /!\ Don't use on selling items
   * 
   * @param ItemStack
   * @return ItemStack
   */
  public ItemStack addGlow(ItemStack item) {
    if (item == null)
      return null;
    ItemMeta met = item.getItemMeta();
    met.addEnchant(Enchantment.BINDING_CURSE, 1, false);
    met.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(met);
    return item;
  }

  /**
   * Remove glow effect to item
   * Remove Curse Binding effect and show enchants
   * 
   * /!\ Don't use on selling items
   * 
   * @param ItemStack
   * @return ItemStack
   */
  public ItemStack removeGlow(ItemStack item) {
    if (item == null)
      return null;
    ItemMeta met = item.getItemMeta();
    met.removeEnchant(Enchantment.BINDING_CURSE);
    met.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(met);
    return item;
  }

  /**
   * Add or remove the glow to an item inside the inventory at a postion
   * 
   * @param inv    Inventory to use
   * @param pos    Position to set the glow
   * @param toGlow If true add the glow else remove the glow effect
   */
  public void setGlow(Inventory inv, int pos, Boolean toGlow) {
    ItemStack item = inv.getItem(pos);
    item = (toGlow) ? ItemUtils.addGlow(item) : ItemUtils.removeGlow(item);
    inv.setItem(pos, item);
  }

  /**
   * Hide the meta of the item
   * 
   * @param item
   * @return return the param itemstack
   */
  public ItemStack hideMeta(ItemStack item) {
    ItemMeta met = item.getItemMeta();
    if (met != null) {
      met.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
      item.setItemMeta(met);
    }
    return item;
  }
}