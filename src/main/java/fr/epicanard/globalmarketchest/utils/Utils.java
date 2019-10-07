package fr.epicanard.globalmarketchest.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
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

  /**
   * Version of minecraft server
   * It get only the major version (ex: version 1.13.2 will get 1.13)
   */
  @Getter
  private final String version;
  /**
   * Last Support Version of minecraft for current plugin
   * Prevent loading config issues for versions not fully supported by the plugin
   */
  @Getter
  private final String lastSupportedVersion = "1.14";

  static {
    version = GlobalMarketChest.plugin.getServer().getBukkitVersion().substring(0, 4);
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
    if (buttonName == null)
      return null;

    final ConfigLoader loader = GlobalMarketChest.plugin.getConfigLoader();
    final String item = loader.getConfig().getString("Interfaces.Buttons." + buttonName);
    final ItemStack itemStack = ItemStackUtils.getItemStack(item);
    final ConfigurationSection sec = loader.getLanguages().getConfigurationSection("Buttons." + buttonName);

    if (sec != null) {
      Map<String, Object> tmp = sec.getValues(false);
      ItemStackUtils.setItemStackMeta(itemStack, (String) tmp.get("Name"), Utils.toList((String)tmp.get("Description")));
    }
    return itemStack;
  }

  /**
   * Convert a String[] into List<String>
   *
   * @param lore Array to convert
   * @return List converted
   */
  public List<String> toList(String[] lore) {
    return (lore == null) ? null : Arrays.asList(lore);
  }

  /**
   * Convert a String into List, splitting with ;
   *
   * @param lore String to split
   * @return List of element splitted
   */
  public List<String> toList(String lore) {
    return (lore == null) ? null : Arrays.asList(lore.split(";"));
  }

  /**
   * Convert a List into String, concat with ;
   *
   * @param lores List to concat
   * @return String concat
   */
  public String fromList(List<String> lores) {
    return (lores == null) ? null : String.join(";", lores);
  }

  /**
   * Map a function to a list
   *
   * @param lst The list on which apply the function
   * @param fct Function to map
   * @return The new list mapped
   */
  public <T, R> List<T> mapList(List<R> lst, Function<R, T> fct) {
    return lst.stream().map(fct).collect(Collectors.toList());
  }

  /**
   * Execute a filter on a list
   *
   * @param lst The list on which apply the function
   * @param fct Function to filter
   * @return The new list filtered
   */
  public <T> List<T> filter(List<T> lst, Predicate<T> fct) {
    return lst.stream().filter(fct).collect(Collectors.toList());
  }

  /**
   * If the index is bigger than size return the size to prevent IndexOutOfBOundException
   *
   * @param index Index to compare
   * @param size Max size
   * @return index
   */
  public int getIndex(int index, int size, Boolean exclusive) {
    if (index <= 0 || size == 0)
      return 0;
    if (exclusive)
      return (index > size) ? size : index;
    return (index >= size) ? size - 1 : index;
  }

  /**
   * If first param is null return the defaut param
   * 
   * @param obj Param to get
   * @param defaut Param return when obj is null
   * @return Return obj or defaut of obj is null
   */
  public <T> T getOrElse(T obj, T defaut) {
    return Optional.ofNullable(obj).orElse(defaut);
  }

  /**
   * Edit the content of a sign at specific location
   *
   * @param loc Location of sign
   * @param lines Lines to set on the sign
   */
  public void editSign(Location loc, String[] lines) {
    if (loc.getBlock().getState() instanceof Sign) {
      Sign signBlock = (Sign) loc.getBlock().getState();

      for (int i = 0; i < lines.length && i < 4; i++) {
        signBlock.setLine(i, lines[i]);
      }

      signBlock.update();
    }
  }
}
