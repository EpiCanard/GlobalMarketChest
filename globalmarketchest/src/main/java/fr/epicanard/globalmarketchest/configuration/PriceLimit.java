package fr.epicanard.globalmarketchest.configuration;

import fr.epicanard.duckconfig.annotations.Header;

@Header({
  "=================================================",
  "Config that contains all prices range limitation by item",
  "",
  "Parameters:",
  "  Min : Minimum price of item, floating number, the value must be positive",
  "  Max : Maximum price of item, floating number, ",
  "        if the value is positive the item can't be sold to a price higher",
  "        if the value is negative the max price will have no limit",
  "",
  "For example:",
  "  ```",
  "  minecraft:dirt:",
  "    Min: 10",
  "    Max: 1000",
  "  ```",
  "  With this config a player must sell the item with a price between 10 and 1000 included",
  "================================================="
})
public class PriceLimit {
  public Double Min = 0.0;
  public Double Max = -1.0;

  /**
   * Check the validity of values Min and Max
   *
   * @return Return a valid PriceLimit
   */
  public PriceLimit checkValidity() {
    if (this.Min < 0.0) {
      this.Min = 0.0;
    }
    if (this.Max < -1.0 || (this.Max >= 0.0 && this.Max < this.Min)) {
      this.Max = -1.0;
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
    if (price < this.Min) {
      return this.Min;
    } else if (this.Max > 0.0 && price > this.Max) {
      return this.Max;
    }
    return price;
  }
}
