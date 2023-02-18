package fr.epicanard.globalmarketchest.utils.reflection;

import fr.epicanard.globalmarketchest.utils.Version;
import lombok.Getter;
import org.bukkit.entity.Player;

import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.*;

public abstract class InventoryUtils {

  private static InventoryUtils INSTANCE;

  private static InventoryUtils getInstance() {
    if (INSTANCE == null) {
      if (Version.isLowerOrEqualsTo(Version.V1_13))
        INSTANCE = new V1_12();
      else if (Version.isLowerOrEqualsTo(Version.V1_16))
        INSTANCE = new V1_14();
      else if (Version.isEqualsTo(Version.V1_17))
        INSTANCE = new V1_17();
      else if (Version.isEqualsTo(Version.V1_18))
        INSTANCE = new V1_18();
      else
        INSTANCE = new V1_X();
    }
    return INSTANCE;
  }

  /**
   * Update the inventory name update by the player
   *
   * @param title  The new inventory Name
   * @param player The player
   */
  public static void updateInventoryName(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");

      VersionField activeContainerVF = getInstance().getActiveContainer(VersionField.from(entityPlayer));
      Object windowId = activeContainerVF.get(getInstance().getWindowIdField()).value();

      Object titleComponent = getInstance().buildTitleComponent(title);
      Object packet = getInstance().buildPacket(windowId, titleComponent, player);

      Object playerConnection = entityPlayer.getClass().getDeclaredField(getInstance().getPlayerConnectionField())
          .get(entityPlayer);

      playerConnection.getClass()
          .getMethod(getInstance().getSendPacketField(), getInstance().getPacketPackage().getClass("Packet"))
          .invoke(playerConnection, packet);
      getInstance().updateContainerInventory(entityPlayer, activeContainerVF.value());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected abstract VersionPath getContainerPackage();

  protected abstract VersionPath getPacketPackage();

  protected abstract VersionPath getPacketOutPackage();

  protected abstract VersionPath getChatPackage();

  protected abstract String getWindowIdField();

  protected abstract String getPlayerConnectionField();

  protected abstract String getSendPacketField();

  protected abstract VersionField getActiveContainer(VersionField entityPlayer) throws Exception;

  protected abstract void updateContainerInventory(Object entityPlayer, Object activeContainer) throws Exception;

  protected abstract Object buildPacket(Object windowId, Object titleComponent, Player player) throws Exception;

  protected abstract Object buildTitleComponent(String title) throws Exception;

  protected Object buildPacketLatest(String genericField, Object windowId, Object titleComponent) throws Exception {
    Class<?> ichat = getInstance().getChatPackage().getClass("IChatBaseComponent");
    Class<?> containers = getInstance().getContainerPackage().getClass("Containers");

    Object packet = getInstance().getPacketOutPackage().getClass("PacketPlayOutOpenWindow")
        .getConstructor(Integer.TYPE, containers, ichat)
        .newInstance(windowId, containers.getField(genericField).get(null), ichat.cast(titleComponent));

    return packet;
  }

  /* ============================
   *      Implementation
   * ============================
   */
  // 1.12 - 1.13
  @Getter
  private static class V1_12 extends InventoryUtils {
    private final VersionPath containerPackage = VersionPath.MINECRAFT_SERVER;
    private final VersionPath packetPackage = VersionPath.MINECRAFT_SERVER;
    private final VersionPath packetOutPackage = VersionPath.MINECRAFT_SERVER;
    private final VersionPath chatPackage = VersionPath.MINECRAFT_SERVER;
    private final String windowIdField = "windowId";
    private final String playerConnectionField = "playerConnection";
    private final String sendPacketField = "sendPacket";

    @Override
    protected void updateContainerInventory(Object entityPlayer, Object activeContainer) throws Exception {
      entityPlayer.getClass()
          .getMethod("updateInventory", VersionPath.MINECRAFT_SERVER.getClass("Container"))
          .invoke(entityPlayer, activeContainer);
    }

    @Override
    protected Object buildPacket(Object windowId, Object titleComponent, Player player) throws Exception {
      Class<?> ichat = VersionPath.MINECRAFT_SERVER.getClass("IChatBaseComponent");

      Object packet = VersionPath.MINECRAFT_SERVER.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, String.class, ichat, Integer.TYPE)
          .newInstance(windowId, "minecraft:chest", ichat.cast(titleComponent),
              player.getOpenInventory().getTopInventory().getSize()); // TODO Can be replaced by 54 ?

      return packet;
    }

    @Override
    protected Object buildTitleComponent(String title) throws Exception {
      return newInstance(VersionPath.MINECRAFT_SERVER.getClass("ChatMessage"), title, new Object[] {});
    }

    @Override
    protected VersionField getActiveContainer(VersionField entityPlayer) throws Exception {
      return entityPlayer.get("activeContainer");
    }
  }

