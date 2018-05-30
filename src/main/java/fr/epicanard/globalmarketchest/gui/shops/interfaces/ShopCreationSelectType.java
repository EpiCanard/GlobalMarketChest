package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.paginator.Paginator;
import fr.epicanard.globalmarketchest.gui.shops.ShopCreationInterface;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import fr.epicanard.globalmarketchest.utils.ItemUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;

public class ShopCreationSelectType extends ShopCreationInterface {

  public ShopCreationSelectType(InventoryGUI inv) {
    super(inv);
    this.actions.put(11, i -> this.toggleShop(11, ShopType.GLOBALSHOP));
    this.actions.put(13, i -> this.toggleShop(13, ShopType.AUCTIONSHOP));
    this.actions.put(15, i -> this.toggleShop(15, ShopType.ADMINSHOP));
    this.actions.put(53, new NextInterface("ShopCreationLink"));
    this.paginator.setLoadConsumer(this::loadNearBlock);
    this.paginator.setClickConsumer(this::setOtherLocation);
  }

  private void setGlow(int pos, int mask, ShopType type) {
    ItemUtils.setGlow(this.inv.getInv(), pos + 9, type.isSetOn(mask));
  }

  private void toggleShop(int pos, ShopType type) {
    ShopInfo shop = this.inv.getTransValue("ShopInfo");
    shop.toggleType(type);

    this.setGlow(pos, shop.getType(), type);
    this.updateName();
  }

  private void loadNearBlock(Paginator pag) {
    ShopInfo shop = this.inv.getTransValue("ShopInfo");

    List<Block> blocks = pag.getSubList(WorldUtils.getNearAllowedBlocks(shop.getSignLocation()));
    List<ItemStack> items = blocks.stream().map(block -> new ItemStack(block.getType())).collect(Collectors.toList());
    pag.getItemstacks().clear();
    pag.getItemstacks().addAll(items);
  }

  private void setOtherLocation(int pos) {
    ShopInfo shop = this.inv.getTransValue("ShopInfo");
    List<Block> blocks = this.paginator.getSubList(WorldUtils.getNearAllowedBlocks(shop.getSignLocation()));

    try {
      Block block = blocks.get(pos);

      shop.setOtherLocation(block.getLocation());
      this.updateName();
    } catch(IndexOutOfBoundsException e) {}
  }

  @Override
  public void load() {
    super.load();

    ShopInfo shop = this.inv.getTransValue("ShopInfo");
    this.setGlow(11, shop.getType(), ShopType.GLOBALSHOP);
    this.setGlow(13, shop.getType(), ShopType.AUCTIONSHOP);
    this.setGlow(15, shop.getType(), ShopType.ADMINSHOP);

  }

  @Override
  public void unload() {
  }
}
