package fr.epicanard.globalmarketchest.auctions;

import java.util.ArrayList;
import java.util.List;

public class AuctionGroup {
  private List<AuctionInfo> auctions = new ArrayList<>();

  public AuctionGroup() {}

  public AuctionGroup(AuctionInfo auction) {
    this.auctions.add(auction);
  }

  /**
   * Get the first element if the auctions list
   */
  public AuctionInfo getAuction() {
    return (this.auctions.size() > 0) ? this.auctions.get(0) : null;
  }

  /**
   * Get number of auctions
   *
   * @return Auction number
   */
  public Integer getAuctionNumber() {
    return this.auctions.size();
  }

  /**
   * Add an auction inside group of auctions
   *
   * @param auction Auction to add inside group
   */
  public void addAuction(AuctionInfo auction) {
    this.auctions.add(auction);
  }

  /**
   * Pop the first element of the auctions list
   */
  public void pop() {
    if (this.auctions.size() > 0)
      this.auctions.remove(0);
  }

}