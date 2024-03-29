package fr.epicanard.globalmarketchest.gui.actions;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;

import java.util.function.Consumer;

/**
 * Consumer to open chat and get content the inventoryGUI
 */
public class ChatInput implements Consumer<InventoryGUI> {

  private Consumer<String> returnConsumer;
  private String path;

  public ChatInput(String path, Consumer<String> consumer) {
    this.returnConsumer = consumer;
    this.path = path;
  }

  @Override
  public void accept(InventoryGUI t) {
    t.openChat(this.path, this.returnConsumer);
  }
}
