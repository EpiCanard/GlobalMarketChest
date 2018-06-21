package fr.epicanard.globalmarketchest.gui.shops;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.gui.InterfacesLoader;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.paginator.Paginator;
import fr.epicanard.globalmarketchest.gui.paginator.PaginatorConfig;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.gui.actions.LeaveShop;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.server.v1_12_R1.ChatMessage;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutOpenWindow;

public abstract class ShopInterface {
  @Accessors(fluent=true) @Getter
  protected Boolean isTemp = false;
  protected Paginator paginator = null;
  protected InventoryGUI inv;
  protected Map<Integer, Consumer<InventoryGUI>> actions = new HashMap<Integer, Consumer<InventoryGUI>>();

  public ShopInterface(InventoryGUI inv) {
    this.inv = inv;
    String className = this.getClass().getSimpleName();
    PaginatorConfig conf = InterfacesLoader.getInstance().getPaginatorConfig(className);
    if (conf != null)
      this.paginator = new Paginator(this.inv.getInv(), conf);
    this.actions.put(8, new LeaveShop());
  }

  /**
   * Load specific interface with is className
   * 
   * @param gui
   */
  public void load() {
    String className = this.getClass().getSimpleName();
    ItemStack[] items = InterfacesLoader.getInstance().getInterface(className);
    if (items == null)
      return;
    for (int i = 0; i < 54; i++)
      this.inv.getInv().setItem(i, items[i]);
    if (this.paginator != null)
      this.paginator.reloadInterface();
    this.updateInventoryName(className);
  }

  /**
   * Update the inventory name with current interface name
   * 
   * @param interfaceName
   */
  private void updateInventoryName(String interfaceName)
  {
    String title = LangUtils.getOrElse("InterfacesTitle." + interfaceName, "&2GlobalMarkChest");
    EntityPlayer ep = ((CraftPlayer)this.inv.getPlayer()).getHandle();
    PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, "minecraft:chest", new ChatMessage(title), this.inv.getPlayer().getOpenInventory().getTopInventory().getSize());
    ep.playerConnection.sendPacket(packet);
    ep.updateInventory(ep.activeContainer);
  }

  /**
   * Unload interface
   */
  public void unload() {
    this.inv.getWarn().stopWarn();
  }

  /**
   * Called when a mouse event is done inside inventory
   * 
   * @param event
   */
  public void onClick(InventoryClickEvent event, InventoryGUI inv) {
    if (event.getClick() != ClickType.LEFT)
      return;
    if (this.paginator == null ||!this.paginator.onClick(event.getSlot()))
      Optional.ofNullable(this.actions.get(event.getSlot())).ifPresent(c -> c.accept(inv));
  }

  /**
   * Called when a mouse drop event is done inside inventory
   * 
   * @param event
   */
  public void onDrop(InventoryClickEvent event, InventoryGUI inv) {}
  
}
