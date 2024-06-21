package fr.epicanard.globalmarketchest.listeners;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.listeners.events.MoneyExchangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MoneyExchangeListener implements Listener {

  @EventHandler
  public void onMoneyExchange(final MoneyExchangeEvent event) {
    GlobalMarketChest.plugin.economy.exchangeMoney(event.getSourcePlayer(), event.getTargetPlayer(), event.getPrice(), event.getPriceAfterTax());
  }
}
