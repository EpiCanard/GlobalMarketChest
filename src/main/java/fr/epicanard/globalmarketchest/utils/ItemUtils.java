package fr.epicanard.globalmarketchest.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
  public static ItemStack addGlow(ItemStack item) {
    ItemMeta met = item.getItemMeta();
    met.addEnchant(Enchantment.BINDING_CURSE, 1, false);
    met.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(met);
    return item;
  }

  public static ItemStack removeGlow(ItemStack item) {
    ItemMeta met = item.getItemMeta();
    met.removeEnchant(Enchantment.BINDING_CURSE);
    met.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    item.setItemMeta(met);
    return item;
  }
}