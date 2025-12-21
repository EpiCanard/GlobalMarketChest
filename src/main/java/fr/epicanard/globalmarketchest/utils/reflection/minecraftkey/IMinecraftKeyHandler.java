package fr.epicanard.globalmarketchest.utils.reflection.minecraftkey;

import org.bukkit.inventory.ItemStack;

public interface IMinecraftKeyHandler {

  /**
   * Get the minecraft key of an itemstack
   *
   * @param itemStack Item
   * @return minecraft:dirt
   */
  String getMinecraftKey(ItemStack itemStack);
}
