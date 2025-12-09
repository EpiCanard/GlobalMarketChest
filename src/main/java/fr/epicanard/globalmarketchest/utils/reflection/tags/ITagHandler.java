package fr.epicanard.globalmarketchest.utils.reflection.tags;

import org.bukkit.inventory.ItemStack;

public interface ITagHandler {

  /**
   * Define if the GMC NBT TAG is set on this item
   *
   * @param itemStack Item to analyze
   * @return Return if the item as gmc nbt tag
   */
  boolean hasTag(ItemStack itemStack);

  /**
   * Set the custom GMC NBT TAG on item in parameter
   *
   * @param itemStack Item on which add NBT TAG
   * @return ItemStack modified
   */
  ItemStack setTag(ItemStack itemStack);
}
