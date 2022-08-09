package fr.epicanard.globalmarketchest.managers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.StatusAuction;
import fr.epicanard.globalmarketchest.database.DatabaseManager;
import fr.epicanard.globalmarketchest.database.connectors.DatabaseConnector;
import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryExecutor;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.*;
import fr.epicanard.globalmarketchest.exceptions.EmptyCategoryException;
import fr.epicanard.globalmarketchest.utils.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Class that handle all auctions and communication with database
 */
public class AuctionManager extends DatabaseManager {

  public AuctionManager() {
    super(DatabaseConnector.tableAuctions);
  }

  /**
   * Create an auction inside database
   *
   * @param auction Auction to create in database
   * @param repeat  Number of time the auction must repeated
   * @return Return if execution succeed
   */
  public Boolean createAuction(final AuctionInfo auction, final Integer repeat, final Integer expirationDays) {
    final InsertBuilder builder = insert();
    final Timestamp ts = DatabaseUtils.getTimestamp();

    for (int i = 0; i < repeat; i++) {
      builder
          .addValue("itemStack", auction.getItemStack())
          .addValue("itemMeta", auction.getItemMeta())
          .addValue("amount", auction.getAmount())
          .addValue("price", auction.getPrice())
          .addValue("status", StatusAuction.IN_PROGRESS.getValue())
          .addValue("type", auction.getType().getType())
          .addValue("playerStarter", auction.getPlayerStarter())
          .addValue("start", ts.toString())
          .addValue("end", DatabaseUtils.addDays(ts, expirationDays).toString())
          .addValue("group", auction.getGroup());
    }
    return QueryExecutor.of().execute(builder);
  }

  /**
   * Update database when player obtain an auction
   *
   * @param id    Id of the auction to update
   * @param buyer Player that vuy the auction
   */
  public Boolean buyAuction(final int id, final Player buyer) {
    final QueryExecutor executor = QueryExecutor.of();

    if (!this.canEditAuction(executor, id))
      return false;

    final UpdateBuilder builder = update()
        .addValue("playerEnder", PlayerUtils.getUUIDToString(buyer))
        .addValue("end", DatabaseUtils.getTimestamp().toString())
        .addValue("status", StatusAuction.FINISHED.getValue())
        .addCondition("id", id);

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
   * @param player         Player target by renew of auctions
   * @param group          Shop group name target
   * @param state          State of current auction
   * @param auctions       List of auctions to renew
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
    builder
        .addCondition("playerStarter", PlayerUtils.getUUIDToString(player))
        .addCondition("group", group)
        .addCondition("id", auctions, ConditionType.IN);
    return QueryExecutor.of().execute(builder);
  }

