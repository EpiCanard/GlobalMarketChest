package fr.epicanard.globalmarketchest.database.querybuilder;

import java.sql.SQLException;
import java.util.List;

import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

@FunctionalInterface
public interface ExceptionConsumer {
  void accept(List<Object> obj) throws TypeNotSupported, SQLException;
}