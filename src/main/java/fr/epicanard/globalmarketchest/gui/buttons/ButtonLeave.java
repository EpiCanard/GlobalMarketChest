package fr.epicanard.globalmarketchest.gui.buttons;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class ButtonLeave extends Button{

  public ButtonLeave(int pos) {
    super("ExitButton", pos);
  }
    
  public ButtonLeave(ItemStack item, String name, List<String> description, int pos) {
    super(item, name, description, pos);
  }
  
  @Override
  public void onButtonClick() {
  }

}
