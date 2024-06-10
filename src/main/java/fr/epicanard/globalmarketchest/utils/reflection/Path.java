package fr.epicanard.globalmarketchest.utils.reflection;

import org.bukkit.Bukkit;

public enum Path {
  BUKKIT("org.bukkit.craftbukkit." + version()),
  MINECRAFT_SERVER("net.minecraft.server." + version()),
  MINECRAFT_RESOURCES("net.minecraft.resources"),
  MINECRAFT_WORLD_ITEM("net.minecraft.world.item"),
  MINECRAFT_WORLD_ITEM_COMPONENT("net.minecraft.world.item.component"),
  MINECRAFT_NETWORK_CHAT("net.minecraft.network.chat"),
  MINECRAFT_NETWORK_GAME("net.minecraft.network.protocol.game"),
  MINECRAFT_NETWORK_PROTOCOL("net.minecraft.network.protocol"),
  MINECRAFT_WORLD_INVENTORY("net.minecraft.world.inventory"),
  MINECRAFT_WORLD_ENTITY_PLAYER("net.minecraft.world.entity.player"),
  MINECRAFT_NBT("net.minecraft.nbt"),
  MINECRAFT_CORE_REGISTRIES("net.minecraft.core.registries"),
  MINECRAFT_CORE_COMPONENT("net.minecraft.core.component"),
  MINECRAFT_CORE("net.minecraft.core");

  final String path;

  Path(String path) {
    this.path = path;
  }

  private static String version() {
    return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  }

  public Class<?> getClass(String className) throws ClassNotFoundException {
    return Class.forName(this.getPath(className));
  }

  public String getPath(String className) {
    return this.path + "." + className;
  }
}
