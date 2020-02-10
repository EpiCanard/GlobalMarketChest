package fr.epicanard.globalmarketchest.utils;

import fr.epicanard.globalmarketchest.GlobalMarketChest;

import java.util.UUID;

public class EconomyUtils {

  /**
   * Format money to string depending of economy plugin
   *
   * @param money Money to format
   * @return Money formatted
   */
  public static String format(final Double money) {
    return GlobalMarketChest.plugin.economy.getEconomy().format(money);
  }

  /**
   * Get money of a player
   *
   * @param uuid Uuid of the player
   * @return Money of player
   */
  public static double getMoneyOfPlayer(final UUID uuid) {
    return GlobalMarketChest.plugin.economy.getMoneyOfPlayer(uuid);
  }
}
