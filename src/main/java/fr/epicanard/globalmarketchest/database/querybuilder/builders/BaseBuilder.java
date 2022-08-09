package fr.epicanard.globalmarketchest.database.querybuilder.builders;

import fr.epicanard.globalmarketchest.database.querybuilder.ConditionStructure;
import fr.epicanard.globalmarketchest.database.querybuilder.ExceptionConsumer;
import fr.epicanard.globalmarketchest.database.querybuilder.MultiConditionMap;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseBuilder<T extends BaseBuilder<T>> {
  protected String tableName;
  protected String extension = "";

  BaseBuilder(final String tableName) {
    this.tableName = tableName;
  }

  public T setExtension(final String extension) {
    this.extension = extension;
    return (T) this;
  }

  /**
   * Add extension to existing extension
   *
   * @param add
   */
  public T addExtension(final String add) {
    this.extension += " " + add;
    return (T) this;
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
  protected StringBuilder buildClause(final StringBuilder builder, final String clause, final String sep, final MultiConditionMap map) {
    if (map.size() == 0)
      return builder;
    builder
        .append(" " + clause + " ")
        .append(String.join(" " + sep + " ",
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
  protected StringBuilder buildExtension(final StringBuilder builder) {
    if (!this.extension.isEmpty())
      builder.append(" " + this.extension);
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
  public abstract void prepare(ExceptionConsumer consumer) throws TypeNotSupported, SQLException;

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
