package fr.epicanard.globalmarketchest.gui.buttons;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ButtonOverview extends Button {
  
  public ButtonOverview(int pos) {
    super("GlobalView", pos);
  }

  public ButtonOverview(ItemStack item, String name, List<String> description, int pos) {
    super(item, name, description, pos);
  }

  @Override
  public void onButtonClick() {
  }

}
