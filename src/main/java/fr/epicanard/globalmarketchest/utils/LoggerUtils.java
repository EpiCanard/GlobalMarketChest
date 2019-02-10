package fr.epicanard.globalmarketchest.utils;

import java.util.logging.Level;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoggerUtils {
  public void warn(String msg) {
    GlobalMarketChest.plugin.getLogger().log(Level.WARNING, msg);
  }

  public void info(String msg) {
    GlobalMarketChest.plugin.getLogger().log(Level.INFO, msg);
  }
}