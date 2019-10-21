package fr.epicanard.globalmarketchest.database.querybuilder.builders;

import fr.epicanard.globalmarketchest.database.querybuilder.ExceptionConsumer;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleQueryBuilder extends BaseBuilder {
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
  public void prepare(ExceptionConsumer<List<Object>> consumer) throws TypeNotSupported, SQLException {}

  @Override
  public Boolean execute(PreparedStatement statement, AtomicReference<ResultSet> resultSet) throws SQLException {
    if (asReturn) {
      resultSet.set(statement.executeQuery());
    }
    return true;
  }
}
