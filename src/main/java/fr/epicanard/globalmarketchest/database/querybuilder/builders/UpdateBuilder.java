package fr.epicanard.globalmarketchest.database.querybuilder.builders;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.ExceptionConsumer;
import fr.epicanard.globalmarketchest.database.querybuilder.MultiConditionMap;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

public class UpdateBuilder extends ConditionBase {
  protected MultiConditionMap values = new MultiConditionMap();

  public UpdateBuilder(String tableName) {
    super(tableName);
  }
  
  /**
   * Add a value to values variable with default ConditionType to equal
   */
  public void addValue(String key, Object value) {
    this.values.put(key, value, ConditionType.EQUAL);
  }

  /**
   * Build the query
   * 
   * @return query string built
   */
  @Override
  public String build() {
    StringBuilder builder = new StringBuilder("UPDATE " + this.tableName);
    this.buildClause(builder, "SET", ", ", this.values);
    this.buildWhereClause(builder);
    return this.buildExtension(builder).toString();
  }

  /**
   * Prepare the query params
   * 
   * @param consumer
   */
  @Override
  public void prepare(ExceptionConsumer<List<Object>> consumer) throws TypeNotSupported, SQLException {
    consumer.accept(this.values.values());    
    consumer.accept(this.conditions.values());    
  }

  /**
   * Execute the query
   * 
   * @param statement
   * @param resultSet
   * 
   * @return return if execution succeed
   */
  @Override
  public Boolean execute(PreparedStatement statement, AtomicReference<ResultSet> resultSet) throws SQLException {
    return statement.executeUpdate() > 0;
  }
}