  /**
   * Renew a specific auction
   *
   * @param id             Id of the auction to renew
   * @param expirationDays Number of days to renew
   */
  public Boolean renewAuction(final int id, final Integer expirationDays) {
    final UpdateBuilder builder = this.updateToNow(null, expirationDays);
    final QueryExecutor executor = QueryExecutor.of();

    if (!this.canEditAuction(executor, id))
      return false;
    builder
        .addCondition("id", id)
        .addCondition("status", StatusAuction.IN_PROGRESS.getValue());
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
   * @param player   Player target by remove of auctions
   * @param group    Shop group name target
   * @param auctions List of auctions to undo
   */
  public Boolean undoGroupOfPlayerAuctions(final Player player, final String group, final List<Integer> auctions) {
    final String playerUuid = PlayerUtils.getUUIDToString(player);

    final UpdateBuilder builder = update()
        .addCondition("id", auctions, ConditionType.IN)
        .addCondition("playerStarter", playerUuid)
        .addCondition("group", group)
        .addValue("status", StatusAuction.ABANDONED.getValue())
        .addValue("playerEnder", playerUuid)
        .addValue("end", DatabaseUtils.getTimestamp().toString());
    return QueryExecutor.of().execute(builder);
  }

  /**
   * Undo auction
   *
   * @param id         Id of the auction to undo
   * @param playerUuid Uuid of player
   */
  public Boolean undoAuction(final int id, final String playerUuid) {
    final QueryExecutor executor = QueryExecutor.of();

    if (!this.canEditAuction(executor, id))
      return false;

    final UpdateBuilder builder = update()
        .addCondition("id", id)
        .addValue("status", StatusAuction.ABANDONED.getValue())
        .addValue("playerEnder", playerUuid)
        .addValue("end", DatabaseUtils.getTimestamp().toString());
    return executor.execute(builder);
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
    final UpdateBuilder builder = update();

    auctions.forEach(auction -> {
      builder
          .resetConditions()
          .resetValues()
          .addCondition("id", auction.getId())
          .addValue("itemMeta", auction.getItemMeta());
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
   * @param level    GroupLevel to analyse
   * @param group    Group of auction
   * @param category Category of items to search
   * @param auction  Auction to search
   * @param limit    Limit of auctions to get from database
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
    final SelectBuilder builder = select()
        .addCondition("group", group);

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
      final List<Pair<ItemStack, AuctionInfo>> lst = new ArrayList<>();
      while (res.next()) {
        ItemStack it;
        if (level == GroupLevels.LEVEL1 && GlobalMarketChest.plugin.getCatHandler().getGroupLevels(category) == 3)
          it = ItemStackUtils.getItemStack(res.getString("itemStack"));
        else
          it = DatabaseUtils.deserialize(res.getString("itemMeta"));

        try {
          ItemStackUtils.setItemStackLore(it, Utils.toList(String.format("%s : %d", LangUtils.get("Divers.AuctionNumber"), res.getInt("count"))));
        } catch (SQLException e) {
          // ignored
        }
        lst.add(new ImmutablePair<>(it, new AuctionInfo(res)));
      }
      consumer.accept(lst);
    }, e -> {
      e.printStackTrace();
      consumer.accept(new ArrayList<>());
    });
  }

  /**
   * Get a list of auctions matching with parameters
   *
   * @param group    group of auction
   * @param state    state of the auction
   * @param starter  the player who created the auction
   * @param ender    the player who ended the auction
   * @param limit    limit to use in request
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
    final SelectBuilder builder = select()
        .addCondition("group", group);

    this.defineStateCondition(builder, state);
    if (starter != null)
      builder.addCondition("playerStarter", PlayerUtils.getUUIDToString(starter));
    if (ender != null)
      builder.addCondition("playerEnder", PlayerUtils.getUUIDToString(ender));

    if (state == StatusAuction.IN_PROGRESS || state == StatusAuction.EXPIRED)
      builder.setExtension("ORDER BY start DESC");
    else
      builder.setExtension("ORDER BY end DESC");

    executeListingAuctions(builder, limit, consumer);
  }

  /**
   * Get all auctions matching minecraft key 'search'
   *
   * @param group    group of auction
   * @param search   minecraft key searched
   * @param limit    limit to use in request
   * @param consumer callable, send database return to this callabke
   */
  public void getAuctionsByItemName(
      final String group,
      final String search,
      final Pair<Integer, Integer> limit,
      final Consumer<List<AuctionInfo>> consumer
  ) {
    final SelectBuilder builder = select()
        .addCondition("group", group);
    this.defineStateCondition(builder, StatusAuction.IN_PROGRESS);
    builder
        .addCondition("itemStack", "%:%" + search + "%", ConditionType.LIKE)
        .setExtension("ORDER BY start DESC");

    executeListingAuctions(builder, limit, consumer);
  }

  /**
   * Get number of auctions for a player
   *
   * @param group    group of auction
   * @param starter  the player who created the auction
   * @param consumer callable, send database return to this callabke
   */
  public void getAuctionNumber(final String group, final Player starter, final Consumer<Integer> consumer) {
    final SelectBuilder builder = select()
        .addField("COUNT(id) AS count");
    this.defineStateCondition(builder, StatusAuction.IN_PROGRESS);
    builder
        .addCondition("group", group)
        .addCondition("playerStarter", PlayerUtils.getUUIDToString(starter));

    QueryExecutor.of().execute(builder, res -> {
      if (res.next())
        consumer.accept(res.getInt("count"));
    });
  }

  /**
   * Get the price of the last item matching
   *
   * @param auction  AuctionInfo to search last matching auction
   * @param consumer Callback to call if price is found
   */
  public void getLastPrice(final AuctionInfo auction, final Consumer<Double> consumer) {
    final SelectBuilder builder = select()
        .addCondition("group", auction.getGroup())
        .addCondition("itemStack", auction.getItemStack())
        .addCondition("itemMeta", auction.getItemMeta())
        .addCondition("playerStarter", auction.getPlayerStarter())
        .setExtension("ORDER BY start DESC LIMIT 1");

    QueryExecutor.of().execute(builder, res -> {
      if (res.next()) {
        consumer.accept(res.getDouble("price"));
      }
    });
  }

  /**
   * Get all auctions inside database
   *
   * @param all      Define if it get only active auctions (not ended) or all auctions
   * @param consumer Callback to call if price is found
   */
  public void getAllAuctions(final Boolean all, final Consumer<List<AuctionInfo>> consumer) {
    final SelectBuilder builder = select()
        .setExtension("ORDER BY start DESC");

    if (!all) {
      builder.addCondition("status", StatusAuction.IN_PROGRESS.getValue());
    }
    executeListingAuctions(builder, null, consumer);
  }

  /**
   * Count sold and expired auctions since last player connection
   *
   * @param starter  Owner of auctions
   * @param consumer Consumer called when request is finished
   */
  public void countSoldAndExpiredAuctions(final OfflinePlayer starter, final Consumer<Map<StatusAuction, Integer>> consumer) {
    final Timestamp end = new Timestamp(starter.getLastPlayed());

    final SelectBuilder builder = select()
        .addField("status")
        .addField("COUNT(id) AS count");
    builder
        .addCondition("playerStarter", PlayerUtils.getUUIDToString(starter))
        .addCondition("end", end, ConditionType.SUPERIOR_EQUAL)
        .addCondition("end", DatabaseUtils.getTimestamp(), ConditionType.INFERIOR)
        .addCondition("status", Arrays.asList(StatusAuction.IN_PROGRESS.getValue(), StatusAuction.FINISHED.getValue()), ConditionType.IN)
        .addExtension("GROUP BY status");


    QueryExecutor.of().execute(builder, res -> {
      Map<StatusAuction, Integer> map = new HashMap<>();

      while (res.next()) {
        map.put(StatusAuction.getStatusAuction(res.getInt("status")), res.getInt("count"));
      }
      consumer.accept(map);
    });
  }

  /**
   * Get auctions of last n hours
   */
  public void getLastAuctions(
      final String group,
      final Integer hours,
      final StatusAuction status,
      final Pair<Integer, Integer> limit,
      final Consumer<List<AuctionInfo>> consumer) {
    final SelectBuilder builder = select()
        .addCondition("group", group);

    this.defineStateCondition(builder, status);
    final String sort = (status == StatusAuction.FINISHED) ? "end" : "start";
    builder
        .addCondition(sort, DatabaseUtils.minusHours(DatabaseUtils.getTimestamp(), hours), ConditionType.SUPERIOR_EQUAL)
        .setExtension("ORDER BY " + sort + " DESC");

    executeListingAuctions(builder, limit, consumer);
  }

  /**
   * Get average price of one specific item from last n days
   *
   * @param itemMeta Item to get price
   * @param group    Shop group
   * @param days     Number of days to analyze
   * @param analyze  Type of analyze to do (running, history or both
   * @param consumer Consumer to send result
   */
  public void getAveragePriceItem(
      final String itemMeta,
      final String group,
      final Integer days,
      final String analyze,
      final Consumer<Double> consumer) {
    final SelectBuilder builder = select()
        .addField("AVG(price) as averagePrice")
        .addField("COUNT(id) as count")
        .addCondition("group", group)
        .addCondition("itemMeta", itemMeta)
        .addCondition("start", DatabaseUtils.minusDays(DatabaseUtils.getTimestamp(), days), ConditionType.SUPERIOR_EQUAL);
    QueryExecutor.of().execute(defineAnalyzeAveragePrice(builder, analyze), res -> {
      if (res.next()) {
        if (res.getInt("count") > 0) {
          consumer.accept(EconomyUtils.roundValue(res.getDouble("averagePrice")));
          return;
        }
      }
      consumer.accept(null);
    });
  }

  /*
   * ================================
   *             TOOLS
   * ================================
   */

  /**
   * Define the status of the auction for the process of average price
   *
   * @param builder Base builder to add condition on
   * @param analyze Type of analyze to apply
   * @return Return builder modified
   */
  private SelectBuilder defineAnalyzeAveragePrice(final SelectBuilder builder, final String analyze) {
    switch (analyze) {
      case "finished":
        return builder.addCondition("status", StatusAuction.FINISHED.getValue());
      case "in_progress":
        return builder.addCondition("status", StatusAuction.IN_PROGRESS.getValue());
      default:
        return builder.addCondition("status", StatusAuction.ABANDONED.getValue(), ConditionType.NOTEQUAL);
    }
  }

  /**
   * Create a querybuilder, update timestamp to now and change state to INPROGRESS
   *
   * @param baseBuilder    Base builder
   * @param expirationDays Days of expiration for the auction
   * @return Return same builder
   */
  private UpdateBuilder updateToNow(final UpdateBuilder baseBuilder, final Integer expirationDays) {
    final Timestamp ts = DatabaseUtils.getTimestamp();
    final UpdateBuilder builder = (baseBuilder == null) ? update() : baseBuilder;

    builder.addValue("start", ts.toString());
    builder.addValue("end", DatabaseUtils.addDays(ts, expirationDays).toString());
    builder.addValue("status", StatusAuction.IN_PROGRESS.getValue());

    return builder;
  }

  /**
   * Define from a StateAuction the condition to apply
   *
   * @param builder builder on which set condition
   * @param state   StateAuction to convert into condition
   */
  private void defineStateCondition(final ConditionBase builder, final StatusAuction state) {
    switch (state) {
      case EXPIRED:
        builder.addCondition("end", DatabaseUtils.getTimestamp(), ConditionType.INFERIOR);
        builder.addCondition("status", StatusAuction.IN_PROGRESS.getValue());
        break;
      case IN_PROGRESS:
        builder.addCondition("end", DatabaseUtils.getTimestamp(), ConditionType.SUPERIOR_EQUAL);
        break;
      default:
        builder.addCondition("status", state.getValue());
    }
  }

  /**
   * Request the database to know if the auction specified is ended or not
   *
   * @param executor instance of QueryExecutor to us
   * @param id       if the auction to verify
   * @return if auction is ended
   */
  private Boolean canEditAuction(final QueryExecutor executor, final int id) {
    final AtomicBoolean end = new AtomicBoolean(true);
    final SelectBuilder select = select()
        .addField("id")
        .addCondition("id", id)
        .addCondition("status", StatusAuction.IN_PROGRESS.getValue(), ConditionType.NOTEQUAL);

    executor.execute(select, res -> {
      if (res != null && res.next())
        end.set(false);
    }, Exception::printStackTrace);
    return end.get();
  }

  /**
   * Execute the query that listing auctions
   *
   * @param builder  Builder to execute
   * @param limit    Limit of results to return
   * @param consumer Consumer to call when listing works
   */
  private void executeListingAuctions(final BaseBuilder builder, final Pair<Integer, Integer> limit, final Consumer<List<AuctionInfo>> consumer) {
    if (limit != null)
      builder.addExtension(GlobalMarketChest.plugin.getSqlConnector().buildLimit(limit));
    QueryExecutor.of().execute(builder, res -> {
      final List<AuctionInfo> auctions = new ArrayList<>();
      while (res.next())
        auctions.add(new AuctionInfo(res));
      consumer.accept(auctions);
    });
  }
}
