package fr.epicanard.globalmarketchest.database.querybuilder.builders;

import fr.epicanard.globalmarketchest.database.querybuilder.ExceptionConsumer;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class DeleteBuilder extends ConditionBase<DeleteBuilder> {

  private DeleteBuilder(String tableName) {
    super(tableName);
  }

  public static DeleteBuilder of(final String tableName) {
    return new DeleteBuilder(tableName);
  }

  @Override
  public String build() {
    final StringBuilder builder = new StringBuilder("DELETE FROM " + this.tableName);
    this.buildWhereClause(builder);
    return this.buildExtension(builder).toString();
  }

  @Override
  public void prepare(final ExceptionConsumer consumer) throws TypeNotSupported, SQLException{
    consumer.accept(this.conditions.values());
  }

  @Override
  public Boolean execute(final PreparedStatement statement, final AtomicReference<ResultSet> resultSet) throws SQLException {
    return statement.executeUpdate() > 0;
  }
}