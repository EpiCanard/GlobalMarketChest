package fr.epicanard.globalmarketchest.gui.actions;

import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopInterface;
import fr.epicanard.globalmarketchest.gui.shops.interfaces.*;
import lombok.Getter;

public enum InterfaceType {
  ADMIN_AUCTION_GLOBAL_VIEW(AdminAuctionGlobalView.class),
  AUCTION_GLOBAL_VIEW(AuctionGlobalView.class),
  AUCTION_VIEW_BY_PLAYER(AuctionViewByPlayer.class),
  AUCTION_VIEW_ITEM(AuctionViewItem.class),
  AUCTION_VIEW_LIST(AuctionViewList.class),
  BUY_AUCTION(BuyAuction.class),
  CATEGORY_VIEW(CategoryView.class),
  CONFIRM_VIEW(ConfirmView.class),
  CREATE_AUCTION_ITEM(CreateAuctionItem.class),
  CREATE_AUCTION_PRICE(CreateAuctionPrice.class),
  EDIT_AUCTION(EditAuction.class),
  LAST_AUCTION_VIEW_LIST(LastAuctionViewList.class),
  SEARCH_VIEW(SearchView.class),
  SHOP_CREATION_LINK(ShopCreationLink.class),
  SHOP_CREATION_SELECT_TYPE(ShopCreationSelectType.class),
  SHOP_CREATION_TYPE(ShopCreationType.class),
  SHULKER_BOX_CONTENT(ShulkerBoxContent.class);

  @Getter
  private Class<ShopInterface> clazz;

  @SuppressWarnings("unchecked")
  private <T extends ShopInterface> InterfaceType(Class<T> claz) {
    this.clazz = (Class<ShopInterface>) claz;
  }
}
