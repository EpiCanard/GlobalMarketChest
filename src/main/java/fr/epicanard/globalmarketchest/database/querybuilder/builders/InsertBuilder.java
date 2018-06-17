package fr.epicanard.globalmarketchest.database.querybuilder.builders;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import fr.epicanard.globalmarketchest.database.querybuilder.ConditionType;
import fr.epicanard.globalmarketchest.database.querybuilder.ExceptionConsumer;
import fr.epicanard.globalmarketchest.database.querybuilder.MultiConditionMap;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;

public class InsertBuilder extends BaseBuilder {
  protected MultiConditionMap values = new MultiConditionMap();

  public InsertBuilder(String tableName) {
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
    StringBuilder builder = new StringBuilder("INSERT INTO " + this.tableName + " (");
    List<String> keys = this.values.keys().stream().distinct().map(e -> "`" + e + "`").collect(Collectors.toList());

    builder.append(String.join(", ", keys));
    String repeat = "(" + DatabaseUtils.joinRepeat("?", ",", keys.size()) + ")";
    builder.append(") VALUES " + DatabaseUtils.joinRepeat(repeat, ",", this.values.values().size() / keys.size()));

    return builder.toString();
  }

  /**
   * Prepare the query params
   * 
   * @param consumer
   */
  @Override
  public void prepare(ExceptionConsumer<List<Object>> consumer) throws TypeNotSupported, SQLException {
    consumer.accept(this.values.values());    
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
    Boolean ret = statement.executeUpdate() >= 1;
    resultSet.set(statement.getGeneratedKeys());
    return ret;
  }
}