package fr.epicanard.globalmarketchest.utils;

import java.util.logging.Level;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LangUtils {
  public String get(String path) {
    String ret = GlobalMarketChest.plugin.getConfigLoader().getLanguages().getString(path);
    if (ret == null) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, "Missing");
      return "MISSING_VAR";
    }
    return ret;
  }
}