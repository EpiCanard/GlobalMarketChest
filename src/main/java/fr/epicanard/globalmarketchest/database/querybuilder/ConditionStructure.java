package fr.epicanard.globalmarketchest.database.querybuilder;

import java.util.List;

import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
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

  @SuppressWarnings("unchecked")
  public String build() {
    String base = "`" + this.key + "` " + this.type.getCharacter();
    if (this.type == ConditionType.IN)
      return base + " (" + DatabaseUtils.joinRepeat("?", ",", ((List<String>)this.value).size()) + ")";
    if (this.value instanceof ColumnType)
      return base + " `" + ((ColumnType)this.value).getValue() + "`";
    return base + " ?";
  }
}