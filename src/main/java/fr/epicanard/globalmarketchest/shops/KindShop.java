package fr.epicanard.globalmarketchest.shops;

public enum KindShop {
  GLOBALSHOP(1),
  AUCTIONSHOP(2),
  ADMINSHOP(4),
  LOCALSHOP(8)
  ;
  
  private int shopMask;

  KindShop(int id) {
    this.shopMask = id;
  }
  
  /**
   * @deprecated
   * TODELETE
   * Get the id of kind of shop
   * 
   * @return
   */
  public int getId() {
    return this.shopMask;
  }

  /**
   * Define if this KindShop is set on this mask
   * 
   * @param kinds the mask
   * @return
   */
  public Boolean isSetOn(int kinds) {
    return ((kinds & this.shopMask) == this.shopMask);
  }

  /**
   * Add this KindShop to the mask
   * 
   * @param kinds the mask
   * @return the mask
   */
  public int setOn(int kinds) {
    return kinds | this.shopMask;
  }

  /**
   * Remove this KindShop from the mask
   * 
   * @param kind the mask
   * @return the mask
   */
  public int unsetOn(int kinds) {
    return kinds & ~this.shopMask;
  }
}
