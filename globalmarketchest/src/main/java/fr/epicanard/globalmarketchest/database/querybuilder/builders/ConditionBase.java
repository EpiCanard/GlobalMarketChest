package fr.epicanard.globalmarketchest.database.querybuilder.builders;


import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.MultiConditionMap;

public abstract class ConditionBase<T extends ConditionBase<T>> extends BaseBuilder<T> {
  protected MultiConditionMap conditions = new MultiConditionMap();

  ConditionBase(String tableName) {
    super(tableName);
  }

  /**
   * Add a condtion to conditions variable
   */
  public T addCondition(String key, Object value, ConditionType type) {
    this.conditions.put(key, value, type);
    return (T) this;
  }

  /**
   * Add a condtion to conditions variable with default ConditionType to equal
   */
  public T addCondition(String key, Object value) {
    this.conditions.put(key, value, ConditionType.EQUAL);
    return (T) this;
  }

  /**
   * Reset conditions
   */
  public T resetConditions() {
    this.conditions.clear();
    return (T) this;
  }

  /**
   * Default parameters for build clause
   */
  protected StringBuilder buildWhereClause(StringBuilder builder) {
    return this.buildClause(builder, "WHERE", "AND", this.conditions);
  }

}
