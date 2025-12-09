package fr.epicanard.globalmarketchest.utils.reflection.tags;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class TagHandler implements ITagHandler {

  private final NamespacedKey gmcTagNamespaceKey = new NamespacedKey(GlobalMarketChest.plugin, "GMCItem");

  /**
   * Define if the GMC NBT TAG is set on this item
   *
   * @param itemStack Item to analyze
   * @return Return if the item as gmc nbt tag
   */
  @Override
  public boolean hasTag(ItemStack itemStack) {
    if (itemStack == null)
      return false;
    PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
    return container.has(this.gmcTagNamespaceKey, PersistentDataType.BYTE);
  }

  /**
   * Set the custom GMC NBT TAG on item in parameter
   *
   * @param itemStack Item on which add NBT TAG
   * @return ItemStack modified
   */
  @Override
  public ItemStack setTag(ItemStack itemStack) {
    if (itemStack == null || hasTag(itemStack))
      return itemStack;

    ItemMeta meta = itemStack.getItemMeta();

    PersistentDataContainer container = meta.getPersistentDataContainer();
    container.set(this.gmcTagNamespaceKey, PersistentDataType.BYTE, (byte) 0x01);

    itemStack.setItemMeta(meta);

    return itemStack;
  }

}
