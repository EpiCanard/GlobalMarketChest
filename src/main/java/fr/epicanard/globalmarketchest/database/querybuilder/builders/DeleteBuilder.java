package fr.epicanard.globalmarketchest.database.querybuilder.builders;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fr.epicanard.globalmarketchest.database.querybuilder.ExceptionConsumer;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

public class DeleteBuilder extends ConditionBase {
  
  public DeleteBuilder(String tableName) {
    super(tableName);
  }

  /**
   * Build the query
   * 
   * @return query string built
   */
  @Override
  public String build() {
    StringBuilder builder = new StringBuilder("DELETE FROM " + this.tableName);
    this.buildWhereClause(builder);
    return this.buildExtension(builder).toString();
  }

  /**
   * Prepare the query params
   * 
   * @param consumer
   */
  @Override
  public void prepare(ExceptionConsumer<List<Object>> consumer) throws TypeNotSupported, SQLException{
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
    return statement.executeUpdate() >= 1;
  }
}