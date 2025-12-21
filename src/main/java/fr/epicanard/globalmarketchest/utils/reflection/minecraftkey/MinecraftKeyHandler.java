package fr.epicanard.globalmarketchest.utils.reflection.minecraftkey;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class MinecraftKeyHandler implements IMinecraftKeyHandler {

  /**
   * Get the minecraft key of an itemstack
   *
   * @param itemStack Item
   * @return minecraft:dirt
   */
  @Override
  public String getMinecraftKey(ItemStack itemStack) {
    NamespacedKey key = itemStack.getType().getKey();
    if (key != null)
      return key.getNamespace() + ":" + key.getKey();
    return null;
  }
}
