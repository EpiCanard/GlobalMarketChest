package fr.epicanard.globalmarketchest.listeners;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import net.minecraft.server.v1_12_R1.CreativeModeTab;
import net.minecraft.server.v1_12_R1.Item;

/**
 * Listener for each interaction done inside a chest
 */
public class GUIListener implements Listener {
  @EventHandler
  public void onClick(InventoryClickEvent event) {
    if (event.getWhoClicked() instanceof Player) {
      Player p = (Player)event.getWhoClicked();
      InventoryGUI inv = GlobalMarketChest.plugin.inventories.getInventory(p.getUniqueId());
      if (inv != null && inv.inventoryEquals(event.getInventory())) {
        if (inv.inventoryEquals(event.getClickedInventory()))
          event.setCancelled(true);
        ClickType click = event.getClick();
        
        if (event.isShiftClick() || click == ClickType.DOUBLE_CLICK || click == ClickType.DROP)
          event.setCancelled(true);
      }
    }
  }
  
  @EventHandler
  public void onDrag(InventoryDragEvent event) {
    if (event.getWhoClicked() instanceof Player) {
      Player p = (Player)event.getWhoClicked();
      InventoryGUI inv = GlobalMarketChest.plugin.inventories.getInventory(p.getUniqueId());
      if (inv != null && inv.inventoryEquals(event.getInventory()))
        event.setCancelled(true);
    }
  }

  // TO DELETE : Not Working for every item (See if it's work better in 1.13)
  public void getCategoryByMystery(Material item) {
    Item it = CraftMagicNumbers.getItem(item);
    try {
      System.out.println(it.getName());
      Field[] lst = FieldUtils.getAllFields(it.getClass());
      for (Field a : lst) {
        if (a.getType() == CreativeModeTab.class) {
          CreativeModeTab ret = (CreativeModeTab) FieldUtils.readField(a, it, true);
          if (ret != null) {
            System.out.println("==> " + FieldUtils.readField(ret, "p", true));
          }
        }
      }
    } catch (IllegalArgumentException e) {
      System.out.println("marche pas");
    } catch (IllegalAccessException e) {
      System.out.println("marche pas illegal");
    }

  }
}
