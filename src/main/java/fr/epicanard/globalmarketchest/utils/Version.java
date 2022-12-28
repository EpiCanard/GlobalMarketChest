package fr.epicanard.globalmarketchest.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Version {
  CURRENT((byte)0, (byte)0),
  V1_12((byte)1, (byte)12),
  V1_13((byte)1, (byte)13),
  V1_14((byte)1, (byte)14),
  V1_15((byte)1, (byte)15),
  V1_16((byte)1, (byte)16),
  V1_17((byte)1, (byte)17),
  V1_18((byte)1, (byte)18),
  V1_19((byte)1, (byte)19);

  private byte major;
  private byte minor;

  private static final Pattern versionRegex = Pattern.compile("(\\d+)[.](\\d+)");

  Version(final byte major, final byte minor) {
    this.major = major;
    this.minor = minor;
  }

  private void updateVersion(final String strVersion) {
    final Matcher matcher = versionRegex.matcher(strVersion);

    if (matcher.find() && matcher.groupCount() >= 2) {
      major = Byte.parseByte(matcher.group(1));
      minor = Byte.parseByte(matcher.group(2));
    }
  }

  public static void initVersion(final String strVersion) {
    Version.CURRENT.updateVersion(strVersion);
  }

  public static boolean isLowerThan(final Version newVersion) {
    return (CURRENT.minor < newVersion.minor && CURRENT.major == newVersion.major) || CURRENT.major < newVersion.major;
  }

  public static boolean isLowerOrEqualsTo(final Version newVersion) {
    return (CURRENT.minor == newVersion.minor && CURRENT.major == newVersion.major)
      || ((CURRENT.minor < newVersion.minor && CURRENT.major == newVersion.major) || CURRENT.major < newVersion.major);
  }

  public static boolean isEqualsTo(final Version newVersion) {
    return CURRENT.minor == newVersion.minor && CURRENT.major == newVersion.major;
  }

  public static boolean isHigherOrEqualsTo(final Version newVersion) {
    return (CURRENT.minor == newVersion.minor && CURRENT.major == newVersion.major)
      || ((CURRENT.minor > newVersion.minor && CURRENT.major == newVersion.major) || CURRENT.major > newVersion.major);
  }

  public static boolean isHigherThan(final Version newVersion) {
    return  (CURRENT.minor > newVersion.minor && CURRENT.major == newVersion.major) || CURRENT.major > newVersion.major;
  }
}
