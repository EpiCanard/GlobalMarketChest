package fr.epicanard.globalmarketchest.managers;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.StateAuction;
import fr.epicanard.globalmarketchest.database.connections.DatabaseConnection;
import fr.epicanard.globalmarketchest.database.querybuilder.ColumnType;
import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryExecutor;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.ConditionBase;
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
      builder.addValue("ended", auction.getEnded());
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

    if (this.canEditAuction(executor, id) == false)
      return false;

    UpdateBuilder builder = new UpdateBuilder(DatabaseConnection.tableAuctions);

    builder.addValue("playerEnder", PlayerUtils.getUUIDToString(buyer));
    builder.addValue("end", DatabaseUtils.getTimestamp().toString());
    builder.addValue("ended", true);
    builder.addCondition("id", id);

    return executor.execute(builder);
  }

  /**
   * ==============
   *     RENEW
   * ==============
   */

  /**
   * Renew all player auctions expired
   *
   * @param player
   * @param group
   */
  public Boolean renewEveryAuctionOfPlayer(Player player, String group, StateAuction state) {
    UpdateBuilder builder = this.updateToNow(null);

    if (state == StateAuction.FINISHED || state == StateAuction.ABANDONED)
      return false;
    String playeruuid = PlayerUtils.getUUIDToString(player);
    this.defineStateCondition(builder, state);
    builder.addCondition("playerStarter", playeruuid);
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
    QueryExecutor executor = QueryExecutor.of();

    if (this.canEditAuction(executor, id) == false)
      return false;
    builder.addCondition("id", id);
    builder.addCondition("ended", false);
    return executor.execute(builder);
  }

  /**
   * ==============
   *      UNDO
   * ==============
   */

  /**
   * Undo a group of auctions
   *
   * @param player
   * @param group
   */
  public Boolean undoGroupOfPlayerAuctions(Player player, String group, List<Integer> auctions) {
    UpdateBuilder builder = new UpdateBuilder(DatabaseConnection.tableAuctions);
    String playeruuid = PlayerUtils.getUUIDToString(player);

    builder.addCondition("id", auctions, ConditionType.IN);
    builder.addCondition("playerStarter", playeruuid);
    builder.addCondition("group", group);
    builder.addValue("ended", true);
    builder.addValue("playerEnder", playeruuid);
    builder.addValue("end", DatabaseUtils.getTimestamp().toString());
    return QueryExecutor.of().execute(builder);
  }

  /**
   * Undo auction
   *
   * @param id Id of the auction to undo
   */
  public Boolean undoAuction(int id, String playerUuid) {
    UpdateBuilder builder = new UpdateBuilder(DatabaseConnection.tableAuctions);
    QueryExecutor executor = QueryExecutor.of();

    if (this.canEditAuction(executor, id) == false)
      return false;

    builder.addCondition("id", id);
    builder.addValue("ended", true);
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
    DeleteBuilder builder = new DeleteBuilder(DatabaseConnection.tableAuctions);
    Integer purge = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Auctions.PurgeInterval");
    if (purge < 0)
      return;
    builder.addCondition("end", DatabaseUtils.addDays(DatabaseUtils.getTimestamp(), purge * -1), ConditionType.INFERIOR_EQUAL);

    QueryExecutor.of().execute(builder);
  }

  /**
   * =====================
   *     LIST AUCTIONS
   * =====================
   */

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
    builder.addCondition("itemStack", Arrays.asList(items), (category.equals("!")) ? ConditionType.NOTIN : ConditionType.IN);
    this.defineStateCondition(builder, StateAuction.INPROGRESS);
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
    this.defineStateCondition(builder, StateAuction.INPROGRESS);
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
  public void getAuctions(String group, StateAuction state, Player starter, Player ender, Pair<Integer, Integer> limit, Consumer<List<AuctionInfo>> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnection.tableAuctions);

    builder.addCondition("group", group);
    this.defineStateCondition(builder, state);
    if (starter != null)
      builder.addCondition("playerStarter", PlayerUtils.getUUIDToString(starter));
    if (ender != null)
      builder.addCondition("playerEnder", PlayerUtils.getUUIDToString(ender));

    if (state == StateAuction.INPROGRESS || state == StateAuction.EXPIRED)
      builder.setExtension("ORDER BY start DESC");
    else
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

  /**
   * Get number of auctions for a player
   *
   * @param group group of auction
   * @param starter the player who created the auction
   * @param consumer callable, send database return to this callabke
   */
  public void getAuctionNumber(String group, Player starter, Consumer<Integer> consumer) {
    SelectBuilder builder = new SelectBuilder(DatabaseConnection.tableAuctions);

    builder.addField("COUNT(id) AS count");
    builder.addCondition("group", group);
    builder.addCondition("ended", false);
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
    SelectBuilder builder = new SelectBuilder(DatabaseConnection.tableAuctions);

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
   * ================================
   *             TOOLS
   * ================================
   */

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
    builder.addValue("ended", false);

    return builder;
  }

  /**
   * Define from a StateAuction the condition to apply
   *
   * @param builder builder on which set condition
   * @param state StateAuction to convert into condition
   */
  private void defineStateCondition(ConditionBase builder, StateAuction state) {
    switch (state) {
      case EXPIRED:
        builder.addCondition("end", DatabaseUtils.getTimestamp(), ConditionType.INFERIOR);
        builder.addCondition("ended", false);
        break;
      case INPROGRESS:
        builder.addCondition("end", DatabaseUtils.getTimestamp(), ConditionType.SUPERIOR);
        builder.addCondition("ended", false);
        break;
      case ABANDONED:
        builder.addCondition("ended", true);
        builder.addCondition("playerEnder", new ColumnType("playerStarter"));
        break;
      case FINISHED:
        builder.addCondition("ended", true);
        builder.addCondition("playerEnder", new ColumnType("playerStarter"), ConditionType.NOTEQUAL);
        break;
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
    SelectBuilder select = new SelectBuilder(DatabaseConnection.tableAuctions);
    AtomicBoolean end = new AtomicBoolean(true);

    select.addField("id");
    select.addCondition("id", id);
    select.addCondition("ended", true, ConditionType.EQUAL);

    executor.execute(select, res -> {
      try {
        if (res != null && res.next())
          end.set(false);
      } catch(SQLException e) {
        e.printStackTrace();
      }
    });
    return end.get();
  }

}
