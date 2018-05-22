package fr.epicanard.globalmarketchest.database.querybuilder;

import lombok.Getter;

/**
 * Enum that define the comparators for condition composition
 */
public enum ConditionType {
  EQUAL("="),
  SUPERIOR(">"),
  SUPERIOR_EQUAL(">="),
  INFERIOR("<"),
  INFERIOR_EQUAL("<=")
  ;

  @Getter
  private String character;

  ConditionType(String c) {
    this.character = c;
  }
}