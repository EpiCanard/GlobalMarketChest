package fr.epicanard.globalmarketchest.database.querybuilder;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlConsumer<T> {
  void accept(T obj) throws SQLException;
}