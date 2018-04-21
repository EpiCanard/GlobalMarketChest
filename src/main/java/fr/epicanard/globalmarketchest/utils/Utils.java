package fr.epicanard.globalmarketchest.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.configuration.ConfigLoader;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;

public class Utils {
  private static Utils INSTANCE;
  public final ItemStack background;
  
  private Utils() {
    this.background = getItemStack(
        GlobalMarketChest.plugin.getConfigLoader().getConfig().getString("Interfaces.Background"));
    setItemStackMeta(background, null);
  }
  
  public static Utils getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Utils();
    return INSTANCE;
  }

  public String toColor(String toChange) {
    return toChange.replaceAll("&", "§");
  }

  public int toPos(int x, int y) {
    return y * 9 + x;
  }

  public ItemStack getItemStack(String name) {
    if (name == null)
      return null;

    String[] spec = name.split("/");
    MinecraftKey mk = new MinecraftKey(spec[0]);
    if (Item.REGISTRY.get(mk) == null)
      return null;
    ItemStack item = CraftItemStack.asNewCraftStack(Item.REGISTRY.get(mk));
    if (spec.length > 1)
      item.setDurability(Short.parseShort(spec[1]));
    return item;
  }

  private ItemStack setItemMeta(ItemStack item, String displayName, List<String> lore) {
    if (item == null)
      return null;

    ItemMeta met = item.getItemMeta();
    met.setDisplayName((displayName == null) ? " " : toColor(displayName));
    if (lore != null) {
      lore = lore.stream().map(element -> toColor(element)).collect(Collectors.toList());
      met.setLore(lore);
    }
    item.setItemMeta(met);
    return item;
  }

  public ItemStack setItemStackMeta(ItemStack item, String displayName) {
    return setItemMeta(item, displayName, null);
  }

  public ItemStack setItemStackMeta(ItemStack item, String displayName, List<String> lore) {
    return setItemMeta(item, displayName, lore);
  }

  public ItemStack setItemStackMeta(ItemStack item, String displayName, String lore) {
    if (lore == null)
      return setItemMeta(item, displayName, null);
    return setItemMeta(item, displayName, Arrays.asList(lore.split(";")));
  }

  public ItemStack setItemStackMeta(ItemStack item, String displayName, String[] lore) {
    if (lore == null)
      return setItemMeta(item, displayName, null);
    return setItemMeta(item, displayName, Arrays.asList(lore));
  }

  public ItemStack getButton(String buttonName) {
    ConfigLoader loader = GlobalMarketChest.plugin.getConfigLoader();
    String item;
    ItemStack itemStack = background;

    if (buttonName != null) {
      item = loader.getConfig().getString("Interfaces.Buttons." + buttonName);
      itemStack = getItemStack(item);
      ConfigurationSection sec = loader.getLanguages().getConfigurationSection("Buttons." + buttonName);
      if (sec != null) {
        Map<String, Object> tmp = sec.getValues(false);
        setItemStackMeta(itemStack, (String) tmp.get("Name"), (String) tmp.get("Description"));
      }
    }
    return itemStack;
  }
}
