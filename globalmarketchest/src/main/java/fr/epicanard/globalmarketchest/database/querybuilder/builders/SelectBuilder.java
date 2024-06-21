package fr.epicanard.globalmarketchest.database.querybuilder.builders;

import fr.epicanard.globalmarketchest.database.querybuilder.ExceptionConsumer;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SelectBuilder extends ConditionBase<SelectBuilder> {
  private List<String> fields = new ArrayList<>();

  private SelectBuilder(final String tableName) {
    super(tableName);
  }

  public static SelectBuilder of(final String tableName) {
    return new SelectBuilder(tableName);
  }


  /**
   * Add field to select
   *
   * @param field Field to add
   */
  public SelectBuilder addField(final String field) {
    this.fields.add(field);
    return this;
  }

  @Override
  public String build() {
    final StringBuilder builder = new StringBuilder("SELECT ");
    builder
        .append((this.fields.isEmpty()) ? "*" : String.join(", ", this.fields))
        .append(" FROM ")
        .append(this.tableName);
    this.buildWhereClause(builder);
    return this.buildExtension(builder).toString();
  }

  @Override
  public void prepare(final ExceptionConsumer consumer) throws TypeNotSupported, SQLException {
    consumer.accept(this.conditions.values());
  }

  @Override
  public Boolean execute(final PreparedStatement statement, final AtomicReference<ResultSet> resultSet) throws SQLException {
    resultSet.set(statement.executeQuery());
    return true;
  }
}
