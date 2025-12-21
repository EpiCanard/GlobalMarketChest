package fr.epicanard.globalmarketchest.utils.reflection;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.MinecraftVersion;
import fr.epicanard.globalmarketchest.utils.annotations.Version;
import fr.epicanard.globalmarketchest.utils.reflection.inventory.IInventoryTitleUpdater;
import fr.epicanard.globalmarketchest.utils.reflection.inventory.InventoryTitleUpdater;
import fr.epicanard.globalmarketchest.utils.reflection.inventory.NMSInventoryTitleUpdater;
import fr.epicanard.globalmarketchest.utils.reflection.minecraftkey.IMinecraftKeyHandler;
import fr.epicanard.globalmarketchest.utils.reflection.minecraftkey.MinecraftKeyHandler;
import fr.epicanard.globalmarketchest.utils.reflection.minecraftkey.NMSMinecraftKeyHandler;
import fr.epicanard.globalmarketchest.utils.reflection.tags.ITagHandler;
import fr.epicanard.globalmarketchest.utils.reflection.tags.OldTagHandler;
import fr.epicanard.globalmarketchest.utils.reflection.tags.TagHandler;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;

import static fr.epicanard.globalmarketchest.utils.annotations.AnnotationCaller.call;
import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.*;

public class VersionSupportUtils extends RegistryProvider implements ITagHandler, IMinecraftKeyHandler, IInventoryTitleUpdater {

  private static VersionSupportUtils INSTANCE;

  private ITagHandler tagHandler;
  private IMinecraftKeyHandler minecraftKeyHandler;
  private IInventoryTitleUpdater inventoryTitleUpdater;

  /**
   * Private constructor VersionSupportUtils
   */
  private VersionSupportUtils(ITagHandler tagHandler, IMinecraftKeyHandler minecraftKeyHandler, IInventoryTitleUpdater inventoryTitleUpdater) {
    this.tagHandler = tagHandler;
    this.minecraftKeyHandler = minecraftKeyHandler;
    this.inventoryTitleUpdater = inventoryTitleUpdater;
  }

  /**
   * Singleton, method to get the instance of this class
   */
  public static VersionSupportUtils getInstance() {
    if (INSTANCE == null) {
      final MinecraftVersion version = GlobalMarketChest.plugin.getMinecraftVersion();

      final ITagHandler tagHandler = (version.isLowerOrEqualsTo(1, 13)) ? new OldTagHandler() : new TagHandler();
      final IMinecraftKeyHandler minecraftKeyHandler = (version.isLowerThan(1, 13)) ?  new NMSMinecraftKeyHandler() : new MinecraftKeyHandler();
      final IInventoryTitleUpdater inventoryTitleUpdater = (version.isLowerThan(1, 20)) ?  new NMSInventoryTitleUpdater() : new InventoryTitleUpdater();

      INSTANCE = new VersionSupportUtils(tagHandler, minecraftKeyHandler, inventoryTitleUpdater);
    }
    return INSTANCE;
  }

  // ======== TOOLS ============

  @Version(name = "newMinecraftKey", versions = { "1.12", "1.13", "1.14", "1.15", "1.16" })
  public Object newMinecraftKey_old(String name) throws Exception {
    return Path.MINECRAFT_SERVER.getClass("MinecraftKey").getConstructor(String.class).newInstance(name);
  }

  @Version(name = "newMinecraftKey", versions = { "1.17", "1.18", "1.19", "1.20" })
  public Object newMinecraftKey_1_17(String name) throws Exception {
    return Path.MINECRAFT_RESOURCES.getClass("MinecraftKey").getConstructor(String.class).newInstance(name);
  }

  @Version(name = "newMinecraftKey")
  public Object newMinecraftKey_latest(String name) throws Exception {
    return Path.MINECRAFT_RESOURCES.getClass("MinecraftKey").getMethod("a", String.class).invoke(null, name);
  }

  // ======= SPECIFIC METHOD ===========

  /**
   * Get an itemStack with is minecraft key
   *
   * @param name
   * @return
   */
  private ItemStack getItemStackFromBukkit(String name) {
    try {
      Object minecraftKey = call("newMinecraftKey", this, name);;

      Object registry = getRegistry();
      Object item = call("getRegistryItem", this, registry, minecraftKey);
      if (item == null)
        return null;

      Class<?> itemCLass = call("getItemClass", this);

      Method asNewCraftStack = Path.BUKKIT.getClass("inventory.CraftItemStack")
          .getDeclaredMethod("asNewCraftStack", itemCLass);
      ItemStack itemStack = (ItemStack) asNewCraftStack.invoke(null, item);

      return this.setTag(itemStack);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public ItemStack getItemStack(String name) {
    if (name.startsWith("minecraft:") && GlobalMarketChest.plugin.getMinecraftVersion().isHigherThan(1, 13)) {
      String key = name.substring(10);
      Material material = Registry.MATERIAL.get(NamespacedKey.minecraft(key));
      return this.setTag(new ItemStack(material, 1));
    } else {
      return getItemStackFromBukkit(name);
    }
  }


  /**
   * Get the display name of an itemstack
   *
   * @param itemStack ItemStack
   * @return return the displayname
   */
  public String getItemStackDisplayName(ItemStack itemStack) {
    try {
      Method asNMSCopy = Path.BUKKIT.getClass("inventory.CraftItemStack")
          .getDeclaredMethod("asNMSCopy", ItemStack.class);
      Object nmsItemStack = asNMSCopy.invoke(null, itemStack);
      Object name = call("getName", this, nmsItemStack);

      if (name == null)
        return itemStack.getType().name();
      if (name instanceof String)
        return (String) name;
      return invokeMethod(name, "getString").toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean hasTag(ItemStack itemStack) {
    return this.tagHandler.hasTag(itemStack);
  }

  @Override
  public ItemStack setTag(ItemStack itemStack) {
    return this.tagHandler.setTag(itemStack);
  }

  @Override
  public String getMinecraftKey(ItemStack itemStack) {
    return this.minecraftKeyHandler.getMinecraftKey(itemStack);
  }

  @Override
  public void updateInventoryName(Player player, String title) {
    this.inventoryTitleUpdater.updateInventoryName(player, title);
  }

}
