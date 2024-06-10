package fr.epicanard.globalmarketchest.utils.reflection;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class NMSUtils {

  /**
   * Convert a spigot ItemStack to NMS ItemStack
   * @param itemStack Spigot ItemStack
   * @return NMS ItemStack or null
   */
  Object toNmsItemstack(ItemStack itemStack) {
    try {
      return Path.BUKKIT
        .getClass("inventory.CraftItemStack")
        .getDeclaredMethod("asNMSCopy", ItemStack.class)
        .invoke(null, itemStack);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Convert a NMS ItemStack to spigot ItemStack
   * @param nmsItemStack NMS ItemStack
   * @return spigot ItemStack
   */
  ItemStack toItemstack(Object nmsItemStack) {
    try {
      return (ItemStack) Path.BUKKIT
        .getClass("inventory.CraftItemStack")
        .getDeclaredMethod("asBukkitCopy", nmsItemStack.getClass())
        .invoke(null, nmsItemStack);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
