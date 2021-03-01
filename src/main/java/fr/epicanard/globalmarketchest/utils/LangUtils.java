package fr.epicanard.globalmarketchest.utils;

import fr.epicanard.globalmarketchest.GlobalMarketChest;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility Class to get language translate
 */
public class LangUtils {
  private static Pattern replacePattern = Pattern.compile("\\{([a-zA-Z]+)}");

  /**
   * Get language translate
   *
   * @param path Path to variable inside language file
   * @return Language variable
   */
  public static String get(String path) {
    return LangUtils.getOrElse(path, "MISSING_VAR " + path);
  }

  /**
   * Get language translate or else value if null
   *
   * @param path Path to variable inside language file
   * @param els  Else value
   * @return if string not in config set else value
   */
  public static String getOrElse(String path, String els) {
    return Utils.toColor(GlobalMarketChest.plugin.getConfigLoader().getLanguages().getString(path, els));
  }

  /**
   * Format language variable with map of parameters
   *
   * @param path Path to language variable
   * @param args Map of parameters to map
   * @return Mapped string
   */
  public static String format(final String path, final Map<String, Object> args) {
    return formatString(get(path), args);
  }

  /**
   * Format language variable with map of parameters
   *
   * @param path  Path to language variable
   * @param key   Key to replace
   * @param value Value in replacement for key
   * @return Mapped string
   */
  public static String format(final String path, final String key, final Object value) {
    return formatString(get(path), Collections.singletonMap(key, value));
  }

  /**
   * Format language string with map of parameters
   *
   * @param langStr Language string to format
   * @param args    Map of parameters to map
   * @return Mapped string
   */
  public static String formatString(final String langStr, final Map<String, Object> args) {
    if (args == null || langStr == null) {
      return langStr;
    }
    final Matcher matcher = replacePattern.matcher(langStr);
    final StringBuffer result = new StringBuffer(langStr.length());

    while (matcher.find()) {
      final String key = matcher.group(1);
      matcher.appendReplacement(result, "");
      result.append(args.getOrDefault(key, "{" + key + "}"));
    }
    matcher.appendTail(result);

    return result.toString();
  }
}