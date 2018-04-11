package fr.epicanard.globalmarketchest.Shops;

public enum KindShop {
  GLOBALSHOP(1),
  AUCTIONSHOP(2),
  ADMINSHOP(3),
  LOCALSHOP(4)
  ;
  
  private int kindId;

  KindShop(int id) {
    this.kindId = id;
  }
  
  public int getId() {
    return this.kindId;
  }
}
