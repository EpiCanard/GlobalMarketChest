package fr.epicanard.globalmarketchest.gui.Paginator;

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
  private int counter;
  @Getter
  private PaginatorConfig config;
  @Setter
  private Consumer<Paginator> nextConsumer;
  @Setter
  private Consumer<Paginator> previousConsumer;


  Paginator(Inventory inv, PaginatorConfig config) {
    this.inv = inv;
    this.previous = Utils.getInstance().getButton("PreviousPage");
    this.next = Utils.getInstance().getButton("NextPage");
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
    this.inv.setItem(pos, this.next);
  }

  /**
   * Add previous button to a specific position
   */
  public void addPrevious(int pos) {
    this.inv.setItem(pos, this.previous);
  }

  /**
   * Define NumPage position
   */
  public void addNumPage(int pos) {
    this.inv.setItem(pos, Utils.getInstance().getButton("NumPage"));
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
    this.config.nextPage();
    if (this.nextConsumer != null)
      this.nextConsumer.accept(this);
    this.updateCounter();
  }

  /**
   * Change to previous page
   */
  public void previousPage() {
    int prev = this.config.getPage();
    if (this.config.nextPage() == prev)
      return;
    if (this.nextConsumer != null)
      this.nextConsumer.accept(this);
    this.updateCounter();
  }


}