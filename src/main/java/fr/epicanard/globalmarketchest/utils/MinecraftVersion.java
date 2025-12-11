package fr.epicanard.globalmarketchest.utils;

import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class MinecraftVersion {
  /**
   * Last Support Version of minecraft for current plugin
   * Prevent loading config issues for versions not fully supported by the plugin
   */
  @Getter
  private static final String lastSupportedVersion = "1.21";

  final private Integer major;
  final private Integer minor;
  final private Integer patch;

  MinecraftVersion(Integer major, Integer minor, Integer patch) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  static public MinecraftVersion parse(String version) {
    Pattern pattern = Pattern.compile("^([0-9]+)\\.([0-9]+)(?:\\.([0-9]+))?.*");
    Matcher matcher = pattern.matcher(version);
    if (matcher.matches()) {
      String major = matcher.group(1);
      String minor = matcher.group(2);
      String patch = matcher.group(3);
      return new MinecraftVersion(Integer.parseInt(major), Integer.parseInt(minor), (patch != null) ?  Integer.parseInt(patch) : 0);
    } else {
      throw new RuntimeException("Unexpected bukkit version : " + version);
    }
  }

  public String fullVersion() {
    return String.format("%d.%d.%d", major, minor, patch);
  }

  public String baseVersion() {
    return String.format("%d.%d", major, minor);
  }

  public boolean isLowerThan(final Integer major, final Integer minor) {
    return (this.minor < minor && this.major == major) || this.major < major;
  }

  public boolean isLowerOrEqualsTo(final Integer major, final Integer minor) {
    return (this.minor == minor && this.major == major) || ((this.minor < minor && this.major == major) || this.major < major);
  }

  public boolean isEqualsTo(final Integer major, final Integer minor) {
    return this.minor == minor && this.major == major;
  }

  public boolean isHigherOrEqualsTo(final Integer major, final Integer minor) {
    return (this.minor == minor && this.major == major) || ((this.minor > minor && this.major == major) || this.major > major);
  }

  public boolean isHigherThan(final Integer major, final Integer minor) {
    return  (this.minor > minor && this.major == major) || this.major > major;
  }

}
