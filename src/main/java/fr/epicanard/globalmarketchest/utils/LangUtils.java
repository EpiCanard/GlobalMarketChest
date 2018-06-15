package fr.epicanard.globalmarketchest.utils;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import lombok.experimental.UtilityClass;

/**
 * Utility Class to get language translate
 */
@UtilityClass
public class LangUtils {
  /**
   * Get language translate
   * 
   * @param path Path to variable inside language file
   * @return
   */
  public String get(String path) {
    return LangUtils.getOrElse(path, "MISSING_VAR");
  }

  /**
   * Get language translate or else value if null
   * 
   * @param path Path to variable inside language file
   * @param els Else value
   * @return if string not in config set else value
   */
  public String getOrElse(String path, String els) {
    return Utils.toColor(GlobalMarketChest.plugin.getConfigLoader().getLanguages().getString(path, els));
  }
}