package fr.epicanard.globalmarketchest.database.querybuilder.builders;

import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.ExceptionConsumer;
import fr.epicanard.globalmarketchest.database.querybuilder.MultiConditionMap;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class UpdateBuilder extends ConditionBase<UpdateBuilder> {
  protected MultiConditionMap values = new MultiConditionMap();

  private UpdateBuilder(final String tableName) {
    super(tableName);
  }

  public static UpdateBuilder of(final String tableName) {
    return new UpdateBuilder(tableName);
  }

  /**
   * Add a value to values variable with default ConditionType to equal
   */
  public UpdateBuilder addValue(final String key, final Object value) {
    this.values.put(key, value, ConditionType.EQUAL);
    return this;
  }

  /**
   * Reset values
   */
  public UpdateBuilder resetValues() {
    this.values.clear();
    return this;
  }

  @Override
  public String build() {
    final StringBuilder builder = new StringBuilder("UPDATE " + this.tableName);
    this.buildClause(builder, "SET", ", ", this.values);
    this.buildWhereClause(builder);
    return this.buildExtension(builder).toString();
  }

  @Override
  public void prepare(final ExceptionConsumer consumer) throws TypeNotSupported, SQLException {
    consumer.accept(this.values.values());
    consumer.accept(this.conditions.values());
  }

  @Override
  public Boolean execute(final PreparedStatement statement, final AtomicReference<ResultSet> resultSet) throws SQLException {
    return statement.executeUpdate() > 0;
  }
}