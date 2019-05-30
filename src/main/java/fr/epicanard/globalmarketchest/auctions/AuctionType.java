package fr.epicanard.globalmarketchest.auctions;

import lombok.Getter;

public enum AuctionType {
  SELL(0),
  BUY(1)
  ;
  
  @Getter
  private Integer type;

  AuctionType(Integer type) {
    this.type = type;
  }

  /**
   * Get an Auction Type with is value
   * 
   * @param value Value of AuctionType
   * @return Auction matching value
   */
  public static final AuctionType getAuctionType(Integer value) {
    if (value != null) {
      for (AuctionType type : AuctionType.values()) {
        if (type.getType() == value)
          return type;
      }
    }
    return AuctionType.SELL;
  }
}
