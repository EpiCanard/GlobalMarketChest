package fr.epicanard.globalmarketchest.utils.chat;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;

import java.util.UUID;

class ChatAbandon implements ConversationAbandonedListener {

  /**
   * Called when the conversation is abandonned or finished
   * Set the chat return
   * 
   * @param abandon Abandon event
   */
  public void conversationAbandoned(ConversationAbandonedEvent abandon) {
    final UUID playerUuid = (UUID) abandon.getContext().getAllSessionData().get(ChatSessionData.PLAYER);
    final String response = (String) abandon.getContext().getAllSessionData().get(ChatSessionData.RESPONSE);
    final InventoryGUI inv = GlobalMarketChest.plugin.inventories.getInventory(playerUuid);
    if (inv != null) {
      inv.setChatReturn(response);
    }
  }
}
