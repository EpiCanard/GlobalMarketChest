package fr.epicanard.globalmarketchest.commands;

import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Accessors(chain = true)
public class CommandNode {
  @Getter
  private List<CommandNode> subNodes = new ArrayList<>();
  @Getter
  private String nodeName;
  private Boolean hasParams;
  private Boolean mustBePlayer;
  private Permissions permission;
  @Setter
  private CommandConsumer command;
  @Setter
  private TabCompleteConsumer tabConsumer;

  /**
   * Constructor Class CommandNode
   *
   * Default params :
   * params[default:false] Define if this command has parameter or subcommand
   * playerOnly[default:true] Define if this command must be only execute by a player
   *
   * @param name Name of the parameter for command
   * @param perm Permission that must checked before execution
   */
  public CommandNode(String name, Permissions perm) {
    this(name, perm, false, true);
  }

  /**
   * Constructor Class CommandNode
   *
   * Default params :
   * playerOnly[default:true] Define if this command must be only execute by a player
   *
   * @param name Name of the parameter for command
   * @param perm Permission that must checked before execution
   * @param params Define if this command has parameter or subcommand
   */
  public CommandNode(String name, Permissions perm, Boolean params) {
    this(name, perm, params, true);
  }

  /**
   * Constructor Class CommandNode
   *
   * Default params :
   * params[default:false] Define if this command has parameter or subcommand
   *
   * @param name Name of the parameter for command
   * @param perm Permission that must checked before execution
   * @param playerOnly Define if this command must be only execute by a player
   */
  public CommandNode(String name, Boolean playerOnly, Permissions perm) {
    this(name, perm, false, playerOnly);
  }

  /**
   * Constructor Class CommandNode
   *
   * @param name Name of the parameter for command
   * @param perm Permission that must checked before execution
   * @param params Define if this command has parameter or subcommand
   * @param playerOnly Define if this command must be only execute by a player
   */
  public CommandNode(String name, Permissions perm, Boolean params, Boolean playerOnly) {
    this.nodeName = name;
    this.permission = perm;
    this.mustBePlayer = playerOnly;
    this.hasParams = params;
  }

  /**
   * Add Sub Command
   *
   * @param subNode CommandNode to add
   * @return Return CommandeNode sent in parameter
   */
  public CommandNode addSubNode(CommandNode subNode) {
    this.subNodes.add(subNode);
    return subNode;
  }

  /**
   * Get a command sub node with his name
   *
   * @param name Name of the command node
   * @return CommandeNode that match or null
   */
  private CommandNode getCommandNode(String name) {
    for (CommandNode node : subNodes) {
      if (node.getNodeName().equals(name))
        return node;
    }
    return null;
  }

  /**
   * Get list of SubNodes name
   *
   * @return list of subnodes name
   */
  private List<String> getSubNodesName() {
    return this.subNodes.stream().map(node -> node.getNodeName()).collect(Collectors.toList());
  }

  /**
   * Define tab complete for this CommandNode
   *
   * @param String Param for tab complete
   * @return List<String> List for completion
   */
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    final List<String> empty = new ArrayList<>();
    if (args.length > 1 && this.subNodes.size() > 0) {
      CommandNode node = this.getCommandNode(args[0].toLowerCase());
      if (node != null && node.permission != null && node.permission.isSetOn(sender) && node.canExecute(sender)) {
        return node.onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
      }
      return empty;
    } else if (this.subNodes.size() > 0) {
      return this.getSubNodes()
        .stream()
        .filter(node -> node.permission != null && node.permission.isSetOn(sender) && node.canExecute(sender)
            && node.getNodeName().startsWith(args[0].toLowerCase())
        )
        .map(node -> node.getNodeName())
        .collect(Collectors.toList());
    } else if (this.tabConsumer != null) {
      return this.tabConsumer.apply(sender, args);
    }
    return empty;
  }

  /**
   * Print a message to sender about the invalidity of the command
   *
   * @param CommandSender The sender that execute the command
   * @return Boolean Return always false
   */
  public Boolean invalidCommand(CommandSender sender, String command) {
    PlayerUtils.sendMessage(sender, LangUtils.get("ErrorMessages.InvalidCommand") + command);
    return false;
  }

  /**
   * Define if the sender can execute the command
   *
   * @param CommandSender The sender that execute the command
   * @return Boolean return true if the command can be executed
   */
  public Boolean canExecute(CommandSender sender) {
    return !(mustBePlayer && !(sender instanceof Player));
  }

  /**
   * Execute the command or call the subnode to execute it
   *
   * @param CommandSender The sneder that execute the command
   * @param args The arguments for the command
   * @return Return true if the command succeed
   */
  public Boolean execute(String cmd, CommandSender sender, String[] args) {
    if (!canExecute(sender)) {
      PlayerUtils.sendMessageConfig(sender, "ErrorMessages.PlayerOnly");
      return false;
    }
    if (permission != null && !this.permission.isSetOn(sender)) {
      return this.invalidCommand(sender, cmd);
    }
    if (this.hasParams || args.length == 0) {
      if (this.command != null)
        return this.command.accept(this, cmd, sender, args);
      return this.invalidCommand(sender, cmd);
    }
    if (this.getSubNodesName().contains(args[0].toLowerCase())) {
      return this.getCommandNode(args[0].toLowerCase()).execute(cmd, sender, Arrays.copyOfRange(args, 1, args.length));
    }
    return this.invalidCommand(sender, cmd);
  }
}
