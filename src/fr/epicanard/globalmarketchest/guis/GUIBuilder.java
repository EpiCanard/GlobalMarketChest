package fr.epicanard.globalmarketchest.guis;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.epicanard.globalmarketchest.DefaultItems;

public class GUIBuilder {
  private Inventory inv;
  private String name;
  private ItemStack icon = DefaultItems.DEFAULT.getItemStack();

  public GUIBuilder(String interfaceName) {
    String displayName = "§8";
    if (interfaceName != null)
      displayName += "GMC-" + interfaceName;
    else
      displayName += "GlobalMarketChest";
    this.inv = Bukkit.createInventory(null, 54, displayName);
    this.name = interfaceName;

    // Init iterface
    ItemStack item = DefaultItems.DEFAULT.getItemStack();
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(" ");
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    item.setItemMeta(meta);
    for (int i = 0; i < 54; i++) {
      inv.setItem(i, item);
    }
  }

  public void open(Player player) {
    player.openInventory(inv);
  }

  public void setItemTo(int pos, ItemStack item) {
    inv.setItem(pos, item);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setIcon(ItemStack mat) {
    if (mat != null) {
      this.icon = mat;
      ItemMeta meta = this.icon.getItemMeta();
      meta.setDisplayName((this.name == null) ? " " : this.name);
      this.icon.setItemMeta(meta);
      inv.setItem(4, this.icon);
    }
  }

  public ItemStack getIcon() {
    return this.icon;
  }

  public void setLeave(Boolean set) {
    ItemStack item = (set == true) ? DefaultItems.LEAVE.getItemStack() : DefaultItems.DEFAULT.getItemStack();
    this.setItemTo(8, item);
  }

  public void setBack(ItemStack item) {
    ItemMeta met = item.getItemMeta();

    met.setDisplayName("Retour");
    met.setLore(Arrays.asList("Retourner à l'inventaire précédent"));
    item.setItemMeta(met);
    this.setItemTo(0, item);
  }

  public void setNew(Boolean set) {
    ItemStack item = (set == true) ? DefaultItems.NEW.getItemStack() : DefaultItems.DEFAULT.getItemStack();
    if (set == true) {
      ItemMeta met = item.getItemMeta();
      List<String> lores = met.getLore();
      lores.set(0, "§f(1/50)");
      met.setLore(lores);
      item.setItemMeta(met);
    }
    this.setItemTo(53, item);
  }

}
