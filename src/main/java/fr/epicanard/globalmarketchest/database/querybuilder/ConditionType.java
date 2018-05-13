package fr.epicanard.globalmarketchest.database.querybuilder;

import lombok.Getter;

/**
 * Enum that define the comparators for condition composition
 */
public enum ConditionType {
  equal("="),
  superior(">"),
  superior_equal(">="),
  inferior("<"),
  inferior_equal("<=")
  ;

  @Getter
  private String character;

  ConditionType(String c) {
    this.character = c;
  }
}