  // 1.14 - 1.16
  @Getter
  private static class V1_14 extends InventoryUtils {
    private final VersionPath containerPackage = VersionPath.MINECRAFT_SERVER;
    private final VersionPath packetPackage = VersionPath.MINECRAFT_SERVER;
    private final VersionPath packetOutPackage = VersionPath.MINECRAFT_SERVER;
    private final VersionPath chatPackage = VersionPath.MINECRAFT_SERVER;
    private final String windowIdField = "windowId";
    private final String playerConnectionField = "playerConnection";
    private final String sendPacketField = "sendPacket";

    @Override
    protected void updateContainerInventory(Object entityPlayer, Object activeContainer) throws Exception {
      entityPlayer.getClass()
          .getMethod("updateInventory", VersionPath.MINECRAFT_SERVER.getClass("Container"))
          .invoke(entityPlayer, activeContainer);
    }

    @Override
    protected Object buildPacket(Object windowId, Object titleComponent, Player player) throws Exception {
      return buildPacketLatest("GENERIC_9X6", windowId, titleComponent);
    }

    @Override
    protected Object buildTitleComponent(String title) throws Exception {
      return newInstance(VersionPath.MINECRAFT_SERVER.getClass("ChatMessage"), title, new Object[] {});
    }

    @Override
    protected VersionField getActiveContainer(VersionField entityPlayer) throws Exception {
      return entityPlayer.get("activeContainer");
    }
  }

  // 1.17
  @Getter
  private static class V1_17 extends InventoryUtils {
    private final VersionPath containerPackage = VersionPath.MINECRAFT_WORLD_INVENTORY;
    private final VersionPath packetPackage = VersionPath.MINECRAFT_NETWORK_PROTOCOL;
    private final VersionPath packetOutPackage = VersionPath.MINECRAFT_NETWORK_GAME;
    private final VersionPath chatPackage = VersionPath.MINECRAFT_NETWORK_CHAT;
    private final String windowIdField = "j";
    private final String playerConnectionField = "b";
    private final String sendPacketField = "sendPacket";

    @Override
    protected void updateContainerInventory(Object entityPlayer, Object activeContainer) throws Exception {
      return;
    }

    @Override
    protected Object buildPacket(Object windowId, Object titleComponent, Player player) throws Exception {
      return buildPacketLatest("f", windowId, titleComponent);
    }

    @Override
    protected Object buildTitleComponent(String title) throws Exception {
      return newInstance(VersionPath.MINECRAFT_SERVER.getClass("ChatMessage"), title, new Object[] {});
    }

    @Override
    protected VersionField getActiveContainer(VersionField entityPlayer) throws Exception {
      Class<?> containerClass = getInstance().getContainerPackage().getClass("Container");
      return entityPlayer.getWithType(containerClass);
    }
  }

  // 1.18
  @Getter
  private static class V1_18 extends InventoryUtils {
    private final VersionPath containerPackage = VersionPath.MINECRAFT_WORLD_INVENTORY;
    private final VersionPath packetPackage = VersionPath.MINECRAFT_NETWORK_PROTOCOL;
    private final VersionPath packetOutPackage = VersionPath.MINECRAFT_NETWORK_GAME;
    private final VersionPath chatPackage = VersionPath.MINECRAFT_NETWORK_CHAT;
    private final String windowIdField = "j";
    private final String playerConnectionField = "b";
    private final String sendPacketField = "a";

    @Override
    protected void updateContainerInventory(Object entityPlayer, Object activeContainer) throws Exception {
      return;
    }

    @Override
    protected Object buildPacket(Object windowId, Object titleComponent, Player player) throws Exception {
      return buildPacketLatest("f", windowId, titleComponent);
    }

    @Override
    protected Object buildTitleComponent(String title) throws Exception {
      return newInstance(VersionPath.MINECRAFT_SERVER.getClass("ChatMessage"), title, new Object[] {});
    }

    @Override
    protected VersionField getActiveContainer(VersionField entityPlayer) throws Exception {
      Class<?> containerClass = getInstance().getContainerPackage().getClass("Container");
      return entityPlayer.getWithType(containerClass);
    }
  }

