package fr.epicanard.globalmarketchest.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Version {
  CURRENT((byte)0, (byte)0),
  V1_12((byte)12, (byte)-1),
  V1_13((byte)13, (byte)-1),
  V1_14((byte)14, (byte)-1),
  V1_15((byte)15, (byte)-1),
  V1_16((byte)16, (byte)-1),
  V1_17((byte)17, (byte)-1),
  V1_18((byte)18, (byte)-1),
  V1_19((byte)19, (byte)-1),
  V1_19_3((byte)19, (byte)3);

  private byte minor;
  private byte patch;

  private static final Pattern versionRegex = Pattern.compile("(\\d+)[.](\\d+)(?:[.](\\d+))?");

  Version(final byte minor, final byte patch) {
    this.minor = minor;
    this.patch = patch;
  }


  public String toStr() {
    return "Version("+ this.minor + ", " + this.patch + ")";
  }

  private void updateVersion(final String strVersion) {
    System.out.println("Update " + strVersion);
    final Matcher matcher = versionRegex.matcher(strVersion);

    if (matcher.find() && matcher.groupCount() >= 2) {
      minor = Byte.parseByte(matcher.group(2));
      String match = matcher.group(3);
      patch = (match != null) ? Byte.parseByte(match) : (byte)0;
    }
  }

  public static void initVersion(final String strVersion) {
    Version.CURRENT.updateVersion(strVersion);
  }

  public static boolean isLowerThan(final Version newVersion) {
    return CURRENT.minor < newVersion.minor  || (CURRENT.minor == newVersion.minor && (newVersion.patch == -1 || CURRENT.patch < newVersion.patch));
  }

  public static boolean isLowerOrEqualsTo(final Version newVersion) {
    return CURRENT.minor < newVersion.minor  || (CURRENT.minor == newVersion.minor && (newVersion.patch == -1 || CURRENT.patch <= newVersion.patch));
  }

  public static boolean isEqualsTo(final Version newVersion) {
    return CURRENT.minor == newVersion.minor && (newVersion.patch == -1 || CURRENT.patch == newVersion.patch);
  }

  public static boolean isHigherOrEqualsTo(final Version newVersion) {
    return CURRENT.minor > newVersion.minor  || (CURRENT.minor == newVersion.minor && (newVersion.patch == -1 || CURRENT.patch >= newVersion.patch));
  }

  public static boolean isHigherThan(final Version newVersion) {
    return CURRENT.minor > newVersion.minor  || (CURRENT.minor == newVersion.minor && (newVersion.patch == -1 || CURRENT.patch > newVersion.patch));
  }
}
