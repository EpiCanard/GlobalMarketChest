package fr.epicanard.globalmarketchest.nms;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Nms_v1_21_R2 implements Nms {
  @Override
  public String getMinecraftKey(ItemStack item) {
    return BuiltInRegistries.ITEM.getKey(CraftItemStack.asNMSCopy(item).getItem()).getPath();
  }

  @Override
  public void updateInventoryName(Player player, String name) {
    ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();
    Integer containerId = serverPlayer.containerMenu.containerId;
    Component title = Component.literal(name);
    ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(containerId, MenuType.GENERIC_9x6, title);

    serverPlayer.connection.send(packet);
  }

  @Override
  public ItemStack setNbtTag(ItemStack item) {
    net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
    CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
    if (data != null && data.contains(GMC_ITEM_TAG))
      return item;

    if (data == null) {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean(GMC_ITEM_TAG, true);
      CustomData.set(DataComponents.CUSTOM_DATA, itemStack, tag);
    } else {
      data = data.update(tag -> tag.putBoolean(GMC_ITEM_TAG,  true));
      itemStack.set(DataComponents.CUSTOM_DATA, data);
    }
    return CraftItemStack.asBukkitCopy(itemStack);
  }

  @Override
  public Boolean hasNbtTag(ItemStack item) {
    CustomData data = CraftItemStack.asNMSCopy(item).get(DataComponents.CUSTOM_DATA);
    return data != null && data.contains(GMC_ITEM_TAG);
  }

  @Override
  public ItemStack getItemStack(String minecraftKey) {
    ResourceLocation resourceLocation = ResourceLocation.parse(minecraftKey);
    Item item = BuiltInRegistries.ITEM.getValue(resourceLocation);
    return setNbtTag(CraftItemStack.asNewCraftStack(item));
  }

  @Override
  public String getItemStackDisplayName(ItemStack item) {
    return CraftItemStack.asNMSCopy(item).getDisplayName().getString();
  }
}
