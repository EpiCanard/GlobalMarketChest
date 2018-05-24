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
  private ItemStack numPage;
  @Getter
  private PaginatorConfig config;
  @Setter
  private Consumer<Paginator> loadConsumer;
  @Setter
  private List<ItemStack> itemstacks = new ArrayList<ItemStack>();


  public Paginator(Inventory inv, PaginatorConfig config) {
    this.inv = inv;
    this.previous = Utils.getButton("PreviousPage");
    this.next = Utils.getButton("NextPage");
    this.numPage = Utils.getButton("NumPage");
    this.config = config;
  }

  public Paginator(Inventory inv) {
    this(inv, null);
    try {
      this.config = new PaginatorConfig(1, 9, 1, -1, -1, -1);
    } catch(InvalidPaginatorParameter e) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
    }
  }

  /**
   * Add item to inventory and check that param are valid
   */
  private void addItem(int pos, ItemStack item) {
    if (pos < 0 || pos >= this.inv.getSize() || item == null)
      return;
    this.inv.setItem(pos, item);
  }

  /**
   * Update page counter
   */
  private void updateCounter() {
    if (this.config.getNumPagePos() < 0 || this.config.getNumPagePos() >= this.inv.getSize())
      return;
    ItemStack item = inv.getItem(this.config.getNumPagePos());
    item.setAmount(this.config.getPage() + 1);
    this.inv.setItem(this.config.getNumPagePos(), item);
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
    this.addItem(this.config.getPreviousPos(), this.previous);
    this.addItem(this.config.getNextPos(), this.next);
    this.addItem(this.config.getNumPagePos(), this.numPage);
    this.reload();
  }

  public Boolean isButton(int pos) {
    return (pos >= 0 && (pos == this.config.getPreviousPos() || pos == this.config.getNextPos()));
  }

  public Boolean isInZone(int pos) {
    pos = pos - this.config.getStartPos();
    if (pos < 0)
      return false;
    if (Utils.getCol(pos, this.config.getWidth()) >= this.config.getWidth())
      return false;
    if (Utils.getLine(pos, this.config.getWidth()) >= this.config.getHeight())
      return false;
    return true;
  }

  public void onClick(int pos) {
    if (pos == this.config.getPreviousPos())
      this.previousPage();
    if (pos == this.config.getNextPos())
      this.nextPage();
  }

}