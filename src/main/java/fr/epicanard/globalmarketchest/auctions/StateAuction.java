package fr.epicanard.globalmarketchest.auctions;

import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;

public enum StateAuction {
  INPROGRESS("InProgress"),
  EXPIRED("Expired"),
  ABANDONED("Abandoned"),
  FINISHED("Finished")
  ;
  
  private String keyLang;


  StateAuction(String keyLang) {
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
  public static final StateAuction getStateAuction(AuctionInfo auction) {
    if (auction.getEnded() == null)
      return StateAuction.INPROGRESS;    
    if (auction.getEnded() == false && auction.getEnd().getTime() < DatabaseUtils.getTimestamp().getTime())
      return StateAuction.EXPIRED;
    if (auction.getEnded() == false)
      return StateAuction.INPROGRESS;
    if (auction.getEnded() == true && auction.getPlayerStarter() == auction.getPlayerEnder())
      return StateAuction.ABANDONED;
    return StateAuction.FINISHED;
  }
}
