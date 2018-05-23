package fr.epicanard.globalmarketchest.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.experimental.UtilityClass;

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
    ItemMeta met = item.getItemMeta();
    met.removeEnchant(Enchantment.BINDING_CURSE);
    met.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(met);
    return item;
  }
}