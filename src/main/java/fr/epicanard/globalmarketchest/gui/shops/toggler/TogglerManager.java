package fr.epicanard.globalmarketchest.gui.shops.toggler;

import com.google.common.collect.ImmutableList;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import org.bukkit.inventory.Inventory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public final class TogglerManager {
  private final Map<Integer, Toggler> togglers = new HashMap<Integer, Toggler>();
  private final Integer dynamicRow;

  public TogglerManager(final Map<Integer, TogglerConfig> togglersConfig, final Integer dynamicRow) {
    this.dynamicRow = dynamicRow;
    togglersConfig.forEach((pos, config) -> {
      this.togglers.put(pos, config.instanceToggler());
    });
  }

  public void loadTogglers(final Inventory inv) {
    this.togglers.values().stream().filter(Toggler::getIsSet).forEach(toggler -> toggler.load(inv));
  }

  public void unsetTogglers(final Inventory inv, final Integer... positions) {
    for (final Integer pos: positions) {
      this.togglers.get(pos).unset(inv);
    }
  }

  public void setTogglers(final Inventory inv, final Integer... positions) {
    for (final Integer pos: positions) {
      this.togglers.get(pos).set(inv);
    }
  }

  public Toggler get(final Integer pos) {
    return this.togglers.get(pos);
  }

  /**
   * Set a toggler and assign an action to this toggler.
   * If the toggler is affected by a dynamic reordering, it will change real position of toggler.
   *
   * @param pos Id of toggler we want to set
   * @param actions List of actions we want to remap
   * @param togglerAction Action of toggler we want to set
   */
  public void setTogglerWithAction(
      final Inventory inv,
      final Integer pos,
      final Map<Integer, Consumer<InventoryGUI>> actions,
      final Consumer<InventoryGUI> togglerAction) {
    final Integer start = dynamicRow * 9;
    final Integer end = (dynamicRow + 1) * 9;
    if (dynamicRow == -1 || pos < start || pos >= end) {
      this.togglers.get(pos).set(inv);
      actions.put(pos, togglerAction);
    } else {
      this.setDynamicToggler(inv, pos, start, end, actions, togglerAction);
    }
  }

  public Map<Integer, Toggler> getTogglers() {
    return togglers;
  }

  public Integer getDynamicRow() {
    return dynamicRow;
  }

  /**
   * Set the toggler with id @pos and reorganize the line automatically and remap actions
   *
   * @param inv Bukkit inventory used to set toggler
   * @param pos Id of toggler we want to set
   * @param start Start of line
   * @param end End of line
   * @param actions List of actions we want to remap
   * @param togglerAction Action of toggler we want to set
   */
  private void setDynamicToggler(
      final Inventory inv,
      final Integer pos,
      final Integer start,
      final Integer end,
      final Map<Integer, Consumer<InventoryGUI>> actions,
      final Consumer<InventoryGUI> togglerAction
  ) {
    final List<Toggler> toUpdate = this.getDynamicTogglers(pos, start, end);
    final List<Integer> index = this.getDynamicIndex(toUpdate.size());

    if (index == null)
      return;

    for (int i = 0; i < toUpdate.size(); i++) {
      final Toggler toggler = toUpdate.get(i);
      final Integer newPos = start + index.get(i);
      final Consumer<InventoryGUI> action = actions.remove(toggler.getPos());
      actions.put(newPos, toggler.getPos() == pos ? togglerAction : action);
      toggler.setAtPos(inv, newPos);
    }

  }

  private List<Integer> getDynamicIndex(final Integer size) {
    switch (size) {
      case 1: return ImmutableList.of(4);
      case 2: return ImmutableList.of(2, 6);
      case 3: return ImmutableList.of(2, 4, 6);
      case 4: return ImmutableList.of(1, 3, 5, 7);
      case 5: return ImmutableList.of(0, 2, 4, 6, 8);
      case 0:
        LoggerUtils.warn("Not toggler found");
        return null;
      default:
        LoggerUtils.warn("Dynamic toggle above 5 is not handled. Actual: " + size);
        return null;
    }
  }

  private List<Toggler> getDynamicTogglers(final Integer pos, final Integer start, final Integer end) {
    return this.togglers.entrySet().stream()
      .filter(entry -> entry.getKey() == pos || (entry.getValue().isSet && entry.getKey() >= start && entry.getKey() < end))
      .sorted(Comparator.comparingInt(Entry::getKey))
      .map(Entry::getValue)
      .collect(Collectors.toList());
  }
}
