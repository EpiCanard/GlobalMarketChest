package fr.epicanard.globalmarketchest.database.querybuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.BaseBuilder;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

public class QueryExecutor {

  /* ======================================
   *          QUERY EXECUTION
   * ======================================
   */

  @SuppressWarnings("unchecked")
  private void setPrepared(PreparedStatement prepared, List<Object> vals, AtomicInteger inc) throws SQLException, TypeNotSupported {
    for (int i = 0; i < vals.size(); i++) {
      final Object value = vals.get(i);
      switch (value.getClass().getSimpleName()) {
        case "ColumnType":
          continue;
        case "String":
          prepared.setString(inc.get(), (String)value);
          break;
        case "Boolean":
          prepared.setBoolean(inc.get(), (Boolean)value);
          break;
        case "Timestamp":
          prepared.setString(inc.get(), value.toString());
          break;
        case "Integer":
          prepared.setInt(inc.get(), (Integer)value);
          break;
        case "Short":
          prepared.setShort(inc.get(), (Short)value);
          break;
        case "Double":
          prepared.setDouble(inc.get(), (Double)value);
          break;
        case "ArrayList":
          inc.decrementAndGet();
          for(Object val: (List<Object>)value) {
            if (val instanceof String)
              prepared.setString(inc.incrementAndGet(), (String)val);
            if (val instanceof Integer)
              prepared.setInt(inc.incrementAndGet(), (Integer)val);
          }
          break;
        default:
          throw new TypeNotSupported(value.getClass().getSimpleName());
      }
      inc.incrementAndGet();
    }
  }

  /**
   * Prepare and execute the query
   *
   * @param builder
   * @param consumer
   */
  public Boolean execute(BaseBuilder builder, Consumer<ResultSet> consumer) {
    Connection co = GlobalMarketChest.plugin.getSqlConnection().getConnection();
    Boolean ret = false;
    AtomicReference<ResultSet> res = new AtomicReference<>();
    PreparedStatement prepared = null;

    try {
      prepared = co.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
      final PreparedStatement preparedCopy = prepared;
      AtomicInteger atomint = new AtomicInteger(1);

      builder.prepare(lst -> {
        this.setPrepared(preparedCopy, lst, atomint);
      });
      ret = builder.execute(prepared, res);

      if (consumer != null)
        Optional.ofNullable(res.get()).ifPresent(consumer);

    } catch (SQLException e) {
      e.printStackTrace();
    } catch (TypeNotSupported e) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
    } finally {
      GlobalMarketChest.plugin.getSqlConnection().closeRessources(res.get(), prepared);
      GlobalMarketChest.plugin.getSqlConnection().getBackConnection(co);
    }
    return ret;
  }

  public Boolean execute(BaseBuilder builder) {
    return this.execute(builder, null);
  }

  public static QueryExecutor of(){
    return new QueryExecutor();
  }
}