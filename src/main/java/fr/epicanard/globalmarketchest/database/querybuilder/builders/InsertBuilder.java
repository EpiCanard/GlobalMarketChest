package fr.epicanard.globalmarketchest.database.querybuilder.builders;

import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.ExceptionConsumer;
import fr.epicanard.globalmarketchest.database.querybuilder.MultiConditionMap;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static fr.epicanard.globalmarketchest.utils.DatabaseUtils.joinRepeat;

public class InsertBuilder extends BaseBuilder<InsertBuilder> {
  protected MultiConditionMap values = new MultiConditionMap();

  private InsertBuilder(final String tableName) {
    super(tableName);
  }

  public static InsertBuilder of(final String tableName) {
    return new InsertBuilder(tableName);
  }

  /**
   * Add a value to values variable with default ConditionType to equal
   */
  public InsertBuilder addValue(String key, Object value) {
    this.values.put(key, value, ConditionType.EQUAL);
    return this;
  }

  @Override
  public String build() {
    final StringBuilder builder = new StringBuilder("INSERT INTO " + this.tableName + " (");
    final List<String> keys = this.values.keys().stream().distinct().map(e -> "`" + e + "`").collect(Collectors.toList());

    final String repeat = "(" + joinRepeat("?", ",", keys.size()) + ")";
    builder
        .append(String.join(", ", keys))
        .append(") VALUES ")
        .append(joinRepeat(repeat, ",", this.values.values().size() / keys.size()));

    return builder.toString();
  }

  @Override
  public void prepare(final ExceptionConsumer consumer) throws TypeNotSupported, SQLException {
    consumer.accept(this.values.values());
  }

  @Override
  public Boolean execute(final PreparedStatement statement, final AtomicReference<ResultSet> resultSet) throws SQLException {
    final Boolean ret = statement.executeUpdate() > 0;
    resultSet.set(statement.getGeneratedKeys());
    return ret;
  }
}