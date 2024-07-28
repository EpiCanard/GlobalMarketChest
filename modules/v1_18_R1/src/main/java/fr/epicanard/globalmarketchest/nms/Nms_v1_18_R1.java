package fr.epicanard.globalmarketchest.nms;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Nms_v1_18_R1 implements Nms {
  @Override
  public String getMinecraftKey(ItemStack item) {
    return Registry.ITEM.getKey(CraftItemStack.asNMSCopy(item).getItem()).getPath();
  }

  @Override
  public void updateInventoryName(Player player, String name) {
    ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();
    Integer containerId = serverPlayer.containerMenu.containerId;
    TranslatableComponent title = new TranslatableComponent(name);
    ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(containerId, MenuType.GENERIC_9x6, title);

    serverPlayer.connection.send(packet);
  }

  @Override
  public ItemStack setNbtTag(ItemStack item) {
    net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
    CompoundTag tag = itemStack.getTag();

    if (tag != null && tag.contains(GMC_ITEM_TAG))
      return item;

    if (tag == null)
      tag = new CompoundTag();
    tag.putBoolean(GMC_ITEM_TAG, true);
    itemStack.setTag(tag);
    return CraftItemStack.asBukkitCopy(itemStack);
  }

  @Override
  public Boolean hasNbtTag(ItemStack item) {
    CompoundTag tag = CraftItemStack.asNMSCopy(item).getTag();
    return tag != null && tag.contains(GMC_ITEM_TAG);
  }

  @Override
  public ItemStack getItemStack(String minecraftKey) {
    ResourceLocation resourceLocation = new ResourceLocation(minecraftKey);
    Item item = Registry.ITEM.get(resourceLocation);
    return setNbtTag(CraftItemStack.asNewCraftStack(item));
  }

  @Override
  public String getItemStackDisplayName(ItemStack item) {
    return CraftItemStack.asNMSCopy(item).getDisplayName().getString();
  }
}
