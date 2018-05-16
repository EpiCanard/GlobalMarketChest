package fr.epicanard.globalmarketchest.database.querybuilder;

import lombok.Getter;

public enum QueryType {
  SELECT("SELECT"),
  INSERT("INSERT"),
  UPDATE("UPDATE"),
  DELETE("DELETE");

  @Getter
  private String start;

  QueryType(String start) {
    this.start = start;
  }

  public static QueryType getQueryType(String query) {
    String st = query.substring(0, 6);
  
    for (QueryType type : QueryType.values()) {
      if (st.compareTo(type.getStart()) == 0)
        return type;
    }
    return QueryType.SELECT;
  }
}