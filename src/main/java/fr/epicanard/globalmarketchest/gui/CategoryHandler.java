package fr.epicanard.globalmarketchest.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;

/**
 * Handlle all categories specified in the file 'categories.yml'
 */
public class CategoryHandler {
  private YamlConfiguration config;
  private Set<String> categories;

  public CategoryHandler(YamlConfiguration conf) {
    this.config = conf;
    this.categories = this.config.getKeys(false)
      .stream()
      .filter(cat -> this.config.getBoolean(cat + ".IsActive", true))
      .collect(Collectors.toSet());
  }

  /**
   * Add all items of one category inside the list
   *
   * @param lst The list where add items
   * @param category Category name
   */
  private void addListItems(List<String> lst, String category) {
    final List<String> items = this.config.getStringList(category + ".Items");
    if (items != null)
      lst.addAll(items);
  }

  /**
   * Get the categories name ignoring uncategorized category ('!')
   *
   * @return Return a set of category name ignoring '!'
   */
  public Set<String> getCategories() {
    return this.categories.stream().filter(cat -> !cat.equals("!")).collect(Collectors.toSet());
  }

  /**
   * Get all the items of one category (or all item if category is '!')
   *
   * @param category Category name
   * @return Return an array of item name (ex: minecraft:grass)
   */
  public String[] getItems(String category) {
    final List<String> lst = new ArrayList<String>();

    // When we want items from a specific category
    if (!category.equals("!")) {
      this.addListItems(lst, category);
      return (lst != null) ? lst.toArray(new String[0]) : null;
    }

    // When we want to get uncategorized items
    for (String cat : this.getCategories()) {
      this.addListItems(lst, cat);
    }
    return lst.toArray(new String[0]);
  }

  /**
   * Get the category display name that should be displayed
   *
   * @param category Category name
   * @return Return the category display name
   */
  public String getDisplayName(String category) {
    return this.config.getString(category + ".DisplayName", "");
  }

  /**
   * Get the item that should be displayed inside inventory
   *
   * @param category Category name
   * @return Return the DisplayItem as ItemStack if DisplayItem is not set return minecraft:barrier
   */
  public ItemStack getDisplayItem(String category) {
    String item = this.config.getString(category + ".DisplayItem");
    if (item == null || ItemStackUtils.getItemStack(item) == null)
      item = "minecraft:barrier";

    ItemStack it = ItemStackUtils.getItemStack(item);
    ItemStackUtils.setItemStackMeta(it, "&f" + this.getDisplayName(category), null);
    return it;
  }

  /**
   * Get the position where should be placed the category inside inventory
   *
   * @param category Category name
   * @return Return the category position or -1 if not position is not set
   */
  public Integer getPosition(String category) {
    Integer pos = this.config.getInt(category + ".Position", -1);
    if (pos < 0 || pos > 53) {
      LoggerUtils.warn(String.format("The position of category '%s' is not set or is not between 0 and 53 included", category));
      LoggerUtils.info(String.format("Please set variable '%s.Position' inside file 'categories.yml'", category));
      return 31;
    }
    return pos;
  }

  /**
   * Get the group levels of this category
   *
   * @param category Category name
   * @return Return the group levels
   */
  public Integer getGroupLevels(String category) {
    Integer levels = this.config.getInt(category + ".GroupLevels", 3);
    if (levels < 0 || levels > 3)
      return 3;
    return levels;
  }

  /**
   * Get the category name from an itemstack
   *
   * @param item ItemStack to define his category
   * @return Category name
   */
  public String getCategory(ItemStack item) {
    String mk = ItemStackUtils.getMinecraftKey(item);

    return this.getCategory(mk);
  }

  /**
   * Get the category name from a minecraft key
   *
   * @param minecraftKey MinecraftKey to search inside categories
   * @return Category name
   */
  public String getCategory(String minecraftKey) {
    for (String category : this.getCategories()) {
      if (Arrays.asList(this.getItems(category)).contains(minecraftKey))
        return category;
    }
    return "!";
  }

  /**
   * Get the displayName of the category corresponding to minecraftkey
   * Shortcut for `this.getDisplayName(this.getCategory(minecraftKey))`
   *
   * @param minecraftKey MinecraftKey to search inside categories
   * @return Display name
   */
  public String getDisplayCategory(String minecraftKey) {
    return "&9" + this.getDisplayName(this.getCategory(minecraftKey));
  }

  /**
   * Get the displayName of the category corresponding to item
   * Shortcut for `this.getDisplayName(this.getCategory(minecraftKey))`
   *
   * @param item ItemStack to define his category
   * @return Display name
   */
  public String getDisplayCategory(ItemStack item) {
    return "&9" + this.getDisplayName(this.getCategory(item));
  }
}
