package fr.epicanard.globalmarketchest.listeners.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@AllArgsConstructor
public class MoneyExchangeEvent extends Event {
  private static final HandlerList handlers = new HandlerList();

  @Getter
  private UUID sourcePlayer;
  @Getter
  private UUID targetPlayer;
  @Getter
  private Double price;

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
