package fr.epicanard.globalmarketchest.database.querybuilder;

import lombok.Getter;

public class ColumnType {
  @Getter
  private String value;

  public ColumnType(String value) {
    this.value = value;
  }
}
