package fr.epicanard.globalmarketchest.gui.shops.toggler;

import fr.epicanard.globalmarketchest.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

  public TogglerConfig(Integer pos, ConfigurationSection config) {
    this.position = pos;
    this.set = config.getBoolean("Set", true);
    this.type = config.getString("Type", "single");
    this.setItem = Utils.getButton(config.getString("SetItem", "CircleSetItem"));
    this.unsetItem = Utils.getButton(config.getString("UnsetItem", "circle".equals(this.type) ? "CircleUnsetItem" : null));
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
