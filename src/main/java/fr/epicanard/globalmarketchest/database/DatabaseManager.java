package fr.epicanard.globalmarketchest.database;

import fr.epicanard.globalmarketchest.database.querybuilder.builders.DeleteBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.InsertBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.SelectBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.UpdateBuilder;

public abstract class DatabaseManager {
  private final String tableName;

  public DatabaseManager(final String tableName) {
    this.tableName = tableName;
  }

  /**
   * Create SelectBuilder with class tableName
   */
  protected SelectBuilder select() {
    return SelectBuilder.of(this.tableName);
  }

  /**
   * Create UpdateBuilder with class tableName
   */
  protected UpdateBuilder update() {
    return UpdateBuilder.of(this.tableName);
  }

  /**
   * Create InsertBuilder with class tableName
   */
  protected InsertBuilder insert() {
    return InsertBuilder.of(this.tableName);
  }

  /**
   * Create DeleteBuilder with class tableName
   */
  protected DeleteBuilder delete() {
    return DeleteBuilder.of(this.tableName);
  }

}
