package fr.epicanard.globalmarketchest.utils;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.configuration.ConfigLoader;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {
  private ItemStack background = null;

  public String toColor(String toChange) {
    return ChatColor.translateAlternateColorCodes('&', toChange);
  }

  public ItemStack getBackground() {
    if (Utils.background == null) {
      Utils.background = ItemStackUtils.getItemStack(
        GlobalMarketChest.plugin.getConfigLoader().getConfig().getString("Interfaces.Background"));
        ItemStackUtils.setItemStackMeta(background, null);
    }
    return Utils.background;
  }

  public int toPos(int x, int y) {
    return y * 9 + x;
  }

  public int getLine(int pos, int lineWidth) {
    return (pos - pos % lineWidth) / lineWidth;
  }

  public int getCol(int pos, int lineWidth) {
    return pos % lineWidth;
  }

  public ItemStack getButton(String buttonName) {
    ConfigLoader loader = GlobalMarketChest.plugin.getConfigLoader();
    String item;
    ItemStack itemStack = Utils.getBackground();

    if (buttonName != null) {
      item = loader.getConfig().getString("Interfaces.Buttons." + buttonName);
      itemStack = ItemStackUtils.getItemStack(item);
      ConfigurationSection sec = loader.getLanguages().getConfigurationSection("Buttons." + buttonName);
      if (sec != null) {
        Map<String, Object> tmp = sec.getValues(false);
        ItemStackUtils.setItemStackMeta(itemStack, (String) tmp.get("Name"), (String) tmp.get("Description"));
      }
    }
    return itemStack;
  }
}
