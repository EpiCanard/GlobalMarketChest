package fr.epicanard.globalmarketchest.listeners;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.listeners.events.MoneyExchangeEvent;
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
   * @param price Price to remove from player account
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
   * @param price Price to remove from player account
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

  private Object getEconomyStorageHandler() {
    return invokeMethod(this.dataBridge, "getEconomyStorageHandler");
  }

  private Boolean hasAccount(final Object handler, final UUID player) {
    return (Boolean)invokeMethod(handler, "hasAccount", player);
  }

  private Double getOfflineBalance(final Object handler, final UUID player) {
    return (Double)invokeMethod(handler, "getOfflineBalance", player);
  }

  private void setOfflineMoney(final Object handler, final UUID player, final Double price) {
    invokeMethod(handler, "setOfflineMoney", player, price);
  }

  private Boolean debugEnabled() {
//    final Object config = invokeMethod(this.dataBridge, "getConfigHandler");
//    if (config != null) {
//      return (Boolean)invokeMethod(config, "getBoolean", "Debug.EconomySync");
//    }
//    return false;
    return true;
  }
}
