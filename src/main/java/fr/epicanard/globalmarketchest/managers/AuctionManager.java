package fr.epicanard.globalmarketchest.managers;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
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
   * @param auction
   * @param repeat
   * 
   * @return Return if execution succeed
   */
  public Boolean createAuction(AuctionInfo auction, Integer repeat) {
    InsertBuilder builder = new InsertBuilder(DatabaseConnection.tableAuctions);
    Timestamp ts = DatabaseUtils.getTimestamp();
    String[] stringTs = {
      ts.toString(),
      DatabaseUtils.addDays(ts, 7).toString()
    };

    for (int i = 0; i < repeat; i++) {
      builder.addValue("itemStack", auction.getItemStack());
      builder.addValue("damage", auction.getDamage());
      builder.addValue("itemMeta", auction.getItemMeta());
      builder.addValue("amount", auction.getAmount());
      builder.addValue("price", auction.getPrice());
      builder.addValue("state", auction.getState().getState());
      builder.addValue("type", auction.getType().getType());
      builder.addValue("playerStarter", auction.getPlayerStarter());
      builder.addValue("start", stringTs[0]);
      builder.addValue("end", stringTs[1]);
      builder.addValue("group", auction.getGroup());
    }
    return QueryExecutor.of().execute(builder);
  }

  /**
   * Update database when player obtain an auction
   * 
   * @param id Id of the auction to update
   * @param buyer Player that vuy the auction
   */
  public Boolean buyAuction(int id, Player buyer) {
    UpdateBuilder builder = new UpdateBuilder(DatabaseConnection.tableAuctions);

    builder.addValue("playerEnder", PlayerUtils.getUUIDToString(buyer));
    builder.addValue("end", DatabaseUtils.getTimestamp().toString());
    builder.addValue("state", StateAuction.FINISHED.getState());
    builder.addCondition("id", id);

    return QueryExecutor.of().execute(builder);
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
  public Boolean renewEveryAuctionOfPlayer(Player player, String group, StateAuction state) {
    UpdateBuilder builder = this.updateToNow(null);

    builder.addCondition("state", state.getState());
    builder.addCondition("playerStarter", PlayerUtils.getUUIDToString(player));
    builder.addCondition("group", group);
    return QueryExecutor.of().execute(builder);
  }

  /**
   * Renew a specific auction
   * 
   * @param id Id of the auction to renew
   */
  public Boolean renewAuction(int id) {
    UpdateBuilder builder = this.updateToNow(null);

    builder.addCondition("id", id);
    return QueryExecutor.of().execute(builder);
  }

  /**
   * Undo a group of auctions
   * 
   * @param player
   * @param group
   */
  public Boolean undoGroupOfPlayerAuctions(Player player, String group, List<Integer> auctions) {
    UpdateBuilder builder = new UpdateBuilder(DatabaseConnection.tableAuctions);

    builder.addCondition("id", auctions, ConditionType.IN);
    builder.addCondition("playerStarter", PlayerUtils.getUUIDToString(player));
    builder.addCondition("group", group);
    builder.addValue("state", StateAuction.ABANDONED.getState());
    builder.addValue("end", DatabaseUtils.getTimestamp().toString());
    return QueryExecutor.of().execute(builder);
  }
  /**
   * Undo auction
   * 
   * @param id Id of the auction to undo
   */
  public Boolean undoAuction(int id) {
    UpdateBuilder builder = new UpdateBuilder(DatabaseConnection.tableAuctions);

    builder.addCondition("id", id);
    builder.addValue("state", StateAuction.ABANDONED.getState());
    builder.addValue("end", DatabaseUtils.getTimestamp().toString());
    return QueryExecutor.of().execute(builder);
  }
  /**
   * Remove every auction before a specific date
   * 
   * @param useConfig Define if remove all or with the date in config file
   */
  public void purgeAuctions() {
    DeleteBuilder builder = new DeleteBuilder(DatabaseConnection.tableAuctions);
    Integer purge = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Auctions.PurgeInterval");
    if (purge < 0)
      return;
    builder.addCondition("end", DatabaseUtils.addDays(DatabaseUtils.getTimestamp(), purge * -1), ConditionType.INFERIOR_EQUAL);

    List<Integer> lst = new ArrayList<>();
    lst.add(StateAuction.ABANDONED.getState());
    lst.add(StateAuction.FINISHED.getState());
    builder.addCondition("state", lst, ConditionType.IN);

    QueryExecutor.of().execute(builder);
  }

  /**
   * Get all item for one category in one group
   * 
   * @param group
   * @param category
   * @param consumer
   */
  public void getItemByCategory(String group, String category, Pair<Integer, Integer> limit, Consumer<List<ItemStack>> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnection.tableAuctions);

    String[] items = GlobalMarketChest.plugin.getCatHandler().getItems(category);

    builder.addCondition("group", group);
    builder.addCondition("itemStack", Arrays.asList(items), ConditionType.IN);
    builder.addCondition("state", StateAuction.INPROGRESS.getState());
    builder.addField("*");
    builder.addField("COUNT(itemStack) AS count");
    builder.setExtension("GROUP BY itemStack, damage");
    if (limit != null)
      builder.addExtension(GlobalMarketChest.plugin.getSqlConnection().buildLimit(limit));
    QueryExecutor.of().execute(builder, res -> {
      List<ItemStack> lst = new ArrayList<>();
      try {
        while (res.next()) {
          ItemStack item = ItemStackUtils.getItemStack(res.getString("itemStack"));
          item.setDurability(res.getShort("damage"));
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
  public void getAuctionsByItem(String group, ItemStack item, Pair<Integer, Integer> limit, Consumer<List<AuctionInfo>> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnection.tableAuctions);

    builder.addCondition("group", group);
    builder.addCondition("itemStack", ItemStackUtils.getMinecraftKey(item));
    builder.addCondition("damage", item.getDurability());
    builder.addCondition("state", StateAuction.INPROGRESS.getState());
    builder.setExtension("ORDER BY price, start ASC");
    if (limit != null)
      builder.addExtension(GlobalMarketChest.plugin.getSqlConnection().buildLimit(limit));
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

  public void getLastPrice(ItemStack item, String group, Player owner) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnection.tableAuctions);

    builder.addCondition("group", group);
    builder.addCondition("itemMeta", ItemStackUtils.getMinecraftKey(item));
    builder.addCondition("playerStarter", PlayerUtils.getUUIDToString(owner));
    builder.setExtension("ORDER BY price, start ASC");
    QueryExecutor.of().execute(builder, res -> {
    });
  }

  public void getAuctions(String group, StateAuction state, Player starter, Player ender, Pair<Integer, Integer> limit, Consumer<List<AuctionInfo>> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnection.tableAuctions);

    builder.addCondition("group", group);
    builder.addCondition("state", state.getState());
    if (starter != null)
      builder.addCondition("playerStarter", PlayerUtils.getUUIDToString(starter));
    if (ender != null)
      builder.addCondition("playerEnder", PlayerUtils.getUUIDToString(ender));
    builder.setExtension("ORDER BY end DESC");
    if (limit != null)
      builder.addExtension(GlobalMarketChest.plugin.getSqlConnection().buildLimit(limit));
    QueryExecutor.of().execute(builder, res -> {
      List<AuctionInfo> auctions = new ArrayList<>();
      try {
        while (res.next())
          auctions.add(new AuctionInfo(res));
        consumer.accept(auctions);
      } catch (SQLException e) {}
    });
    
  }

}
