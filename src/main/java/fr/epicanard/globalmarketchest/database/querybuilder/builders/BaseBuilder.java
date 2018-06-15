package fr.epicanard.globalmarketchest.database.querybuilder.builders;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fr.epicanard.globalmarketchest.database.querybuilder.ConditionStructure;
import fr.epicanard.globalmarketchest.database.querybuilder.ExceptionConsumer;
import fr.epicanard.globalmarketchest.database.querybuilder.MultiConditionMap;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;
import lombok.Setter;

public abstract class BaseBuilder {
  protected String tableName;
  @Setter
  protected String extension;

  public BaseBuilder(String tableName) {
    this.tableName = tableName;
  }

  /**
   * Get the extension with a default empty string if null
   * 
   * @return
   */
  public String getExtension() {
    return (this.extension == null) ? "" : this.extension;
  }

  /**
   * Build a clause
   * 
   * @param builder
   * @param clause
   * @param sep
   * @param map
   * 
   * @return return the param builder
   */
  protected StringBuilder buildClause(StringBuilder builder, String clause, String sep, MultiConditionMap map) {
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
   * Build the extension
   * 
   * @param builder string builder to use
   * @return return the param builder
   */
  protected StringBuilder buildExtension(StringBuilder builder) {
    if (this.extension != null)
      builder.append(this.extension);
    return builder;
  }

  /**
   * Build the query
   * 
   * @return query string built
   */
  public abstract String build();

  /**
   * Prepare the query params
   * 
   * @param consumer
   */
  public abstract void prepare(ExceptionConsumer<List<Object>> consumer) throws TypeNotSupported, SQLException;

  /**
   * Execute the query
   * 
   * @param statement
   * @param resultSet
   * 
   * @return return if execution succeed
   */
  public abstract Boolean execute(PreparedStatement statement, AtomicReference<ResultSet> resultSet) throws SQLException;
}