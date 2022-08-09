package fr.epicanard.globalmarketchest.listeners;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.listeners.events.MoneyExchangeEvent;
import fr.epicanard.globalmarketchest.utils.ConfigUtils;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.invokeMethod;


@AllArgsConstructor
public class DataBridgeListener implements Listener {

  private static String shareEconomyPath = "General.enableModules.shareEconomy";
  private Plugin dataBridge;

  @EventHandler
  public void onMoneyExchange(final MoneyExchangeEvent event) {
    if (event.getSourcePlayer() != null && event.getTargetPlayer() != null && event.getPrice() != null) {
      takeMoney(event.getSourcePlayer(), event.getPrice());
      addMoney(event.getTargetPlayer(), event.getPrice());
    }
  }

  /**
   * Remove money specified from account of player
   *
   * @param player Player to tak money
   * @param price  Price to remove from player account
   */
  private void takeMoney(final UUID player, final Double price) {
    Bukkit.getScheduler().runTaskAsynchronously(GlobalMarketChest.plugin, new Runnable() {
      public void run() {
        final Object economyHandler = getEconomyStorageHandler();
        if (hasAccount(economyHandler, player)) {
          final Double balance = getOfflineBalance(economyHandler, player);
          setOfflineMoney(economyHandler, player, balance - price);
          if (debugEnabled()) {
            LoggerUtils.info("Economy Debug - GlobalMarketChest - removing offline money | " + player);
            LoggerUtils.info("Economy Debug - GlobalMarketChest - data snapshot | offline balance: " + balance + " | remove money: " + price + " | " + player);
          }
        }
      }
    });
  }

  /**
   * Add money specified from account of player
   *
   * @param player Player to tak money
   * @param price  Price to remove from player account
   */
  private void addMoney(final UUID player, final Double price) {
    Bukkit.getScheduler().runTaskAsynchronously(GlobalMarketChest.plugin, new Runnable() {
      public void run() {
        final Object economyHandler = getEconomyStorageHandler();
        if (hasAccount(economyHandler, player)) {
          final Double balance = getOfflineBalance(economyHandler, player);
          setOfflineMoney(economyHandler, player, balance + price);
          if (debugEnabled()) {
            LoggerUtils.info("Economy Debug - GlobalMarketChest - adding offline money | " + player);
            LoggerUtils.info("Economy Debug - GlobalMarketChest - data snapshot | offline balance: " + balance + " | add money: " + price + " | " + player);
          }
        }
      }
    });
  }

  /* =============================== */
  /*   MysqlPlayerDataBridge tools   */
  /* =============================== */

  private Object getEconomyStorageHandler() {
    return invokeMethod(this.dataBridge, "getEconomyStorageHandler");
  }

  private Boolean hasAccount(final Object handler, final UUID player) {
    return (Boolean) invokeMethod(handler, "hasAccount", player);
  }

  private Double getOfflineBalance(final Object handler, final UUID player) {
    return (Double) invokeMethod(handler, "getOfflineBalance", player);
  }

  private void setOfflineMoney(final Object handler, final UUID player, final Double price) {
    invokeMethod(handler, "setOfflineMoney", player, price);
  }

  private Boolean debugEnabled() {
    return getConfigBoolean(this.dataBridge, "Debug.EconomySync", false);
  }

  /**
   * Define if the mysqlPlayerDataBridge support can be activated
   *
   * @param dataBridgePlugin MysqlPlayerDataBridge plugin
   * @return Can be activate
   */
  public static Boolean canBeEnabled(final Plugin dataBridgePlugin) {
    if (dataBridgePlugin == null || dataBridgePlugin.isEnabled()
        || !ConfigUtils.getBoolean("MultiServer.MysqlPlayerDataBridgeSupport", false)) {
      return false;
    }
    if (!DataBridgeListener.shareEconomy(dataBridgePlugin)) {
      LoggerUtils.warn("To activate MysqlPlayerDataBridge support, You must set '" + shareEconomyPath + "' to true inside MysqlPlayerDataBridge config");
      return false;
    }
    return true;
  }

  /**
   * MysqlPlayerDataBridge : Get config variable to define if shareEconomy is enabled
   *
   * @param dataBridgePlugin Plugin MysqlPlayerDataBridge
   * @return If economy is shared
   */
  private static Boolean shareEconomy(final Plugin dataBridgePlugin) {
    return getConfigBoolean(dataBridgePlugin, shareEconomyPath, false);
  }

  /**
   * MysqlPlayerDataBridge : Get config variable sent in parameter
   *
   * @param plugin       Plugin MysqlPlayerDataBridge
   * @param path         Path to variable
   * @param defaultValue Default value to set
   * @return boolean value
   */
  private static Boolean getConfigBoolean(final Plugin plugin, final String path, final Boolean defaultValue) {
    final Object configHandler = invokeMethod(plugin, "getConfigHandler");
    if (configHandler != null) {
      return (Boolean) invokeMethod(configHandler, "getBoolean", path, defaultValue);
    }
    return defaultValue;
  }
}
