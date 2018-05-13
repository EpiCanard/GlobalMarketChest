package fr.epicanard.globalmarketchest.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.StateAuction;
import fr.epicanard.globalmarketchest.database.connections.DatabaseConnection;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryBuilder;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import net.minecraft.server.v1_12_R1.ItemStack;

class AuctionManager {
  public void createAuction(ItemStack itemStack, String itemMeta, Integer amount, Double price, Player playerStarter, String worldGroup) {
    QueryBuilder builder = new QueryBuilder(DatabaseConnection.tableShops);
    Timestamp ts = DatabaseUtils.getTimestamp();

    builder.addValue("itemStack", itemStack.getItem().getName());
    builder.addValue("itemMeta", itemMeta);
    builder.addValue("amount", amount);
    builder.addValue("price", price);
    builder.addValue("state", StateAuction.INPROGRESS.getState());
    builder.addValue("playerStarter", PlayerUtils.getUUIDToString(playerStarter));
    builder.addValue("start", ts.toString());
    builder.addValue("end", DatabaseUtils.addDays(ts, 7).toString());
    builder.addValue("worldGroup", worldGroup);
    builder.execute(builder.insert());

    Connection co = GlobalMarketChest.plugin.getSqlConnection().getConnection();

    try {
      PreparedStatement prepared = co.prepareStatement("INSERT INTO " + DatabaseConnection.tableAuctions
          + " (itemStack, itemMeta, amount, price, state, playerStarter, playerEnder, start, end, worldGroup) VALUES (?, ?, ?, ?, ?)");

      prepared.executeUpdate();
      GlobalMarketChest.plugin.getSqlConnection().closeRessources(null, prepared);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    GlobalMarketChest.plugin.getSqlConnection().getBackConnection(co);
  }
}
