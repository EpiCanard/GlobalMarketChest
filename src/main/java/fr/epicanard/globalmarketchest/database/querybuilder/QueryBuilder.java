package fr.epicanard.globalmarketchest.database.querybuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.MultiConditionMap;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

public class QueryBuilder {
  MultiConditionMap conditions = new MultiConditionMap();
  MultiConditionMap values = new MultiConditionMap();
  String tableName;

  public QueryBuilder(String tableName) {
    this.tableName = tableName;
  }

  /**
   * Reset the conditions and values
   */
  public void reset() {
    this.conditions.clear();
    this.values.clear();
  }

  /* ======================================
   *          SET COMPOSITION VAR
   * ======================================
   */

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
    this.conditions.put(key, value, ConditionType.equal);
  }

  /**
   * Add a value to values variable with default ConditionType to equal
   */
  public void addValue(String key, Object value) {
    this.values.put(key, value, ConditionType.equal);
  }

  /* ======================================
   *          QUERY EXECUTION
   * ======================================
   */

  public void setPrepared(PreparedStatement prepared, MultiConditionMap map, int inc) throws SQLException, TypeNotSupported {
    List<Object> values = map.values();

    for (int i = 0; i < values.size(); i++) {
      final Object value = values.get(i);
      switch (value.getClass().getSimpleName()) {
        case "String":
          prepared.setString(i + inc + 1, (String)value);
          break;
        case "Integer":
          prepared.setInt(i + inc + 1, (Integer)value);
          break;
        case "Double":
          prepared.setDouble(i + inc + 1, (Double)value);
          break;
        default:
          throw new TypeNotSupported(value.getClass().getSimpleName());
      }
    }
  }

  /**
   * Prepare and execute the query
   */
  private Object execute(String query, boolean select, boolean values) {
    Connection co = GlobalMarketChest.plugin.getSqlConnection().getConnection();
    Object res = null;

    try {
      PreparedStatement prepared = co.prepareStatement(query);
      if (values == true)
        this.setPrepared(prepared, this.values, 0);
      this.setPrepared(prepared, this.conditions, this.values.size());
      if (select)
        res = prepared.executeQuery();
      else
        res = prepared.executeUpdate();

      GlobalMarketChest.plugin.getSqlConnection().closeRessources(null, prepared);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (TypeNotSupported e) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
    }
    GlobalMarketChest.plugin.getSqlConnection().getBackConnection(co);
    return res;
  }

  public Object execute(String query) {
    final String start = query.substring(0, 6);
    final Boolean select = start.compareTo("SELECT") == 0;
    final Boolean value = start.compareTo("INSERT") == 0 || start.compareTo("UPDATE") == 0;
    return this.execute(query, select, value);
  }

  /* ======================================
   *          QUERY BUILD
   * ======================================
   */

  /**
   * Build a clause 
   */
  private StringBuilder buildClause(StringBuilder builder, String clause, String sep, MultiConditionMap map) {
    if (map.size() == 0)
      return builder;
    builder.append(" " + clause + " ");
    builder.append(String.join(" " + sep + " ",
    map.getMap().stream()
    .map(ConditionStructure::build)
    .toArray(String[]::new)));

    return builder;
  }

  /**
   * Default parameters for build clause
   */
  private StringBuilder buildWhereClause(StringBuilder builder) {
    return this.buildClause(builder, "WHERE", "AND", this.conditions);
  }

  /**
   * Generate SQL String for SELECT
   * 
   * @return String
   */
  public String select() {
    StringBuilder builder = new StringBuilder("SELECT * FROM " + this.tableName);
    return this.buildWhereClause(builder).toString();
  }

  /**
   * Generate SQL String for INSERT
   * 
   * @return String
   */
  public String insert() {
    StringBuilder query = new StringBuilder("INSERT INTO " + this.tableName + " (");

    query.append(String.join(",", this.values.keys()));
    query.append(") VALUES (" + StringUtils.repeat("?,", this.values.keys().size()));
    query.deleteCharAt(query.length() - 1);
    query.append(")");

    return query.toString();
  }

  /**
   * Generate SQL String for UPDATE
   * 
   * @return String
   */
  public String update() {
    StringBuilder builder = new StringBuilder("UPDATE " + this.tableName);
    this.buildClause(builder, "SET", ",", this.values);
    return this.buildWhereClause(builder).toString();
  }

  /**
   * Generate SQL String for DELETE
   * 
   * @return String
   */
  public String delete() {
    StringBuilder builder = new StringBuilder("DELETE FROM " + this.tableName);
    return this.buildWhereClause(builder).toString();
  }

}