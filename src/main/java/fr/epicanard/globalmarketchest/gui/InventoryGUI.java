package fr.epicanard.globalmarketchest.gui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;

public class InventoryGUI {
  private Inventory inv;
  private Deque<ShopInterface> shopStack = new ArrayDeque<ShopInterface>();

  public InventoryGUI() {
    this.inv = Bukkit.createInventory(null, 54, "§8GlobalMarketChest");
  }

  public Boolean inventoryEquals(Inventory i) {
    return this.inv.equals(i);
  }

  /**
   * Open current inventory for the specified player
   * 
   * @param player
   */
  public void open(Player player) {
    player.openInventory(this.inv);
  }

  /**
   * Close current inventory for the specified player
   * 
   * @param player
   */
  public void close(Player player) {
    player.closeInventory();
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
      Optional.ofNullable(peek).ifPresent(e -> e.load(this.inv));
    } catch (NoSuchElementException e) {}
  }
  
  /**
   * Unload last loaded Interface and load the previous one
   */
  public void unloadLastInterface() {
    try {
      this.shopStack.pop().unload();
      Optional.ofNullable(this.shopStack.peek()).ifPresent(e -> e.load(this.inv));
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
      ShopInterface shop = (ShopInterface) Class.forName("fr.epicanard.globalmarketchest.gui.shops." + name).newInstance();
      Optional.ofNullable(this.shopStack.peek()).ifPresent(ShopInterface::unload);

      shop.load(this.inv);
      this.shopStack.push(shop);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
}
