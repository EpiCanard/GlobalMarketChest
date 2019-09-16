package fr.epicanard.globalmarketchest.utils.chat;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

/**
 * Utiity Class for database actions
 */
@UtilityClass
public class ChatUtils {

  /**
   * Create a link text component where the player can click to execute the command
   *
   * @param text Text to set inside TextComponent
   * @param hover Hover text for the link
   * @param color Color of the text
   * @param command Command to execute when a player click on the link
   * @return TextComponent generated
   */
  public TextComponent createLink(String text, String hover, ChatColor color, String command) {
    TextComponent link = new TextComponent(text);
    if (hover != null)
      link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
    if (command != null)
      link.setClickEvent(new ClickEvent(Action.RUN_COMMAND, command));
    link.setColor(color);
    return link;
  }

  /**
   * Same as other methode createLink but with bracket around text
   *
   * @param text Text to set inside TextComponent
   * @param hover Hover text for the link
   * @param color Color of the text
   * @param command Command to execute when a player click on the link
   * @return TextComponent generated
   */
  public TextComponent createLinkWithBracket(String text, String hover, ChatColor color, String command) {
    return ChatUtils.createLink(" [ " + text + " ]", hover, color, command);
  }

  /**
   * Create a default TextComponent with a prefix or not, dépending of config var Logs.HidePrefix
   * if Logs.HidePrefix is set to false a prefix is had to each component created
   *
   * @param text Text to set inside TextComponent
   * @return TextComponent generated
   */
  public TextComponent newComponent(String text) {
    TextComponent component = new TextComponent(PlayerUtils.getPrefix());
    component.addExtra(text);
    return component;
  }

  /**
   * Create a conversation to dialog with a player
   * 
   * @param player Player to dialog with
   * @param message Prompt message to display
   * @return Return the conversation started
   */
  public Conversation newConversation(Player player, String message) {
    final ConversationFactory factory = new ConversationFactory(GlobalMarketChest.plugin);
    factory
      .withFirstPrompt(new ChatPrompt(message))
      .withLocalEcho(false)
      .withModality(false)
      .addConversationAbandonedListener(new ChatAbandon())
      .thatExcludesNonPlayersWithMessage(LangUtils.get("ErrorMessages.PlayerOnly"));

    final YamlConfiguration config = GlobalMarketChest.plugin.getConfigLoader().getConfig();
    if (config.getBoolean("Chat.UseExitSequence", false)) {
      factory.withEscapeSequence(config.getString("Chat.ExitSequence", "exit"));
    }
    if (config.getBoolean("Chat.UseTimeout", false)) {
      factory.withTimeout(config.getInt("Chat.Timeout", 10));
    }

    final Conversation conv = factory.buildConversation(player);
    conv.getContext().getAllSessionData().put(ChatSessionData.PLAYER, player.getUniqueId());
    return conv;
  }
}