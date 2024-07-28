package fr.epicanard.globalmarketchest.nms;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Nms {
  public static String GMC_ITEM_TAG = "GMCItem";

  public String getMinecraftKey(ItemStack item);

  public void updateInventoryName(Player player, String name);

  public ItemStack setNbtTag(ItemStack item);

  public Boolean hasNbtTag(ItemStack item);

  public ItemStack getItemStack(String minecraftKey); // = FromMinecraftKey

  public String getItemStackDisplayName(ItemStack item);
}
