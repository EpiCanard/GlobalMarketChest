package fr.epicanard.globalmarketchest.database.querybuilder.builders;


import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.MultiConditionMap;

public abstract class ConditionBase extends BaseBuilder {
  protected MultiConditionMap conditions = new MultiConditionMap();
  
  public ConditionBase(String tableName) {
    super(tableName);
  }

  /**
   * Add a condtion to conditions variable
   */
  public void addCondition(String key, Object value, ConditionType type) {
    this.conditions.put(key, value, type);
  }

  /**
   * Add a condtion to conditions variable with default ConditionType to equal
   */
  public void addCondition(String key, Object value) {
    this.conditions.put(key, value, ConditionType.EQUAL);
  }

  /**
   * Default parameters for build clause
   */
  protected StringBuilder buildWhereClause(StringBuilder builder) {
    return this.buildClause(builder, "WHERE", "AND", this.conditions);
  }

}