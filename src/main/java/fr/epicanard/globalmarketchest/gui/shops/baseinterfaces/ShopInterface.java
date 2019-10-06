package fr.epicanard.globalmarketchest.gui.shops.baseinterfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.exceptions.MissingMethodException;
import fr.epicanard.globalmarketchest.gui.InterfacesLoader;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.actions.LeaveShop;
import fr.epicanard.globalmarketchest.gui.paginator.Paginator;
import fr.epicanard.globalmarketchest.gui.paginator.PaginatorConfig;
import fr.epicanard.globalmarketchest.gui.shops.toggler.Toggler;
import fr.epicanard.globalmarketchest.gui.shops.toggler.TogglerConfig;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import fr.epicanard.globalmarketchest.utils.annotations.AnnotationCaller;
import fr.epicanard.globalmarketchest.utils.reflection.VersionSupportUtils;
import lombok.Getter;
import lombok.experimental.Accessors;

public abstract class ShopInterface {
  @Accessors(fluent=true) @Getter
  protected Boolean isTemp = false;
  protected Paginator paginator = null;
  protected Map<Integer, Toggler> togglers = new HashMap<>();
  protected InventoryGUI inv;
  protected Map<Integer, Consumer<InventoryGUI>> actions = new HashMap<Integer, Consumer<InventoryGUI>>();
  private ItemStack icon;

  public ShopInterface(InventoryGUI inv) {
    this.inv = inv;
    this.icon = Utils.getBackground();
    String className = this.getClass().getSimpleName();
    PaginatorConfig conf = InterfacesLoader.getInstance().getPaginatorConfig(className);
    if (conf != null)
      this.paginator = new Paginator(this.inv.getInv(), conf);
    List<TogglerConfig> togglersConfig = InterfacesLoader.getInstance().getTogglers(className);
    if (togglersConfig != null) {
      togglersConfig.forEach(togglerConfig -> {
        this.togglers.put(togglerConfig.getPosition(), togglerConfig.instanceToggler(inv.getInv()));
      });
    }
    this.actions.put(8, new LeaveShop());
  }

  /**
   * Load specific interface with is className
   */
  public void load() {
    String className = this.getClass().getSimpleName();
    ItemStack[] items = InterfacesLoader.getInstance().getInterface(className).clone();
    if (items == null)
      return;

    this.togglers.forEach((k, v) -> {
      v.getItems().forEach((pos, item) -> {
        items[pos] = item;
      });
    });
    for (int i = 0; i < 54; i++)
      this.inv.getInv().setItem(i, items[i]);
    if (this.paginator != null)
      this.paginator.reloadInterface();
    this.updateInventoryName(className);
    this.loadIcon();
  }

  /**
   * Update the inventory name with current interface name
   *
   * @param interfaceName Name of interface to set
   */
  private void updateInventoryName(String interfaceName) {
    String title = LangUtils.getOrElse("InterfacesTitle." + interfaceName, "&2GlobalMarketChest");
    try {
      AnnotationCaller.call("updateInventoryName", VersionSupportUtils.getInstance(), title, (Player)this.inv.getPlayer());
    } catch (MissingMethodException e) {
      e.printStackTrace();
    }
  }

  /**
   * Add the icon item inside inventory
   */
  private void loadIcon() {
    this.inv.getInv().setItem(4, this.icon);
  }

  /**
   * Set and load icon
   *
   * @param item Icon
   */
  protected void setIcon(ItemStack item) {
    this.icon = VersionSupportUtils.getInstance().setNbtTag(item);
    this.loadIcon();
  }

  /**
   * Unload interface
   */
  public void unload() {
    this.inv.getWarn().stopWarn();
  }

  /**
   * Called when interface is destroyed
   */
  public void destroy() {}

  /**
   * Called when a mouse event is done inside inventory
   *
   * @param event
   */
  public void onClick(InventoryClickEvent event, InventoryGUI inv) {
    if (event.getClick() != ClickType.LEFT)
      return;
    if (this.paginator == null || !this.paginator.onClick(event.getSlot()))
      Optional.ofNullable(this.actions.get(event.getSlot())).ifPresent(c -> c.accept(inv));
  }

  /**
   * Called when a mouse drop event is done inside inventory
   *
   * @param event
   */
  public void onDrop(InventoryClickEvent event, InventoryGUI inv) {}

  /**
   * Update the interface title
   */
  public void updateInterfaceTitle() {
    this.updateInventoryName(this.getClass().getSimpleName());
  }

}
