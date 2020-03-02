package fr.epicanard.globalmarketchest.managers;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import fr.epicanard.globalmarketchest.utils.*;
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
  public Boolean createAuction(final AuctionInfo auction, final Integer repeat, final Integer expirationDays) {
    final InsertBuilder builder = new InsertBuilder(DatabaseConnector.tableAuctions);
    final Timestamp ts = DatabaseUtils.getTimestamp();

    for (int i = 0; i < repeat; i++) {
      builder.addValue("itemStack", auction.getItemStack());
      builder.addValue("itemMeta", auction.getItemMeta());
      builder.addValue("amount", auction.getAmount());
      builder.addValue("price", auction.getPrice());
      builder.addValue("status", StatusAuction.IN_PROGRESS.getValue());
      builder.addValue("type", auction.getType().getType());
      builder.addValue("playerStarter", auction.getPlayerStarter());
      builder.addValue("start", ts.toString());
      builder.addValue("end", DatabaseUtils.addDays(ts, expirationDays).toString());
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
  public Boolean buyAuction(final int id, final Player buyer) {
    final QueryExecutor executor = QueryExecutor.of();

    if (!this.canEditAuction(executor, id))
      return false;

    final UpdateBuilder builder = new UpdateBuilder(DatabaseConnector.tableAuctions);

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
   * @param state State of current auction
   * @param auctions List of auctions to renew
   * @param expirationDays Number of days to renew
   */
  public Boolean renewGroupOfPlayerAuctions(
      final Player player,
      final String group,
      final StatusAuction state,
      final List<Integer> auctions,
      final Integer expirationDays
  ) {
    if (state == StatusAuction.FINISHED || state == StatusAuction.ABANDONED)
      return false;

    final UpdateBuilder builder = this.updateToNow(null, expirationDays);

    this.defineStateCondition(builder, state);
    builder.addCondition("playerStarter", PlayerUtils.getUUIDToString(player));
    builder.addCondition("group", group);
    builder.addCondition("id", auctions, ConditionType.IN);
    return QueryExecutor.of().execute(builder);
  }

  /**
   * Renew a specific auction
   *
   * @param id Id of the auction to renew
   * @param expirationDays Number of days to renew
   */
  public Boolean renewAuction(final int id, final Integer expirationDays) {
    final UpdateBuilder builder = this.updateToNow(null, expirationDays);
    final QueryExecutor executor = QueryExecutor.of();

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
   * @param auctions List of auctions to undo
   */
  public Boolean undoGroupOfPlayerAuctions(final Player player, final String group, final List<Integer> auctions) {
    final UpdateBuilder builder = new UpdateBuilder(DatabaseConnector.tableAuctions);
    final String playerUuid = PlayerUtils.getUUIDToString(player);

    builder.addCondition("id", auctions, ConditionType.IN);
    builder.addCondition("playerStarter", playerUuid);
    builder.addCondition("group", group);
    builder.addValue("status", StatusAuction.ABANDONED.getValue());
    builder.addValue("playerEnder", playerUuid);
    builder.addValue("end", DatabaseUtils.getTimestamp().toString());
    return QueryExecutor.of().execute(builder);
  }

  /**
   * Undo auction
   *
   * @param id Id of the auction to undo
   * @param playerUuid Uuid of player
   */
  public Boolean undoAuction(final int id, final String playerUuid) {
    final UpdateBuilder builder = new UpdateBuilder(DatabaseConnector.tableAuctions);
    final QueryExecutor executor = QueryExecutor.of();

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
   */
  public void purgeAuctions() {
    DeleteBuilder builder = new DeleteBuilder(DatabaseConnector.tableAuctions);
    int purge = ConfigUtils.getInt("Options.PurgeInterval");
    if (purge < 0)
      return;
    builder.addCondition("end", DatabaseUtils.addDays(DatabaseUtils.getTimestamp(), purge * -1), ConditionType.INFERIOR_EQUAL);

    QueryExecutor.of().execute(builder);
  }

  /*
   * =====================
   *     UPDATE AUCTIONS
   * =====================
   */

  /**
   * Update metadatas a group of auctions
   *
   * @param auctions Auctions to update
   */
  public void updateGroupOfAuctionsMetadata(final List<AuctionInfo> auctions) {
    final UpdateBuilder builder = new UpdateBuilder(DatabaseConnector.tableAuctions);

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
  public void getAuctions(
      final GroupLevels level,
      final String group,
      final String category,
      final AuctionInfo auction,
      final Pair<Integer, Integer> limit,
      final Consumer<List<Pair<ItemStack, AuctionInfo>>> consumer
  ) {
    final SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

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
  public void getAuctions(
      final String group,
      final StatusAuction state,
      final OfflinePlayer starter,
      final OfflinePlayer ender,
      final Pair<Integer, Integer> limit,
      final Consumer<List<AuctionInfo>> consumer
  ) {
    final SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

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
  public void getAuctionsByItemName(
      final String group,
      final String search,
      final Pair<Integer, Integer> limit,
      final Consumer<List<AuctionInfo>> consumer
  ) {
    final SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

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
  public void getAuctionNumber(final String group, final Player starter, final Consumer<Integer> consumer) {
    final SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

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
  public void getLastPrice(final AuctionInfo auction, final Consumer<Double> consumer) {
    final SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

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
  public void getAllAuctions(final Boolean all, final Consumer<List<AuctionInfo>> consumer) {
    final SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

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

  /**
   * Count sold auctions since last player connection
   *
   * @param starter Owner of auctions
   * @param consumer Consumer called when request is finished
   */
  public void countSoldAuctions(final OfflinePlayer starter, final Consumer<Integer> consumer) {
    final SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);
    final Timestamp end = new Timestamp(starter.getLastPlayed());

    builder.addField("COUNT(id) AS count");
    this.defineStateCondition(builder, StatusAuction.FINISHED);
    builder.addCondition("playerStarter", PlayerUtils.getUUIDToString(starter));
    builder.addCondition("end", end, ConditionType.SUPERIOR_EQUAL);

    QueryExecutor.of().execute(builder, res -> {
      try {
        if (res.next())
          consumer.accept(res.getInt("count"));
      } catch (SQLException e) {}
    });
  }

  /**
   * Get auctions of last n hours
   */
  public void getLastAuctions(
      final String group,
      final Integer hours,
      final Pair<Integer, Integer> limit,
      final Consumer<List<AuctionInfo>> consumer) {
    final SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

    builder.addCondition("group", group);
    this.defineStateCondition(builder, StatusAuction.IN_PROGRESS);
    builder.setExtension("ORDER BY start DESC");
    builder.addCondition("start", DatabaseUtils.minusHours(DatabaseUtils.getTimestamp(), hours), ConditionType.SUPERIOR_EQUAL);

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
   * Get average price of one specific item from last n days
   *
   * @param auction Auction info of specific item
   * @param days Number of days to analyze
   * @param defaultPrice Default price to set if no price found
   * @param consumer Consumer to send result
   */
  public void getAveragePriceItem(
      final AuctionInfo auction,
      final Integer days,
      final Double defaultPrice,
      final Consumer<Double> consumer) {
    final SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableAuctions);

    builder.addField("AVG(price) as averagePrice");
    builder.addField("COUNT(id) as count");
    builder.addCondition("group", auction.getGroup());
    builder.addCondition("itemStack", auction.getItemStack());
    builder.addCondition("itemMeta", auction.getItemMeta());
    builder.addCondition("status", StatusAuction.ABANDONED.getValue(), ConditionType.NOTEQUAL);
    builder.addCondition("start", DatabaseUtils.minusDays(DatabaseUtils.getTimestamp(), days), ConditionType.SUPERIOR_EQUAL);
    QueryExecutor.of().execute(builder, res -> {
      try {
        if (res.next()) {
          final double price = (res.getInt("count") > 0) ? res.getDouble("averagePrice") : defaultPrice;
          consumer.accept(EconomyUtils.roundValue(price));
        }
      } catch(SQLException e) {}
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
   * @param baseBuilder Base builder
   * @param expirationDays Days of expiration for the auction
   * @return Return same builder
   */
  private UpdateBuilder updateToNow(final UpdateBuilder baseBuilder, final Integer expirationDays) {
    final Timestamp ts = DatabaseUtils.getTimestamp();
    final UpdateBuilder builder = (baseBuilder == null) ? new UpdateBuilder(DatabaseConnector.tableAuctions) : baseBuilder;

    builder.addValue("start", ts.toString());
    builder.addValue("end", DatabaseUtils.addDays(ts, expirationDays).toString());
    builder.addValue("status", StatusAuction.IN_PROGRESS.getValue());

    return builder;
  }

  /**
   * Define from a StateAuction the condition to apply
   *
   * @param builder builder on which set condition
   * @param state StateAuction to convert into condition
   */
  private void defineStateCondition(final ConditionBase builder, final StatusAuction state) {
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
  private Boolean canEditAuction(final QueryExecutor executor, final int id) {
    final SelectBuilder select = new SelectBuilder(DatabaseConnector.tableAuctions);
    final AtomicBoolean end = new AtomicBoolean(true);

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
