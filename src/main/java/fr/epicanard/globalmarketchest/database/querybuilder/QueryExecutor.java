package fr.epicanard.globalmarketchest.database.querybuilder;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.BaseBuilder;
import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;

public class QueryExecutor {

  /* ======================================
   *          QUERY EXECUTION
   * ======================================
   */

  private void setPrepared(PreparedStatement prepared, List<Object> vals, AtomicInteger inc) throws SQLException, TypeNotSupported {
    for (int i = 0; i < vals.size(); i++) {
      if (setPreparedOneField(prepared, vals.get(i), inc))
        inc.incrementAndGet();
    }
  }

  @SuppressWarnings("unchecked")
  private Boolean setPreparedOneField(PreparedStatement prepared, Object value, AtomicInteger inc) throws SQLException, TypeNotSupported {
    switch (value.getClass().getSimpleName()) {
      case "Optional":
        Optional opt = (Optional)value;
        if (opt.isPresent())
          setPreparedOneField(prepared, opt.get(), inc);
        else
          prepared.setNull(inc.get(), 0);
        break;
      case "ColumnType":
        return false;
      case "String":
        prepared.setString(inc.get(), (String) value);
        break;
      case "Boolean":
        prepared.setBoolean(inc.get(), (Boolean) value);
        break;
      case "Timestamp":
        prepared.setString(inc.get(), value.toString());
        break;
      case "Integer":
        prepared.setInt(inc.get(), (Integer) value);
        break;
      case "Short":
        prepared.setShort(inc.get(), (Short) value);
        break;
      case "Double":
        prepared.setDouble(inc.get(), (Double) value);
        break;
      case "ArrayList":
        inc.decrementAndGet();
        for (Object val : (List<Object>) value) {
          if (val instanceof String)
            prepared.setString(inc.incrementAndGet(), (String) val);
          if (val instanceof Integer)
            prepared.setInt(inc.incrementAndGet(), (Integer) val);
        }
        break;
      default:
        throw new TypeNotSupported(value.getClass().getSimpleName());
    }
    return true;
  }

  /**
   * Prepare and execute the query
   *
   * @param builder BaseBuilder that contains the query
   * @param onSuccess Callback called with the query response
   */
  public <T extends BaseBuilder<T>> Boolean execute(BaseBuilder<T> builder, final SqlConsumer<ResultSet> onSuccess, final Consumer<SQLException> onError) {
    Connection co = GlobalMarketChest.plugin.getSqlConnector().getConnection();
    Boolean ret = false;
    AtomicReference<ResultSet> res = new AtomicReference<>();
    PreparedStatement prepared = null;

    try {
      prepared = co.prepareStatement(builder.build());
      final PreparedStatement preparedCopy = prepared;
      AtomicInteger atomint = new AtomicInteger(1);

      builder.prepare(lst -> {
        this.setPrepared(preparedCopy, lst, atomint);
      });

      ret = builder.execute(prepared, res);

      if (onSuccess != null && res.get() != null) {
        try {
          onSuccess.accept(res.get());
        } catch (SQLException exception) {
          if (onError != null) {
            onError.accept(exception);
          }
        }

      }

    } catch (TypeNotSupported e) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      GlobalMarketChest.plugin.getSqlConnector().closeRessources(res.get(), prepared);
      GlobalMarketChest.plugin.getSqlConnector().getBackConnection(co);
    }
    return ret;
  }

  public <T extends BaseBuilder<T>> Boolean execute(final BaseBuilder<T> builder, final SqlConsumer<ResultSet> onSuccess) {
    return this.execute(builder, onSuccess, null);
  }

  public <T extends BaseBuilder<T>> Boolean execute(BaseBuilder<T> builder) {
    return this.execute(builder, null, null);
  }

  /**
   * Prepare and execute the query
   *
   * @param queries List of String queries to execute inside batch
   */
  public Boolean executeBatches(List<String> queries) {
    Connection co = GlobalMarketChest.plugin.getSqlConnector().getConnection();
    boolean ret = false;
    Statement prepared;

    try {
      prepared = co.createStatement();
      Statement finalPrepared = prepared;
      queries.forEach(query -> {
        try {
          finalPrepared.addBatch(query);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      prepared.executeBatch();
      ret = true;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      GlobalMarketChest.plugin.getSqlConnector().getBackConnection(co);
    }
    return ret;
  }

  public static QueryExecutor of() {
    return new QueryExecutor();
  }
}
