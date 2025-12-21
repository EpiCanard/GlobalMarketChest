package fr.epicanard.globalmarketchest.utils.reflection.inventory;

import fr.epicanard.globalmarketchest.exceptions.MissingMethodException;
import fr.epicanard.globalmarketchest.utils.annotations.Version;
import fr.epicanard.globalmarketchest.utils.reflection.Path;
import fr.epicanard.globalmarketchest.utils.reflection.VersionField;
import org.bukkit.entity.Player;

import static fr.epicanard.globalmarketchest.utils.annotations.AnnotationCaller.call;
import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.invokeMethod;
import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.newInstance;

public class NMSInventoryTitleUpdater implements IInventoryTitleUpdater {

  /**
   * Update the current inventory name
   *
   * @param player Player owner of inventory
   * @param title New title of inventory
   */
  @Override
  public void updateInventoryName(Player player, String title) {
    try {
      call("updateInventoryName", this, title, player);
    } catch (MissingMethodException e) {
      e.printStackTrace();
    }
  }

  /**
   * Update the inventory name update by the player
   *
   * @param title  The new inventory Name
   * @param player The player
   */
  @Version(name = "updateInventoryName", versions = { "1.12", "1.13" })
  public void updateInventoryName_1_13(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = newInstance(Path.MINECRAFT_SERVER.getClass("ChatMessage"), title, new Object[] {});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("activeContainer");
      Object windowId = activeContainerVF.get("windowId").value();

      Class<?> ichat = Path.MINECRAFT_SERVER.getClass("IChatBaseComponent");

      Object packet = Path.MINECRAFT_SERVER.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, String.class, ichat, Integer.TYPE)
          .newInstance(windowId, "minecraft:chest", ichat.cast(chatMessage), 54);

      Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);

      playerConnection.getClass().getMethod("sendPacket", Path.MINECRAFT_SERVER.getClass("Packet"))
          .invoke(playerConnection, packet);
      entityPlayer.getClass()
          .getMethod("updateInventory", Path.MINECRAFT_SERVER.getClass("Container"))
          .invoke(entityPlayer, activeContainerVF.value());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Update the inventory name update by the player
   *
   * @param title  The new inventory Name
   * @param player The player
   */
  @Version(name = "updateInventoryName", versions = { "1.14", "1.15", "1.16" })
  public void updateInventoryName_old(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = newInstance(Path.MINECRAFT_SERVER.getClass("ChatMessage"), title,
          new Object[] {});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("activeContainer");
      Object windowId = activeContainerVF.get("windowId").value();

      Class<?> ichat = Path.MINECRAFT_SERVER.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_SERVER.getClass("Containers");

      Object packet = Path.MINECRAFT_SERVER.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("GENERIC_9X6").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);

      playerConnection.getClass().getMethod("sendPacket", Path.MINECRAFT_SERVER.getClass("Packet"))
          .invoke(playerConnection, packet);
      entityPlayer.getClass()
          .getMethod("updateInventory", Path.MINECRAFT_SERVER.getClass("Container"))
          .invoke(entityPlayer, activeContainerVF.value());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Update the inventory name update by the player
   *
   * @param title  The new inventory Name
   * @param player The player
   */
  @Version(name = "updateInventoryName", versions = { "1.17" })
  public void updateInventoryName_1_17(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = newInstance(Path.MINECRAFT_NETWORK_CHAT.getClass("ChatMessage"), title,
          new Object[] {});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("bV");
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_WORLD_INVENTORY.getClass("Containers");

      Object packet = Path.MINECRAFT_NETWORK_GAME.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);

      playerConnection.getClass().getMethod("sendPacket", Path.MINECRAFT_NETWORK_PROTOCOL.getClass("Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Version(name = "updateInventoryName", versions = { "1.18" })
  public void updateInventoryName_1_18(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = newInstance(Path.MINECRAFT_NETWORK_CHAT.getClass("ChatMessage"), title,
          new Object[] {});
      Class<?> containerClass = Path.MINECRAFT_WORLD_INVENTORY.getClass("Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer).getWithType(containerClass);
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_WORLD_INVENTORY.getClass("Containers");

      Object packet = Path.MINECRAFT_NETWORK_GAME.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);

      playerConnection.getClass().getMethod("a", Path.MINECRAFT_NETWORK_PROTOCOL.getClass("Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Version(name = "updateInventoryName", versions = { "1.19" })
  public void updateInventoryName_1_19(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent")
          .getMethod("b", String.class).invoke(null, title);
      Class<?> containerClass = Path.MINECRAFT_WORLD_INVENTORY.getClass("Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer).getWithType(containerClass);
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_WORLD_INVENTORY.getClass("Containers");

      Object packet = Path.MINECRAFT_NETWORK_GAME.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);

      playerConnection.getClass().getMethod("a", Path.MINECRAFT_NETWORK_PROTOCOL.getClass("Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Version(name = "updateInventoryName", versions = { "1.20.0", "1.20.1" })
  public void updateInventoryName_latest_1_20(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent")
          .getMethod("b", String.class).invoke(null, title);
      Class<?> containerClass = Path.MINECRAFT_WORLD_INVENTORY.getClass("Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer).getWithType(containerClass);
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_WORLD_INVENTORY.getClass("Containers");

      Object packet = Path.MINECRAFT_NETWORK_GAME.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("c").get(entityPlayer);

      playerConnection.getClass().getMethod("a", Path.MINECRAFT_NETWORK_PROTOCOL.getClass("Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Version(name = "updateInventoryName", versions = {  "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6"})
  public void updateInventoryName_1_21_1(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Class<?> entityHumanClass = Path.MINECRAFT_WORLD_ENTITY_PLAYER.getClass("EntityHuman");
      Object chatMessage = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent")
          .getMethod("b", String.class).invoke(null, title);
      Class<?> containerClass = Path.MINECRAFT_WORLD_INVENTORY.getClass("Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer, entityHumanClass).getWithType(containerClass);
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_WORLD_INVENTORY.getClass("Containers");

      Object packet = Path.MINECRAFT_NETWORK_GAME.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("c").get(entityPlayer);

      playerConnection.getClass().getMethod("b", Path.MINECRAFT_NETWORK_PROTOCOL.getClass("Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
