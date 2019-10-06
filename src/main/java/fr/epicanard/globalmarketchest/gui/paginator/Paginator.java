package fr.epicanard.globalmarketchest.gui.paginator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.InvalidPaginatorParameter;
import fr.epicanard.globalmarketchest.utils.Utils;
import fr.epicanard.globalmarketchest.utils.reflection.VersionSupportUtils;
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
  private Consumer<Integer> clickConsumer;
  @Getter
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
   * Reset page to 0
   */
  public void resetPage() {
    if (this.config.getPage() <= 0)
      return;
    this.config.resetPage();
    if (this.loadConsumer != null)
      this.loadConsumer.accept(this);
    this.updateCounter();
    this.loadItems();
  }

  /**
   * Change to next page
   */
  public void nextPage() {
    int prev = this.config.getPage();
    if (this.config.nextPage() == prev)
      return;
    List<ItemStack> tmp = new ArrayList<>(this.itemstacks);
    if (this.loadConsumer != null)
      this.loadConsumer.accept(this);
    if (this.itemstacks.size() == 0) {
      this.config.previousPage();
      this.setItemStacks(tmp);
    } else {
      this.updateCounter();
      this.loadItems();
    }
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
      if (i < this.itemstacks.size())
        this.inv.setItem(this.getPos(i), this.itemstacks.get(i));
      else
        this.inv.clear(this.getPos(i));
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
    this.addItem(this.config.getPreviousPos(), this.previous);
    this.addItem(this.config.getNextPos(), this.next);
    this.addItem(this.config.getNumPagePos(), this.numPage);
    this.updateCounter();
    this.reload();
  }

  /**
   * Clear all paginator zone
   */
  public void clear() {
    for (int i = 0; i < this.config.getLimit(); i++) {
      this.inv.clear(this.getPos(i));
    }
  }

  /**
   * Define if the position is a button previous or next
   *
   * @return Boolean
   */
  public Boolean isButton(int pos) {
    return (pos >= 0 && (pos == this.config.getPreviousPos() || pos == this.config.getNextPos() || pos == this.config.getNumPagePos()));
  }

  /**
   * Define if the position is inside the click zone
   *
   * @return Boolean
   */
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

  /**
   * Execute action when player click on button (previous or next) or inside the click zone
   */
  public Boolean onClick(int pos) {
    if (this.isButton(pos)) {
      if (pos == this.config.getPreviousPos())
        this.previousPage();
      if (pos == this.config.getNextPos())
        this.nextPage();
      if (pos == this.config.getNumPagePos())
        this.resetPage();
      return true;
    }
    if (this.isInZone(pos)) {
      Optional.ofNullable(this.clickConsumer).ifPresent(e -> e.accept(this.getIndex(pos)));
      return true;
    }
    return false;

  }

  /**
   * Get the real position inside the inventory depending of the index (the case number x from the paginator (not the same than the inventory))
   *
   * @param int index
   * @return int
   */
  public int getPos(int index) {
    return this.config.getStartPos() +
      Utils.getLine(index, this.config.getWidth()) * 9 +
      Utils.getCol(index, this.config.getWidth());
  }

  /**
   * Get index inside paginator from the real inventory position
   *
   * @param int pos
   * @return int
   */
  public int getIndex(int pos) {
    if (!this.isInZone(pos))
      return -1;
    pos -= this.config.getStartPos();
    return Utils.getLine(pos, 9) * this.config.getWidth() + Utils.getCol(pos, 9);
  }

  /**
   * Get Sublist from a given list depending of the page
   *
   * @param List<T> list
   * @return List<T>
   */
  public <T> List<T> getSubList(List<T> list) {
    int start = Utils.getIndex(this.getConfig().getStartLimit(), list.size(), true);
    int end = Utils.getIndex(this.getConfig().getStartLimit() + this.getConfig().getLimit(), list.size(), true);
    return list.subList(start, end);
  }

  /**
   * Get the limit for the paginator
   */
  public Pair<Integer, Integer> getLimit() {
    return new ImmutablePair<Integer,Integer>(this.config.getStartLimit(), this.config.getLimit());
  }

  public void setItemStacks(List<ItemStack> items) {
    this.itemstacks.clear();
    items = Utils.mapList(items, itemStack -> VersionSupportUtils.getInstance().setNbtTag(itemStack));
    this.itemstacks.addAll(items);
  }

  public ItemStack getItemStack(int pos) {
    if (pos < 0 || pos >= this.itemstacks.size())
      return null;
    return this.itemstacks.get(pos);
  }
}