package fr.epicanard.globalmarketchest.gui.Paginator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.InvalidPaginatorParameter;
import fr.epicanard.globalmarketchest.utils.Utils;
import lombok.Getter;
import lombok.Setter;

public class Paginator {
  private final Inventory inv;
  private ItemStack previous;
  private ItemStack next;
  private int previousPos = -1;
  private int nextPos = -1;
  private int counter;
  @Getter
  private PaginatorConfig config;
  @Setter
  private Consumer<Paginator> loadConsumer;
  @Setter
  private List<ItemStack> itemstacks = new ArrayList<ItemStack>();


  Paginator(Inventory inv, PaginatorConfig config) {
    this.inv = inv;
    this.previous = Utils.getButton("PreviousPage");
    this.next = Utils.getButton("NextPage");
    this.config = config;
  }

  Paginator(Inventory inv) {
    this(inv, null);
    try {
      this.config = new PaginatorConfig(1, 9, 1);
    } catch(InvalidPaginatorParameter e) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
    }
  }

  /**
   * Add next button to a specific position
   */
  public void addNext(int pos) {
    if (pos < 0 || pos >= this.inv.getSize())
      return;
    this.previousPos = pos;
    this.inv.setItem(pos, this.next);
  }

  /**
   * Add previous button to a specific position
   */
  public void addPrevious(int pos) {
    if (pos < 0 || pos >= this.inv.getSize())
      return;
    this.nextPos = pos;
    this.inv.setItem(pos, this.previous);
  }

  /**
   * Define NumPage position
   */
  public void addNumPage(int pos) {
    this.inv.setItem(pos, Utils.getButton("NumPage"));
    this.counter = pos;
  }

  /**
   * Update page counter
   */
  private void updateCounter() {
    ItemStack item = inv.getItem(this.counter);
    item.setAmount(this.config.getPage() + 1);
    this.inv.setItem(this.counter, item);
  }

  /**
   * Change to next page
   */
  public void nextPage() {
    int prev = this.config.getPage();
    if (this.config.nextPage() == prev)
      return;
    if (this.loadConsumer != null)
      this.loadConsumer.accept(this);
    this.updateCounter();
    this.loadItems();
  }

  /**
   * Change to previous page
   */
  public void previousPage() {
    int prev = this.config.getPage();
    if (this.config.previousPage() == prev)
      return;
    if (this.loadConsumer != null)
      this.loadConsumer.accept(this);
    this.updateCounter();
    this.loadItems();
  }

  /**
   * Load Items paginator in inventpry
   */
  private void loadItems() {
    for (int i = 0; i < this.config.getLimit(); i++) {
      try {
        ItemStack item = this.itemstacks.get(i);
        int pos = this.config.getStartPos() + 
                  Utils.getLine(i, this.config.getWidth()) * 9 +
                  Utils.getCol(i, this.config.getWidth());
        this.inv.setItem(pos, item);
      } catch (IndexOutOfBoundsException e) {
        break;
      }
    }
  }

  /**
   * Call the consumer and load items in inventory (for first use for exemple)
   */
  public void reload() {
    if (this.loadConsumer != null)
      this.loadConsumer.accept(this);
    this.loadItems();
  }

  /**
   * When reload interface call this method to reload all the paginator with the same result before leaving
   */
  public void reloadInterface() {
    this.updateCounter();
    this.loadItems();
    this.addNext(this.nextPos);
    this.addPrevious(this.previousPos);
  }


}