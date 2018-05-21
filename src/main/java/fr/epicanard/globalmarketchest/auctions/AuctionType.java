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

  public static final AuctionType getAuctionType(int value) {
    for (AuctionType type : AuctionType.values()) {
      if (type.getType() == value)
        return type;
    }
    return AuctionType.SELL;
  }
}
