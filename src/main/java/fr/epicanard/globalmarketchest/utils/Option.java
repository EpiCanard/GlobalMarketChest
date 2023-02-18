package fr.epicanard.globalmarketchest.utils;

import java.util.Optional;
import java.util.function.Predicate;

public class Option {
  static public <T> Boolean exists(Optional<T> opt, Predicate<T> predicate) {
    return opt.filter(predicate).isPresent();
  }
}
