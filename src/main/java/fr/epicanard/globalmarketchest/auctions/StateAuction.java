package fr.epicanard.globalmarketchest.auctions;

import lombok.Getter;

public enum StateAuction {
  INPROGRESS(1, "InProgress"),
  EXPIRED(2, "Expired"),
  ABANDONED(3, "Abandoned"),
  FINISHED(4, "Finished")
  ;
  
  @Getter
  private int state;
  @Getter
  private String keyLang;


  StateAuction(int id, String keyLang) {
    this.state = id;
    this.keyLang = keyLang;
  }
}
