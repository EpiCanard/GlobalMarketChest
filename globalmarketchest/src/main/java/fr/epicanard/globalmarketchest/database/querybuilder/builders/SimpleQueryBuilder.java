package fr.epicanard.globalmarketchest.database.querybuilder.builders;

import fr.epicanard.globalmarketchest.database.querybuilder.ExceptionConsumer;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleQueryBuilder extends BaseBuilder<SimpleQueryBuilder> {
  private final String request;
  private final Boolean asReturn;

  public SimpleQueryBuilder(final String request, Boolean asReturn) {
    super("");
    this.request = request;
    this.asReturn = asReturn;
  }

  @Override
  public String build() {
    return this.request;
  }

  @Override
  public void prepare(ExceptionConsumer consumer) throws TypeNotSupported, SQLException {}

  @Override
  public Boolean execute(final PreparedStatement statement, final AtomicReference<ResultSet> resultSet) throws SQLException {
    if (asReturn) {
      resultSet.set(statement.executeQuery());
    }
    return true;
  }
}
