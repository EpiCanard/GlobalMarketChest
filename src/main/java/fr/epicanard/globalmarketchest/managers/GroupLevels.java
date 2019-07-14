package fr.epicanard.globalmarketchest.managers;

import java.util.Arrays;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.SelectBuilder;
import fr.epicanard.globalmarketchest.exceptions.EmptyCategoryException;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;

public enum GroupLevels {
  LEVEL3(3, null),
  LEVEL2(2, GroupLevels.LEVEL3),
  LEVEL1(1, GroupLevels.LEVEL2);

  private GroupLevels nextLevel;
  private Integer numLayers;

  GroupLevels(Integer layers, GroupLevels next) {
    this.nextLevel = next;
    this.numLayers = layers;
  }

  /**
   * Get the next level
   *
   * @param category Category name
   * @return GroupLevels found or null
   */
  public GroupLevels getNextLevel(String category) {
    Integer numberLevels = GlobalMarketChest.plugin.getCatHandler().getGroupLevels(category);

    return this.getNextLevel(numberLevels);
  }

  /**
   * Get the next level
   *
   * @param numberLevels The number of levels for this category
   * @return GroupLevels found or null
   */
  public GroupLevels getNextLevel(Integer numberLevels) {
    if (this.nextLevel != null && this.numLayers < numberLevels)
      return this.nextLevel;
    return null;
  }

  /**
   * Configure the request depending on level and category groupLevels
   *
   * @param builder SelectBuilder to configure (add field, condition, etc ...)
   * @param category The auctions category
   * @param match ItemStack to match for research
   */
  public void configBuilder(SelectBuilder builder, String category, AuctionInfo match) throws EmptyCategoryException {
    Integer groupLevels = GlobalMarketChest.plugin.getCatHandler().getGroupLevels(category);

    switch (this) {
      case LEVEL1:
        this.level1(builder, groupLevels, match, category);
        break;
      case LEVEL2:
        this.level2(builder, groupLevels, match, category);
        break;
      case LEVEL3:
        this.level3(builder, groupLevels, match, category);
        break;
    }
  }

  /**
   * Configure the request for level 1 (First interface)
   * Configured depending on groupLevels of the category
   * GroupLevels affected : 3, 2 and 1
   *
   * @param builder SelectBuilder to configure (add field, condition, etc ...)
   * @param groupLevels GroupLevels of the category
   * @param category The auctions category
   * @param match Auction to match for research
   * @throws EmptyCategoryException
   */
  private void level1(SelectBuilder builder, Integer groupLevels, AuctionInfo match, String category) throws EmptyCategoryException {
    String[] items = GlobalMarketChest.plugin.getCatHandler().getItems(category);
    if (items.length == 0)
      throw new EmptyCategoryException(category);
    builder.addCondition("itemStack", Arrays.asList(items), (category.equals("!")) ? ConditionType.NOTIN : ConditionType.IN);
    switch (groupLevels) {
      case 3:
        builder.addField("itemStack");
        builder.addField("COUNT(itemStack) AS count");
        builder.setExtension("GROUP BY itemStack");
        break;
      case 2:
        builder.addField("itemMeta");
        builder.addField("COUNT(itemMeta) AS count");
        builder.setExtension("GROUP BY itemMeta");
        break;
      case 1:
        builder.setExtension("ORDER BY price ASC, start ASC");
      default:
        builder.addField("*");
    }
  }

  /**
   * Configure the request for level 2 (Second interface)
   * Configured depending on groupLevels of the category
   * GroupLevels affected : 3 and 2
   *
   * @param builder SelectBuilder to configure (add field, condition, etc ...)
   * @param groupLevels GroupLevels of the category
   * @param category The auctions category
   * @param match Auction to match for research
   */
  private void level2(SelectBuilder builder, Integer groupLevels, AuctionInfo match, String category) {
    switch (groupLevels) {
      case 3:
        builder.addCondition("itemStack", ItemStackUtils.getMinecraftKey(DatabaseUtils.deserialize(match.getItemMeta())));
        builder.addField("itemMeta");
        builder.addField("COUNT(itemMeta) AS count");
        builder.setExtension("GROUP BY itemMeta");
        break;
      case 2:
        builder.addCondition("itemMeta", match.getItemMeta());
        builder.setExtension("ORDER BY price ASC, start ASC");
      default:
        builder.addField("*");
    }
  }

  /**
   * Configure the request for level 1 (Third interface)
   * Configured depending on groupLevels of the category
   * GroupLevels affected : 3
   *
   * @param builder SelectBuilder to configure (add field, condition, etc ...)
   * @param groupLevels GroupLevels of the category
   * @param category The auctions category
   * @param match Auction to match for research
   */
  private void level3(SelectBuilder builder, Integer groupLevels, AuctionInfo match, String category) {
    switch (groupLevels) {
      case 3:
        builder.addCondition("itemMeta", match.getItemMeta());
        builder.setExtension("ORDER BY price ASC, start ASC");
      default:
        builder.addField("*");
    }
  }

}
