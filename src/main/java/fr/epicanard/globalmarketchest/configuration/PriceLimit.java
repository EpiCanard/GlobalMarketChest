package fr.epicanard.globalmarketchest.configuration;

import java.util.Arrays;
import java.util.List;

public class PriceLimit {
  public Double min;
  public Double max;

  public final static List<String> header = Arrays.asList(
    "=================================================",
    "Config that contains all prices range limitation by item",
    "",
    "Parameters:",
    "  min : Minimum price of item, floating number, the value must be positive",
    "  max : Maximum price of item, floating number, ",
    "        if the value is positive the item can't be sold to a price higher",
    "        if the value is negative the max price will have no limit",
    "",
    "For example:",
    "  ```",
    "  minecraft:dirt:",
    "    min: 10",
    "    max: 1000",
    "  ```",
    "  With this config a player must sell the item with a price between 10 and 1000 included",
    "================================================="
  );

  public PriceLimit(Double min, Double max) {
    this.min = min;
    this.max = max;
  }

  public PriceLimit() {
    this.min = 0.0;
    this.max = -1.0;
  }

  /**
   * Check the validity of values min and max
   *
   * @return Return a valid PriceLimit
   */
  public PriceLimit checkValidity() {
    if (this.min < 0.0) {
      this.min = 0.0;
    }
    if (this.max < -1.0 || (this.max >= 0.0 && this.max < this.min)) {
      this.max = -1.0;
    }
    return this;
  }

  /**
   * Validate that a price is inside limits
   *
   * @param price Price to validate
   * @return New valid price
   */
  public double validatePrice(final double price) {
    if (price < this.min) {
      return this.min;
    } else if (this.max > 0.0 && price > this.max) {
      return this.max;
    }
    return price;
  }
}
