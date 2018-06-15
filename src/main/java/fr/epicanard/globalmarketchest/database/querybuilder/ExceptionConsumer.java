package fr.epicanard.globalmarketchest.database.querybuilder;

import java.sql.SQLException;

import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

@FunctionalInterface
public interface ExceptionConsumer<T> {
  void accept(T obj) throws TypeNotSupported, SQLException;
}