package fr.epicanard.globalmarketchest.auctions;

import fr.epicanard.globalmarketchest.utils.LangUtils;
import lombok.Getter;

public enum StateAuction {
  INPROGRESS(1, "InProgress"),
  EXPIRED(2, "Expired"),
  ABANDONED(3, "Abandoned"),
  FINISHED(4, "Finished")
  ;
  
  @Getter
  private int state;
  private String keyLang;


  StateAuction(int id, String keyLang) {
    this.state = id;
    this.keyLang = keyLang;
  }

  /**
   * Get translate language for state
   * Path inside language file : States.<keyLang>
   * 
   * @return Translated keylang
   */
  public String getLang() {
    return LangUtils.get("States." + this.keyLang);
  }

  /**
   * Static method to get StateAuction enum from int state value
   * if StateAuction is not valid return StateAuction.FINISHED
   * 
   * @param value state value
   * @return StateAuction
   */
  public static final StateAuction getStateAuction(int value) {
    for (StateAuction state : StateAuction.values()) {
      if (state.getState() == value)
        return state;
    }
    return StateAuction.FINISHED;
  }
}
