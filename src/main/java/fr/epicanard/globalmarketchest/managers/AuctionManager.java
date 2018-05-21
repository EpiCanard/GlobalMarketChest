package fr.epicanard.globalmarketchest.managers;

import java.sql.Timestamp;

import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionType;
import fr.epicanard.globalmarketchest.auctions.StateAuction;
import fr.epicanard.globalmarketchest.database.connections.DatabaseConnection;
import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryBuilder;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import net.minecraft.server.v1_12_R1.ItemStack;

class AuctionManager {
  /**
   * Create an auction inside database
   */
  public void createAuction(ItemStack itemStack, String itemMeta, Integer amount, Double price, AuctionType type, Player playerStarter, String group) {
    QueryBuilder builder = new QueryBuilder(DatabaseConnection.tableAuctions);
    Timestamp ts = DatabaseUtils.getTimestamp();

    builder.addValue("itemStack", itemStack.getItem().getName());
    builder.addValue("itemMeta", itemMeta);
    builder.addValue("amount", amount);
    builder.addValue("price", price);
    builder.addValue("state", StateAuction.INPROGRESS.getState());
    builder.addValue("type", type.getType());
    builder.addValue("playerStarter", PlayerUtils.getUUIDToString(playerStarter));
    builder.addValue("start", ts.toString());
    builder.addValue("end", DatabaseUtils.addDays(ts, 7).toString());
    builder.addValue("group", group);
    builder.execute(builder.insert());
  }

  /**
   * Update database when player obtain an auction
   */
  public void buyAuction(int id, Player buyer) {
    QueryBuilder builder = new QueryBuilder(DatabaseConnection.tableAuctions);

    builder.addValue("playerEnder", PlayerUtils.getUUIDToString(buyer));
    builder.addValue("state", StateAuction.FINISHED.getState());
    builder.addCondition("id", id);

    builder.execute(builder.update());
  }

  /**
   * Create a querybuilder, update timestamp to now and change state to INPROGRESS
   */
  private QueryBuilder updateToNow(QueryBuilder builder) {
    Timestamp ts = DatabaseUtils.getTimestamp();
    if (builder == null)
      builder = new QueryBuilder(DatabaseConnection.tableAuctions);

    builder.addValue("start", ts.toString());
    builder.addValue("end", DatabaseUtils.addDays(ts, 7).toString());
    builder.addValue("state", StateAuction.INPROGRESS.getState());
        
    return builder;
  }

  /**
   * Renew all player auctions expired
   */
  public void renewEveryAuctionOfPlayer(Player player, String group) {
    QueryBuilder builder = this.updateToNow(null);

    builder.addCondition("state", StateAuction.EXPIRED.getState());
    builder.addCondition("owner", PlayerUtils.getUUIDToString(player));
    builder.addCondition("group", group);
    builder.execute(builder.update());
  }

  /**
   * Renew a specific auction
   */
  public void renewAuction(int id) {
    QueryBuilder builder = this.updateToNow(null);

    builder.addCondition("id", id);
    builder.execute(builder.update());
  }

  public void purgeAuctions(Boolean useConfig) {
    QueryBuilder builder = new QueryBuilder(DatabaseConnection.tableAuctions);
    if  (useConfig) {
      Integer purge = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Auctions.PurgeInterval");
      builder.addCondition("start", DatabaseUtils.addDays(DatabaseUtils.getTimestamp(), purge * -1), ConditionType.inferior_equal);
    }
    builder.execute(builder.delete());
  }
}
