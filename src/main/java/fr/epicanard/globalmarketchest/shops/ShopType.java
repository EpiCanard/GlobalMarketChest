package fr.epicanard.globalmarketchest.shops;

public enum ShopType {
  GLOBALSHOP(1),
  AUCTIONSHOP(2),
  ADMINSHOP(4),
  LOCALSHOP(8)
  ;
  
  private int shopMask;

  ShopType(int id) {
    this.shopMask = id;
  }

  /**
   * Define if this ShopType is set on this mask
   * 
   * @param types the mask
   * @return
   */
  public Boolean isSetOn(int types) {
    return ((types & this.shopMask) == this.shopMask);
  }

  /**
   * Add this ShopType to the mask
   * 
   * @param types the mask
   * @return the mask
   */
  public int setOn(int types) {
    return types | this.shopMask;
  }

  /**
   * Remove this ShopType from the mask
   * 
   * @param type the mask
   * @return the mask
   */
  public int unsetOn(int types) {
    return types & ~this.shopMask;
  }

  /**
   * Toggle this ShopType from the mask
   * 
   * @param type the mask
   * @return the mask
   */
  public int toggle(int types) {
    return (this.isSetOn(types)) ? this.unsetOn(types) : this.setOn(types);
  }
}
