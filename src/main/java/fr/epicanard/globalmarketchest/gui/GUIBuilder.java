package fr.epicanard.globalmarketchest.gui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;

public class GUIBuilder {
  private Inventory inv;
  private Deque<ShopInterface> shopStack = new ArrayDeque<ShopInterface>();

  public GUIBuilder() {
    this.inv = Bukkit.createInventory(null, 54, "§8GlobalMarketChest");
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
      do {
        this.shopStack.pop().unload();
      } while(this.shopStack.peek().isTemp());
      this.shopStack.peek().load(this.inv);
    } catch (NoSuchElementException e) {}
  }
  
  /**
   * Unload last loaded Interface and load the previous one
   */
  public void unloadLastInterface() {
    try {
      this.shopStack.pop().unload();
      this.shopStack.peek().load(this.inv);      
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
      shop.load(this.inv);
      
      this.shopStack.push(shop);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