  // 1.19+
  @Getter
  private static class V1_X extends InventoryUtils {
    private final VersionPath containerPackage = VersionPath.MINECRAFT_WORLD_INVENTORY;
    private final VersionPath packetPackage = VersionPath.MINECRAFT_NETWORK_PROTOCOL;
    private final VersionPath packetOutPackage = VersionPath.MINECRAFT_NETWORK_GAME;
    private final VersionPath chatPackage = VersionPath.MINECRAFT_NETWORK_CHAT;
    private final String windowIdField = "j";
    private final String playerConnectionField = "b";
    private final String sendPacketField = "a";

    @Override
    protected void updateContainerInventory(Object entityPlayer, Object activeContainer) throws Exception {
      return;
    }

    @Override
    protected Object buildPacket(Object windowId, Object titleComponent, Player player) throws Exception {
      return buildPacketLatest("f", windowId, titleComponent);
    }

    @Override
    protected Object buildTitleComponent(String title) throws Exception {
      return VersionPath.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent").getMethod("b", String.class).invoke(null,
          title);
    }

    @Override
    protected VersionField getActiveContainer(VersionField entityPlayer) throws Exception {
      Class<?> containerClass = getInstance().getContainerPackage().getClass("Container");
      return entityPlayer.getWithType(containerClass);
    }
  }

  // /**
  // * Update the inventory name update by the player
  // *
  // * @param title The new inventory Name
  // * @param player The player
  // */
  // // @Version(name = "updateInventoryName", versions = { "1.12", "1.13" })
  // public void updateInventoryName_1_13(String title, Player player) {
  // try {
  // Object entityPlayer = invokeMethod(player, "getHandle");
  // VersionField activeContainerVF =
  // VersionField.from(entityPlayer).get("activeContainer");
  // Object windowId = activeContainerVF.get("windowId").value();
  //
  // Class<?> ichat = getClassFromPathWithVersion(Path.MINECRAFT_SERVER,
  // "IChatBaseComponent");
  //
  // Object chatMessage =
  // newInstance(getClassFromPathWithVersion(Path.MINECRAFT_SERVER,
  // "ChatMessage"), title, new Object[] {});
  // Object packet = getClassFromPathWithVersion(Path.MINECRAFT_SERVER,
  // "PacketPlayOutOpenWindow")
  // .getConstructor(Integer.TYPE, String.class, ichat, Integer.TYPE)
  // .newInstance(windowId, "minecraft:chest", ichat.cast(chatMessage),
  // player.getOpenInventory().getTopInventory().getSize());
  //
  // Object playerConnection =
  // entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
  //
  // playerConnection.getClass().getMethod("sendPacket",
  // getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "Packet"))
  // .invoke(playerConnection, packet);
  // entityPlayer.getClass()
  // .getMethod("updateInventory",
  // getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "Container"))
  // .invoke(entityPlayer, activeContainerVF.value());
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // /**
  // * Update the inventory name update by the player
  // *
  // * @param title The new inventory Name
  // * @param player The player
  // */
  // // @Version(name = "updateInventoryName", versions = { "1.14", "1.15", "1.16"
  // })
  // public void updateInventoryName_old(String title, Player player) {
  // try {
  // Object entityPlayer = invokeMethod(player, "getHandle");
  // VersionField activeContainerVF =
  // VersionField.from(entityPlayer).get("activeContainer");
  // Object windowId = activeContainerVF.get("windowId").value();
  //
  // Class<?> ichat = getClassFromPathWithVersion(Path.MINECRAFT_SERVER,
  // "IChatBaseComponent");
  // Class<?> containers = getClassFromPathWithVersion(Path.MINECRAFT_SERVER,
  // "Containers");
  //
  // Object chatMessage =
  // newInstance(getClassFromPathWithVersion(Path.MINECRAFT_SERVER,
  // "ChatMessage"), title, new Object[] {});
  // Object packet = getClassFromPathWithVersion(Path.MINECRAFT_SERVER,
  // "PacketPlayOutOpenWindow")
  // .getConstructor(Integer.TYPE, containers, ichat)
  // .newInstance(windowId, containers.getField("GENERIC_9X6").get(null),
  // ichat.cast(chatMessage));
  //
  // Object playerConnection =
  // entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
  //
  // playerConnection.getClass().getMethod("sendPacket",
  // getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "Packet"))
  // .invoke(playerConnection, packet);
  // entityPlayer.getClass()
  // .getMethod("updateInventory",
  // getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "Container"))
  // .invoke(entityPlayer, activeContainerVF.value());
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // /**
  // * Update the inventory name update by the player
  // *
  // * @param title The new inventory Name
  // * @param player The player
  // */
  // // @Version(name = "updateInventoryName", versions = { "1.17" })
  // public void updateInventoryName_1_17(String title, Player player) {
  // try {
  // Object entityPlayer = invokeMethod(player, "getHandle");
  // VersionField activeContainerVF = VersionField.from(entityPlayer).get("bV");
  // Object windowId = activeContainerVF.get("j").value();
  //
  // Class<?> ichat = getClassFromPath(Path.MINECRAFT_NETWORK_CHAT,
  // "IChatBaseComponent");
  // Class<?> containers = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY,
  // "Containers");
  //
  // Object chatMessage =
  // newInstance(getClassFromPath(Path.MINECRAFT_NETWORK_CHAT, "ChatMessage"),
  // title, new Object[] {});
  // Object packet = getClassFromPath(Path.MINECRAFT_NETWORK_GAME,
  // "PacketPlayOutOpenWindow")
  // .getConstructor(Integer.TYPE, containers, ichat)
  // .newInstance(windowId, containers.getField("f").get(null),
  // ichat.cast(chatMessage));
  //
  // Object playerConnection =
  // entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);
  //
  // playerConnection.getClass().getMethod("sendPacket",
  // getClassFromPath(Path.MINECRAFT_NETWORK_PROTOCOL, "Packet"))
  // .invoke(playerConnection, packet);
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // // @Version(name = "updateInventoryName", versions = { "1.18" })
  // public void updateInventoryName_1_18(String title, Player player) {
  // try {
  // Object entityPlayer = invokeMethod(player, "getHandle");
  // Class<?> containerClass = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY,
  // "Container");
  // VersionField activeContainerVF =
  // VersionField.from(entityPlayer).getWithType(containerClass);
  // Object windowId = activeContainerVF.get("j").value();
  //
  // Class<?> ichat = getClassFromPath(Path.MINECRAFT_NETWORK_CHAT,
  // "IChatBaseComponent");
  // Class<?> containers = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY,
  // "Containers");
  //
  // Object chatMessage =
  // newInstance(getClassFromPath(Path.MINECRAFT_NETWORK_CHAT, "ChatMessage"),
  // title, new Object[] {});
  // Object packet = getClassFromPath(Path.MINECRAFT_NETWORK_GAME,
  // "PacketPlayOutOpenWindow")
  // .getConstructor(Integer.TYPE, containers, ichat)
  // .newInstance(windowId, containers.getField("f").get(null),
  // ichat.cast(chatMessage));
  //
  // Object playerConnection =
  // entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);
  //
  // playerConnection.getClass().getMethod("a",
  // getClassFromPath(Path.MINECRAFT_NETWORK_PROTOCOL, "Packet"))
  // .invoke(playerConnection, packet);
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
  // // @Version(name = "updateInventoryName")
  // public void updateInventoryName_latest(String title, Player player) {
  // try {
  // Object entityPlayer = invokeMethod(player, "getHandle");
  // Class<?> containerClass = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY,
  // "Container");
  // VersionField activeContainerVF =
  // VersionField.from(entityPlayer).getWithType(containerClass);
  // Object windowId = activeContainerVF.get("j").value();
  //
  // Class<?> ichat = getClassFromPath(Path.MINECRAFT_NETWORK_CHAT,
  // "IChatBaseComponent");
  // Class<?> containers = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY,
  // "Containers");
  //
  // Object chatMessage = getClassFromPath(Path.MINECRAFT_NETWORK_CHAT,
  // "IChatBaseComponent").getMethod("b", String.class).invoke(null, title);
  // Object packet = getClassFromPath(Path.MINECRAFT_NETWORK_GAME,
  // "PacketPlayOutOpenWindow")
  // .getConstructor(Integer.TYPE, containers, ichat)
  // .newInstance(windowId, containers.getField("f").get(null),
  // ichat.cast(chatMessage));
  //
  // Object playerConnection =
  // entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);
  //
  // playerConnection.getClass().getMethod("a",
  // getClassFromPath(Path.MINECRAFT_NETWORK_PROTOCOL, "Packet"))
  // .invoke(playerConnection, packet);
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  //
}
