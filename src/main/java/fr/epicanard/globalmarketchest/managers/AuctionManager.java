package fr.epicanard.globalmarketchest.managers;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionType;
import fr.epicanard.globalmarketchest.auctions.StateAuction;
import fr.epicanard.globalmarketchest.database.connections.DatabaseConnection;
import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryExecutor;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.DeleteBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.InsertBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.SelectBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.UpdateBuilder;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

/**
 * Class that handle all auctions and communication with database
 */
public class AuctionManager {
  /**
   * Create an auction inside database
   * 
   * @param itemStack
   * @param itemMeta
   * @param amount
   * @param price
   * @param type
   * @param playerStarter
   * @param group
   */
  public Boolean createAuction(String itemStack, String itemMeta, Integer amount, Double price, AuctionType type, String playerStarter, String group, Integer repeat) {
    InsertBuilder builder = new InsertBuilder(DatabaseConnection.tableAuctions);
    Timestamp ts = DatabaseUtils.getTimestamp();

    for (int i = 0; i < repeat; i++) {
      builder.addValue("itemStack", itemStack);
      builder.addValue("itemMeta", itemMeta);
      builder.addValue("amount", amount);
      builder.addValue("price", price);
      builder.addValue("state", StateAuction.INPROGRESS.getState());
      builder.addValue("type", type.getType());
      builder.addValue("playerStarter", playerStarter);
      builder.addValue("start", ts.toString());
      builder.addValue("end", DatabaseUtils.addDays(ts, 7).toString());
      builder.addValue("group", group);
    }
    System.out.println(builder.build());
    return QueryExecutor.of().execute(builder);
  }

  public Boolean createAuction(AuctionInfo auction, Integer repeat) {
    return this.createAuction(auction.getItemStack(), "", auction.getAmount(), auction.getPrice(), auction.getType(), auction.getPlayerStarter(), auction.getGroup(), repeat);
  }

  /**
   * Update database when player obtain an auction
   * 
   * @param id Id of the auction to update
   * @param buyer Player that vuy the auction
   */
  public void buyAuction(int id, Player buyer) {
    UpdateBuilder builder = new UpdateBuilder(DatabaseConnection.tableAuctions);

    builder.addValue("playerEnder", PlayerUtils.getUUIDToString(buyer));
    builder.addValue("end", DatabaseUtils.getTimestamp().toString());
    builder.addValue("state", StateAuction.FINISHED.getState());
    builder.addCondition("id", id);

    QueryExecutor.of().execute(builder);
  }

  /**
   * Create a querybuilder, update timestamp to now and change state to INPROGRESS
   * 
   * @param builder
   * @return Return same builder
   */
  private UpdateBuilder updateToNow(UpdateBuilder builder) {
    Timestamp ts = DatabaseUtils.getTimestamp();
    if (builder == null)
      builder = new UpdateBuilder(DatabaseConnection.tableAuctions);

    builder.addValue("start", ts.toString());
    builder.addValue("end", DatabaseUtils.addDays(ts, 7).toString());
    builder.addValue("state", StateAuction.INPROGRESS.getState());
        
    return builder;
  }

  /**
   * Renew all player auctions expired
   * 
   * @param player
   * @param group
   */
  public void renewEveryAuctionOfPlayer(Player player, String group) {
    UpdateBuilder builder = this.updateToNow(null);

    builder.addCondition("state", StateAuction.EXPIRED.getState());
    builder.addCondition("owner", PlayerUtils.getUUIDToString(player));
    builder.addCondition("group", group);
    QueryExecutor.of().execute(builder);
  }

  /**
   * Renew a specific auction
   * 
   * @param id Id of the auction to renew
   */
  public void renewAuction(int id) {
    UpdateBuilder builder = this.updateToNow(null);

    builder.addCondition("id", id);
    QueryExecutor.of().execute(builder);
  }

  /**
   * Remove every auction before a specific date
   * 
   * @param useConfig Define if remove all or with the date in config file
   */
  public void purgeAuctions(Boolean useConfig) {
    DeleteBuilder builder = new DeleteBuilder(DatabaseConnection.tableAuctions);
    if  (useConfig) {
      Integer purge = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Auctions.PurgeInterval");
      builder.addCondition("start", DatabaseUtils.addDays(DatabaseUtils.getTimestamp(), purge * -1), ConditionType.INFERIOR_EQUAL);
    }
    QueryExecutor.of().execute(builder);
  }

  /**
   * Get all item for one category in one group
   * 
   * @param group
   * @param category
   * @param consumer
   */
  public void getItemByCategory(String group, String category, Consumer<List<ItemStack>> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnection.tableAuctions);

    String[] items = GlobalMarketChest.plugin.getCatHandler().getItems(category);

    builder.addCondition("group", group);
    builder.addCondition("itemStack", Arrays.asList(items), ConditionType.IN);
    builder.addCondition("state", StateAuction.INPROGRESS.getState());
    builder.addField("*");
    builder.addField("COUNT(itemStack) AS count");
    builder.setExtension(" GROUP BY itemStack");
    QueryExecutor.of().execute(builder, res -> {
      List<ItemStack> lst = new ArrayList<>();
      try {
        while (res.next()) {
          ItemStack item = ItemStackUtils.getItemStack(res.getString("itemStack"));
          ItemStackUtils.setItemStackLore(item, Utils.toList(String.format("%s : %d", LangUtils.get("Divers.AuctionNumber"), res.getInt("count"))));
          lst.add(item);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      consumer.accept(lst);
    });
  }

  /**
   * Get all item for one category in one group
   * 
   * @param group
   * @param category
   * @param consumer
   */
  public void getAuctionsByItem(String group, String item, Consumer<List<AuctionInfo>> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnection.tableAuctions);

    builder.addCondition("group", group);
    builder.addCondition("itemStack", item);
    builder.addCondition("state", StateAuction.INPROGRESS.getState());
    builder.setExtension(" ORDER BY price, start ASC");
    QueryExecutor.of().execute(builder, res -> {
      List<AuctionInfo> lst = new ArrayList<>();
      try {
        while (res.next()) {
          lst.add(new AuctionInfo(res));
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      consumer.accept(lst);
    });
  }

}
