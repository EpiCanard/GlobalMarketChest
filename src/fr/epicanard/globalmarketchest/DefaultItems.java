package fr.epicanard.globalmarketchest;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum DefaultItems {
  DEFAULT(Material.STAINED_GLASS_PANE, 15, " ", null),
  LEAVE(Material.STAINED_GLASS_PANE, 14, "Sortie", "Retour à la maison"),
  MONEY(Material.GOLD_INGOT, 0, "Votre argent", null),
  NEW(Material.NETHER_STAR, 0, "§2Créer une nouvelle enchère", "--/§fCliquer ici pour vendre vos objets");

  private Material mat;
  private byte meta;
  private String name;
  private String lore;

  DefaultItems(Material mat, int meta, String name, String lore) {
    this.mat = mat;
    this.meta = (byte) meta;
    this.name = (name == null) ? " " : name;
    this.lore = lore;
  }

  public ItemStack getItemStack() {
    ItemStack item = new ItemStack(this.mat, 1, this.meta);
    ItemMeta met = item.getItemMeta();

    met.setDisplayName(this.name);
    if (this.lore != null)
      met.setLore(Arrays.asList(StringUtils.split(this.lore, "/")));
    item.setItemMeta(met);

    return item;
  }
}
