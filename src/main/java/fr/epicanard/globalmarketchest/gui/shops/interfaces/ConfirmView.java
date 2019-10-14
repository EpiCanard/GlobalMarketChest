package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.function.Consumer;

import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopInterface;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class ConfirmView extends ShopInterface {
  private Pair<String, Consumer<Boolean>> question;

  public ConfirmView(InventoryGUI inv) {
    super(inv);
    this.question = inv.getTransactionValue(TransactionKey.QUESTION);
    this.actions.put(29, i -> this.leave(i, true));
    this.actions.put(33, i -> this.leave(i, false));

    final Boolean hasReturn = inv.getTransactionValue(TransactionKey.HAS_RETURN);
    if (hasReturn == null || hasReturn) {
      this.togglers.get(0).set();
      this.actions.put(0, new PreviousInterface());
    }
  }

  /**
   * Call the consumer and leave the shop
   *
   * @param i
   * @param value
   */
  private void leave(InventoryGUI i, Boolean value) {
    this.question.getRight().accept(value);
  }

  @Override
  public void load() {
    super.load();
    final ItemStack item = Utils.getButton("Question");
    ItemStackUtils.setItemStackMeta(item, this.question.getLeft(), null);
    inv.getInv().setItem(13, item);
  }

}