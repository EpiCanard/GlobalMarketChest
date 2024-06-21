package fr.epicanard.globalmarketchest.gui.shops.toggler;

import fr.epicanard.globalmarketchest.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class TogglerConfig {
  @Getter
  private Boolean set;
  @Getter
  private String type;
  @Getter
  private Integer position;
  @Getter
  private ItemStack setItem;
  @Getter @Setter
  private ItemStack unsetItem;

  public TogglerConfig(Map<?, ?> config) {
    this.position = Utils.getOrElse((Integer) config.get("Pos"), 1);
    this.set = Utils.getOrElse((Boolean) config.get("Set"), true);
    this.type = Utils.getOrElse((String) config.get("Type"), "single");
    this.setItem = Utils.getButton(Utils.getOrElse((String) config.get("SetItem"), "CircleSetItem"));
    this.unsetItem = Utils.getButton(Utils.getOrElse((String) config.get("UnsetItem"),
        "circle".equals(this.type) ? "CircleUnsetItem" : null));
  }

  public TogglerConfig(TogglerConfig config) {
    this.position = config.position;
    this.set = config.set;
    this.type = config.type;
    this.setItem = config.setItem;
    this.unsetItem = config.unsetItem;
  }

  /**
   * Instanciate a new Toggler depending of it's type
   *
   * @param inv Inventory to use for instanciation
   * @return New instance of Toggler (SingleToggler or CircleToggler)
   */
  public Toggler instanceToggler(Inventory inv) {
    Toggler ret = null;
    switch (this.type) {
      case "circle":
        ret = new CircleToggler(inv, this);
        break;
      case "single":
      default:
        ret = new SingleToggler(inv, this);
        break;
    }
    return ret;
  }
}
