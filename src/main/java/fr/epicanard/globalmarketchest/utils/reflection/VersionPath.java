package fr.epicanard.globalmarketchest.utils.reflection;

import org.bukkit.Bukkit;

public enum VersionPath {
  BUKKIT("org.bukkit.craftbukkit." + version()),
  MINECRAFT_SERVER("net.minecraft.server." + version()),
  MINECRAFT_RESOURCES("net.minecraft.resources"),
  MINECRAFT_WORLD_ITEM("net.minecraft.world.item"),
  MINECRAFT_NETWORK_CHAT("net.minecraft.network.chat"),
  MINECRAFT_NETWORK_GAME("net.minecraft.network.protocol.game"),
  MINECRAFT_NETWORK_PROTOCOL("net.minecraft.network.protocol"),
  MINECRAFT_WORLD_INVENTORY("net.minecraft.world.inventory"),
  MINECRAFT_NBT("net.minecraft.nbt"),
  MINECRAFT_CORE_REGISTRIES("net.minecraft.core.registries"),
  MINECRAFT_CORE("net.minecraft.core");

  final String path;

  VersionPath(String path) {
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
