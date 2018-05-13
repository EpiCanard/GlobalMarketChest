package fr.epicanard.globalmarketchest.database.querybuilder;

import lombok.Getter;

/**
 * Structure used with MultiConditionMap that store the key de value and the ConditionType
 */
public class ConditionStructure {
  @Getter
  private final String key;
  @Getter
  private final Object value;
  @Getter
  private final ConditionType type;

  public ConditionStructure(String key, Object value, ConditionType type) {
    this.key = key;
    this.value = value;
    this.type = type;
  }

  public String build() {
    return this.key + " " + this.type.getCharacter() + " ?";
  }
}