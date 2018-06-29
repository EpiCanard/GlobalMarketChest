package fr.epicanard.globalmarketchest.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.configuration.ConfigLoader;
import lombok.Getter;
import lombok.experimental.UtilityClass;

/**
 * Global Utility Class
 */
@UtilityClass
public class Utils {
  @Getter
  private ItemStack background = null;

  public void init() {
    Utils.background = ItemStackUtils.getItemStackFromConfig("Interfaces.Background");
    Utils.background = ItemStackUtils.setItemStackMeta(background, null, null);
  }

  /**
   * Change String too support color
   */
  public String toColor(String toChange) {
    return ChatColor.translateAlternateColorCodes('&', toChange);
  }

  /**
   * From position x and y give a position inside the inventory
   * 
   * @param x
   * @param y
   * @return position
   */
  public int toPos(int x, int y) {
    return y * 9 + x;
  }

  /**
   * Get the line number from position
   * 
   * @param pos position
   * @param lineWidth line width if the used zone is smaller then the inventory width
   * @return line number
   */
  public int getLine(int pos, int lineWidth) {
    return (pos - pos % lineWidth) / lineWidth;
  }

  /**
   * Get the column number from position
   * 
   * @param pos position
   * @param lineWidth line width if the used zone is smaller then the inventory width
   * @return column number
   */
  public int getCol(int pos, int lineWidth) {
    return pos % lineWidth;
  }

  /**
   * Get a button from the config file and create itemstack
   * 
   * @param buttonName button name to search inside config
   * @return item created
   */
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
        ItemStackUtils.setItemStackMeta(itemStack, (String) tmp.get("Name"), Utils.toList((String)tmp.get("Description")));
      }
    }
    return itemStack;
  }

  public List<String> toList(String[] lore) {
    return (lore == null) ? null : Arrays.asList(lore);
  }

  public List<String> toList(String lore) {
    return (lore == null) ? null : Arrays.asList(lore.split(";"));
  }

  /**
   * If the index is bigger than size return the size to prevent IndexOutOfBOundException
   * 
   * @return index
   */
  public int getIndex(int index, int size) {
    if (index < 0)
      return 0;
    return (index >= size) ? size : index;
  }
}
