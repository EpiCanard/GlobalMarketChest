package fr.epicanard.globalmarketchest.listeners;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.minecraft.server.v1_12_R1.CreativeModeTab;
import net.minecraft.server.v1_12_R1.Item;

public class GUIListeners implements Listener {
  @EventHandler
  public void onClick(InventoryClickEvent event) {

    String[] name = StringUtils.split(event.getInventory().getName(), "-");
    System.out.println("==" + name[0] + "==");
    if (event.getCurrentItem() != null && (name[0].equals("§8GlobalMarketChest") || name[0].equals("§8GMC"))) {
      event.setCancelled(true);
      Item it = CraftMagicNumbers.getItem(event.getCurrentItem().getType());
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
      System.out.println("============");
    }

  }
}
