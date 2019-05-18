package fr.epicanard.globalmarketchest.utils.chat;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

class ChatPrompt extends StringPrompt {
  private final String prompt;

  public ChatPrompt(String prompt) {
    this.prompt = prompt;
  }

  /**
   * Return the next ChatPrompt to display
   * Here we have only one prompt so it return always null
   * 
   * @param context Current ConversationContext
   * @param answer The answer received
   * @return
   */
  @Override
  public Prompt acceptInput(ConversationContext context, String answer) {
    context.getAllSessionData().put(ChatSessionData.RESPONSE, answer);
    return null;
  }

  /**
   * Get the current Prompt to display
   * 
   * @param context Current ConversationContext
   * @return Return the string prompt to display
   */
  @Override
  public String getPromptText(ConversationContext context) {
    final YamlConfiguration config = GlobalMarketChest.plugin.getConfigLoader().getConfig();
    String extra = "";
    if (config.getBoolean("Chat.UseExitSequence", false) && config.getBoolean("Chat.DisplayHelpExit", false)) {
      extra = String.format(LangUtils.get("Divers.ExitChatMode"), config.getString("Chat.ExitSequence", "exit"));
    }
    
    return PlayerUtils.getPrefix() + Utils.toColor(this.prompt) + extra;
  }

}