package fr.epicanard.globalmarketchest.gui.shops.toggler;

import java.util.Map;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.utils.Utils;
import lombok.Getter;

public class TogglerConfig {
  @Getter
  private Boolean set;
  @Getter
  private String type;
  @Getter
  private Integer position;
  @Getter
  private ItemStack setItem;
  @Getter
  private ItemStack unsetItem;

  public TogglerConfig(Map<?, ?> config) {
    this.position = Utils.getOrElse((Integer)config.get("Pos"), 1);
    this.set = Utils.getOrElse((Boolean)config.get("Set"), true);
    this.type = Utils.getOrElse((String)config.get("Type"), "single");
    this.setItem = Utils.getButton(Utils.getOrElse((String)config.get("SetItem"), "CircleSetItem"));
    String tmp = Utils.getOrElse((String)config.get("UnsetItem"), this.type.equals("circle") ? "CircleUnsetItem" : null);
    this.unsetItem = (tmp == null) ? Utils.getBackground() : Utils.getButton(tmp);
  }

  /**
   * Instanciate a new Toggler depending of it's type
   *
   * @param inv Inventory to use for instanciation
   * @return New instance of Toggler (SingleToggler or CircleToggler)
   */
  public Toggler instanceToggler(Inventory inv) {
    Toggler ret = null;
    switch(this.type) {
      case "single":
        ret = new SingleToggler(inv, this);
        break;
      case "circle":
        ret = new CircleToggler(inv, this);
        break;
    }
    return ret;
  }
}