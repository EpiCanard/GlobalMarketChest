package fr.epicanard.globalmarketchest.managers;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.StatusAuction;
import fr.epicanard.globalmarketchest.database.connectors.DatabaseConnector;
import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryExecutor;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.ConditionBase;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.DeleteBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.InsertBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.SelectBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.UpdateBuilder;
import fr.epicanard.globalmarketchest.exceptions.EmptyCategoryException;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

/**
 * Class that handle all auctions and communication with database
 */
public class AuctionManager {

  /**
   * Create an auction inside database
   *
   * @param auction Auction to create in database
   * @param repeat Number of time the auction must repeated
   *
   * @return Return if execution succeed
   */
  public Boolean createAuction(AuctionInfo auction, Integer repeat) {
    InsertBuilder builder = new InsertBuilder(DatabaseConnector.tableAuctions);
    Timestamp ts = DatabaseUtils.getTimestamp();
    int days = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Options.NumberDaysExpiration", 7);
    String[] stringTs = {
      ts.toString(),
      DatabaseUtils.addDays(ts, days).toString()
    };

    for (int i = 0; i < repeat; i++) {
      builder.addValue("itemStack", auction.getItemStack());
      builder.addValue("itemMeta", auction.getItemMeta());
      builder.addValue("amount", auction.getAmount());
      builder.addValue("price", auction.getPrice());
      builder.addValue("status", StatusAuction.IN_PROGRESS.getValue());
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
    QueryExecutor executor = QueryExecutor.of();

    if (!this.canEditAuction(executor, id))
      return false;

    UpdateBuilder builder = new UpdateBuilder(DatabaseConnector.tableAuctions);

    builder.addValue("playerEnder", PlayerUtils.getUUIDToString(buyer));
    builder.addValue("end", DatabaseUtils.getTimestamp().toString());
    builder.addValue("status", StatusAuction.FINISHED.getValue());
    builder.addCondition("id", id);

    return executor.execute(builder);
  }

  /*
   * ==============
   *     RENEW
   * ==============
   */

  /**
   * Renew a group of player auctions expired or in progress
   *
   * @param player Player target by renew of auctions
   * @param group Shop group name target
   */
  public Boolean renewGroupOfPlayerAuctions(Player player, String group, StatusAuction state, List<Integer> auctions) {
    UpdateBuilder builder = this.updateToNow(null);

    if (state == StatusAuction.FINISHED || state == StatusAuction.ABANDONED)
      return false;
    String playeruuid = PlayerUtils.getUUIDToString(player);
    this.defineStateCondition(builder, state);
    builder.addCondition("playerStarter", playeruuid);
    builder.addCondition("group", group);
    builder.addCondition("id", auctions, ConditionType.IN);
    return QueryExecutor.of().execute(builder);
  }

  /**
   * Renew a specific auction
   *
   * @param id Id of the auction to renew
   */
  public Boolean renewAuction(int id) {
    UpdateBuilder builder = this.updateToNow(null);
    QueryExecutor executor = QueryExecutor.of();

    if (!this.canEditAuction(executor, id))
      return false;
    builder.addCondition("id", id);
    builder.addCondition("status", StatusAuction.IN_PROGRESS.getValue());
    return executor.execute(builder);
  }

  /*
   * ==============
   *      UNDO
   * ==============
   */

  /**
   * Undo a group of auctions
   *
   * @param player Player target by remove of auctions
   * @param group Shop group name target
   */
  public Boolean undoGroupOfPlayerAuctions(Player player, String group, List<Integer> auctions) {
    UpdateBuilder builder = new UpdateBuilder(DatabaseConnector.tableAuctions);
    String playeruuid = PlayerUtils.getUUIDToString(player);

    builder.addCondition("id", auctions, ConditionType.IN);
    builder.addCondition("playerStarter", playeruuid);
    builder.addCondition("group", group);
    builder.addValue("status", StatusAuction.ABANDONED.getValue());
    builder.addValue("playerEnder", playeruuid);
    builder.addValue("end", DatabaseUtils.getTimestamp().toString());
    return QueryExecutor.of().execute(builder);
  }

  /**
   * Undo auction
   *
   * @param id Id of the auction to undo
   * @param playerUuid Uuid of player
   */
  public Boolean undoAuction(int id, String playerUuid) {
    UpdateBuilder builder = new UpdateBuilder(DatabaseConnector.tableAuctions);
    QueryExecutor executor = QueryExecutor.of();

    if (!this.canEditAuction(executor, id))
      return false;

    builder.addCondition("id", id);
    builder.addValue("status", StatusAuction.ABANDONED.getValue());
    builder.addValue("playerEnder", playerUuid);
    builder.addValue("end", DatabaseUtils.getTimestamp().toString());
    return executor.execute(builder);
  }

  /**
   * TODO
   * Remove every auction before a specific date
   *
   * @param useConfig Define if remove all or with the date in config file
   */
  public void purgeAuctions() {
    DeleteBuilder builder = new DeleteBuilder(DatabaseConnector.tableAuctions);
    Integer purge = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Options.PurgeInterval");
    if (purge < 0)
      return;
    builder.addCondition("end", DatabaseUtils.addDays(DatabaseUtils.getTimestamp(), purge * -1), ConditionType.INFERIOR_EQUAL);

    QueryExecutor.of().execute(builder);
  }

  /**
   * =====================
   *     UPDATE AUCTIONS
   * =====================
   */

  public void updateGroupOfAuctionsMetadata(List<AuctionInfo> auctions) {
    UpdateBuilder builder = new UpdateBuilder(DatabaseConnector.tableAuctions);

    auctions.forEach(auction -> {
      builder.resetConditions();
      builder.resetValues();
      builder.addCondition("id", auction.getId());
      builder.addValue("itemMeta", auction.getItemMeta());

      QueryExecutor.of().execute(builder);
    });
  }

  /*
   * =====================
   *     LIST AUCTIONS
   * =====================
   */

    /**
     * List auctions depnding of group level
     *
     * @param level GroupLevel to analyse
     * @param group Group of auction
     * @param category Category of items to search
     * @param auction Auction to search
     * @param limit Limit of auctions to get from database
     * @param consumer Callback called when the sql request is executed
     */
  public void getAuctions(GroupLevels level, String group, String category, AuctionInfo auction, Pair<Integer, Integer> limit, Consumer<List<Pair<ItemStack, AuctionInfo>>> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

    builder.addCondition("group", group);
    this.defineStateCondition(builder, StatusAuction.IN_PROGRESS);
    try {
      level.configBuilder(builder, category, auction);
    } catch (EmptyCategoryException e) {
      LoggerUtils.warn(e.getMessage());
      consumer.accept(new ArrayList<>());
      return;
    }

    if (limit != null)
      builder.addExtension(GlobalMarketChest.plugin.getSqlConnector().buildLimit(limit));
    QueryExecutor.of().execute(builder, res -> {
      List<Pair<ItemStack, AuctionInfo>> lst = new ArrayList<>();
      try {
        while (res.next()) {
          ItemStack it;
          if (level == GroupLevels.LEVEL1 && GlobalMarketChest.plugin.getCatHandler().getGroupLevels(category) == 3)
            it = ItemStackUtils.getItemStack(res.getString("itemStack"));
          else
            it = DatabaseUtils.deserialize(res.getString("itemMeta"));

          try {
            ItemStackUtils.setItemStackLore(it, Utils.toList(String.format("%s : %d", LangUtils.get("Divers.AuctionNumber"), res.getInt("count"))));
          } catch (SQLException e) {}
          lst.add(new ImmutablePair<>(it, new AuctionInfo(res)));
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
      consumer.accept(lst);
    });
  }

  /**
   * Get a list of auctions matching with parameters
   *
   * @param group group of auction
   * @param state state of the auction
   * @param starter the player who created the auction
   * @param ender the player who ended the auction
   * @param limit limit to use in request
   * @param consumer callable, send database return to this callabke
   */
  public void getAuctions(String group, StatusAuction state, OfflinePlayer starter, OfflinePlayer ender, Pair<Integer, Integer> limit, Consumer<List<AuctionInfo>> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

    builder.addCondition("group", group);
    this.defineStateCondition(builder, state);
    if (starter != null)
      builder.addCondition("playerStarter", PlayerUtils.getUUIDToString(starter));
    if (ender != null)
      builder.addCondition("playerEnder", PlayerUtils.getUUIDToString(ender));

    if (state == StatusAuction.IN_PROGRESS || state == StatusAuction.EXPIRED)
      builder.setExtension("ORDER BY start DESC");
    else
      builder.setExtension("ORDER BY end DESC");

    if (limit != null)
      builder.addExtension(GlobalMarketChest.plugin.getSqlConnector().buildLimit(limit));
    QueryExecutor.of().execute(builder, res -> {
      List<AuctionInfo> auctions = new ArrayList<>();
      try {
        while (res.next())
          auctions.add(new AuctionInfo(res));
        consumer.accept(auctions);
      } catch (SQLException e) {}
    });
  }

  /**
   * Get all auctions matching minecraft key 'search'
   *
   * @param group group of auction
   * @param search minecraft key searched
   * @param limit limit to use in request
   * @param consumer callable, send database return to this callabke
   */
  public void getAuctionsByItemName(String group, String search, Pair<Integer, Integer> limit, Consumer<List<AuctionInfo>> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

    builder.addCondition("group", group);
    this.defineStateCondition(builder, StatusAuction.IN_PROGRESS);
    builder.setExtension("ORDER BY start DESC");
    builder.addCondition("itemStack", "%:%" + search + "%", ConditionType.LIKE);

    if (limit != null)
      builder.addExtension(GlobalMarketChest.plugin.getSqlConnector().buildLimit(limit));
    QueryExecutor.of().execute(builder, res -> {
      List<AuctionInfo> auctions = new ArrayList<>();
      try {
        while (res.next())
          auctions.add(new AuctionInfo(res));
        consumer.accept(auctions);
      } catch (SQLException e) {}
    });
  }

  /**
   * Get number of auctions for a player
   *
   * @param group group of auction
   * @param starter the player who created the auction
   * @param consumer callable, send database return to this callabke
   */
  public void getAuctionNumber(String group, Player starter, Consumer<Integer> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

    builder.addField("COUNT(id) AS count");
    this.defineStateCondition(builder, StatusAuction.IN_PROGRESS);
    builder.addCondition("group", group);
    builder.addCondition("playerStarter", PlayerUtils.getUUIDToString(starter));

    QueryExecutor.of().execute(builder, res -> {
      try {
        if (res.next())
          consumer.accept(res.getInt("count"));
      } catch (SQLException e) {}
    });
  }

  /**
   * Get the price of the last item matching
   *
   * @param auction AuctionInfo to search last matching auction
   * @param consumer Callback to call if price is found
   */
  public void getLastPrice(AuctionInfo auction, Consumer<Double> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

    builder.addCondition("group", auction.getGroup());
    builder.addCondition("itemStack", auction.getItemStack());
    builder.addCondition("itemMeta", auction.getItemMeta());
    builder.addCondition("playerStarter", auction.getPlayerStarter());
    builder.setExtension("ORDER BY start DESC LIMIT 1");
    QueryExecutor.of().execute(builder, res -> {
      try {
        if (res.next()) {
          consumer.accept(res.getDouble("price"));
        }
      } catch(SQLException e) {}
    });
  }

  /**
   * Get all auctions inside database
   *
   * @param all Define if it get only active auctions (not ended) or all auctions
   * @param consumer Callback to call if price is found
   */
  public void getAllAuctions(Boolean all, Consumer<List<AuctionInfo>> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

    if (!all) {
      builder.addCondition("status", StatusAuction.IN_PROGRESS.getValue());
    }
    builder.setExtension("ORDER BY start DESC");
    QueryExecutor.of().execute(builder, res -> {
      List<AuctionInfo> auctions = new ArrayList<>();
      try {
        while (res.next())
          auctions.add(new AuctionInfo(res));
        consumer.accept(auctions);
      } catch (SQLException e) {}
    });
  }

  /*
   * ================================
   *             TOOLS
   * ================================
   */

  /**
   * Create a querybuilder, update timestamp to now and change state to INPROGRESS
   *
   * @param builder Base builder
   * @return Return same builder
   */
  private UpdateBuilder updateToNow(UpdateBuilder builder) {
    Timestamp ts = DatabaseUtils.getTimestamp();
    if (builder == null)
      builder = new UpdateBuilder(DatabaseConnector.tableAuctions);
    int days = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Options.NumberDaysExpiration", 7);

    builder.addValue("start", ts.toString());
    builder.addValue("end", DatabaseUtils.addDays(ts, days).toString());
    builder.addValue("status", StatusAuction.IN_PROGRESS.getValue());

    return builder;
  }

  /**
   * Define from a StateAuction the condition to apply
   *
   * @param builder builder on which set condition
   * @param state StateAuction to convert into condition
   */
  private void defineStateCondition(ConditionBase builder, StatusAuction state) {
    switch (state) {
      case EXPIRED:
        builder.addCondition("end", DatabaseUtils.getTimestamp(), ConditionType.INFERIOR);
        builder.addCondition("status", StatusAuction.IN_PROGRESS.getValue());
        break;
      case IN_PROGRESS:
        builder.addCondition("end", DatabaseUtils.getTimestamp(), ConditionType.SUPERIOR_EQUAL);
      default:
        builder.addCondition("status", state.getValue());
    }
  }

  /**
   * Request the database to know if the auction specified is ended or not
   *
   * @param executor instance of QueryExecutor to us
   * @param id if the auction to verify
   * @return if auction is ended
   */
  private Boolean canEditAuction(QueryExecutor executor, int id) {
    SelectBuilder select = new SelectBuilder(DatabaseConnector.tableAuctions);
    AtomicBoolean end = new AtomicBoolean(true);

    select.addField("id");
    select.addCondition("id", id);
    select.addCondition("status", StatusAuction.IN_PROGRESS.getValue(), ConditionType.NOTEQUAL);

    executor.execute(select, res -> {
      try {
        if (res != null && res.next())
          end.set(false);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
    return end.get();
  }

}
