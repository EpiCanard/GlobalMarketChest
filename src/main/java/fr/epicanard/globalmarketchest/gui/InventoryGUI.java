package fr.epicanard.globalmarketchest.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.gui.shops.Warning;
import lombok.Getter;

/**
 * Inventory Class that manage inventory view
 */
public class InventoryGUI {
  @Getter
  private Inventory inv;
  private Deque<ShopInterface> shopStack = new ArrayDeque<ShopInterface>();
  @Getter
  private Map<String, Object> transaction = new HashMap<String, Object>();
  @Getter
  private Player player;
  @Getter
  private Warning warn;

  public InventoryGUI() {
    this.inv = Bukkit.createInventory(null, 54, "§8GlobalMarketChest");
    this.warn = new Warning(this.inv);
  }

  /**
   * Check if the inventory in param is the same as this inventory
   * 
   * @param inventory Inventory to verify
   */
  public Boolean inventoryEquals(Inventory inventory) {
    return this.inv.equals(inventory);
  }

  /**
   * Open current inventory for the specified player
   * 
   * @param player
   */
  public void open(Player player) {
    this.player = player;
    player.openInventory(this.inv);
  }

  /**
   * Close current inventory for the specified player
   * 
   * @param player
   */
  public void close(Player player) {
    if (player != null)
      player.closeInventory();
    this.player.closeInventory();
  }

  /**
   * Unload temporary interface and come back to principal
   */
  public void unloadTempInterface() {
    try {
      ShopInterface peek;
      do {
        this.shopStack.pop().unload();
        peek = this.shopStack.peek();
      } while(peek != null && peek.isTemp());
      Optional.ofNullable(peek).ifPresent(e -> e.load());
    } catch (NoSuchElementException e) {}
  }
  
  /**
   * Unload last loaded Interface and load the previous one
   */
  public void unloadLastInterface() {
    try {
      this.shopStack.pop().unload();
      Optional.ofNullable(this.shopStack.peek()).ifPresent(e -> e.load());
    } catch (NoSuchElementException e) {}
  }

  /**
   * Unload all interface
   * 
   * @param name
   */
  public void unloadAllInterface() {
    try {
      this.shopStack.pop().unload();
      this.shopStack.clear();
    } catch (NoSuchElementException e) {}
  }

  /**
   * Load interface with a specific name
   * 
   * @param name
   */
  public void loadInterface(String name) {
    try {
      ShopInterface shop = (ShopInterface) Class.forName("fr.epicanard.globalmarketchest.gui.shops.interfaces." + name).getDeclaredConstructor(InventoryGUI.class).newInstance(this);
      Optional.ofNullable(this.shopStack.peek()).ifPresent(ShopInterface::unload);
     
      shop.load();
      this.shopStack.push(shop);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the loaded interface
   */
  public ShopInterface getInterface() {
    return this.shopStack.peek();
  }

  /**
   * Get Transiction Value
   * 
   * @param name Key to get transcation object
   * @return <T> return the object with these key
   */
  @SuppressWarnings("unchecked")
  public <T> T getTransValue(String name) {
    return (T) this.transaction.get(name);
  }
}
