package fr.epicanard.globalmarketchest.utils.chat;

import fr.epicanard.globalmarketchest.utils.ConfigUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import static fr.epicanard.globalmarketchest.utils.LangUtils.format;


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
    final ConfigurationSection config = ConfigUtils.get().getConfigurationSection("Chat");
    String extra = "";
    if (config.getBoolean("UseExitSequence", false) && config.getBoolean("DisplayHelpExit", false)) {
      extra = format("Divers.ExitChatMode", "exit", config.getString("ExitSequence", "exit"));
    }
    
    return PlayerUtils.getPrefix() + Utils.toColor(this.prompt) + extra;
  }

}
