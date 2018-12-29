package fr.epicanard.globalmarketchest.auctions;

import lombok.Getter;

public enum AuctionLoreConfig {
  ALL(true, true, true, true, true, true, true, true, true),
  TOSELL(false, true, true, true, true, false, true, false, true),
  OWN(true, true, true, true, false, false, true, false, true),
  SOLD(true, true, true, true, false, true, true, true, false),
  BOUGHT(true, true, true, true, true, false, true, true, false),
  OWNENDED(true, true, true, true, false, false, true, true, false),
  ;

  @Getter
  private Boolean state;
  @Getter
  private Boolean quantity;
  @Getter
  private Boolean unitPrice;
  @Getter
  private Boolean totalPrice;
  @Getter
  private Boolean starter;
  @Getter
  private Boolean ender;
  @Getter
  private Boolean started;
  @Getter
  private Boolean ended;
  @Getter
  private Boolean expire;


  AuctionLoreConfig(
    Boolean state,
    Boolean quantity,
    Boolean unitPrice,
    Boolean totalPrice,
    Boolean starter,
    Boolean ender,
    Boolean started,
    Boolean ended,
    Boolean expire
  ) {
    this.state = state;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.totalPrice = totalPrice;
    this.starter = starter;
    this.ender = ender;
    this.started = started;
    this.ended = ended;
    this.expire = expire;
  }
}