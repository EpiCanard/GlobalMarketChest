package fr.epicanard.globalmarketchest.database.querybuilder;

import fr.epicanard.globalmarketchest.exceptions.TypeNotSupported;

import java.sql.SQLException;
import java.util.List;

@FunctionalInterface
public interface ExceptionConsumer {
  void accept(List<Object> obj) throws TypeNotSupported, SQLException;
}
