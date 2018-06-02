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
    String ret = GlobalMarketChest.plugin.getConfigLoader().getLanguages().getString(path);
    if (ret == null) {
      return "MISSING_VAR";
    }
    return ret;
  }
}