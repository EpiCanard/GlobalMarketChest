package fr.epicanard.globalmarketchest.utils.reflection.tags;

import fr.epicanard.globalmarketchest.utils.reflection.NMSUtils;
import fr.epicanard.globalmarketchest.utils.reflection.Path;
import org.bukkit.inventory.ItemStack;

import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.invokeMethod;
import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.newInstance;

public class OldTagHandler implements ITagHandler {

  private final String NBTTAG = "GMCItem";

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
    try {
      Object nmsItemStack = NMSUtils.toNmsItemstack(itemStack);

      Object tagCompound = invokeMethod(nmsItemStack, "getTag");

      return tagCompound != null && (Boolean) invokeMethod(tagCompound, "hasKey", this.NBTTAG);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Set the custom GMC NBT TAG on item in parameter
   *
   * @param itemStack Item on which add NBT TAG
   * @return ItemStack modified
   */
  @Override
  public ItemStack setTag(ItemStack itemStack) {
    if (itemStack == null || this.hasTag(itemStack))
      return itemStack;

    try {
      Object nmsItemStack = NMSUtils.toNmsItemstack(itemStack);

      Object tagCompound = invokeMethod(nmsItemStack, "getTag");
      tagCompound = updateTag_old(tagCompound);
      invokeMethod(nmsItemStack, "setTag", tagCompound);

      return NMSUtils.toItemstack(nmsItemStack);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return itemStack;
  }

  private Object updateTag_old(Object tagCompound) {
    try {
      if (tagCompound == null)
        tagCompound = newInstance(Path.MINECRAFT_SERVER.getClass("NBTTagCompound"));
      tagCompound.getClass().getMethod("setBoolean", String.class, boolean.class).invoke(tagCompound, this.NBTTAG, true);
      return tagCompound;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


}
