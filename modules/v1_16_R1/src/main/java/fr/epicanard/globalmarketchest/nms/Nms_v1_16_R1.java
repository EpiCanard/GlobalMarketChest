package fr.epicanard.globalmarketchest.nms;

import net.minecraft.server.v1_16_R1.ChatMessage;
import net.minecraft.server.v1_16_R1.Containers;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.IRegistry;
import net.minecraft.server.v1_16_R1.Item;
import net.minecraft.server.v1_16_R1.MinecraftKey;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import net.minecraft.server.v1_16_R1.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Nms_v1_16_R1 implements Nms {

  @Override
  public String getMinecraftKey(ItemStack item) {
    return IRegistry.ITEM.getKey(CraftItemStack.asNMSCopy(item).getItem()).toString();
  }

  @Override
  public void updateInventoryName(Player player, String name) {
    EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
    Integer windowId = entityPlayer.activeContainer.windowId;
    PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(windowId, Containers.GENERIC_9X6, new ChatMessage(name));

    entityPlayer.playerConnection.sendPacket(packet);
    entityPlayer.updateInventory(entityPlayer.activeContainer);
  }

  @Override
  public ItemStack setNbtTag(ItemStack item) {
    net.minecraft.server.v1_16_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
    NBTTagCompound tag = nmsItemStack.getTag();

    if (tag != null && tag.hasKey(GMC_ITEM_TAG))
      return item;

    if (tag == null)
      tag = new NBTTagCompound();
    tag.setBoolean(GMC_ITEM_TAG, true);
    nmsItemStack.setTag(tag);

    return CraftItemStack.asBukkitCopy(nmsItemStack);
  }

  @Override
  public Boolean hasNbtTag(ItemStack item) {
    NBTTagCompound tag = CraftItemStack.asNMSCopy(item).getTag();
    return tag != null && tag.hasKey(GMC_ITEM_TAG);
  }

  @Override
  public ItemStack getItemStack(String minecraftKey) {
    Item item = IRegistry.ITEM.get(new MinecraftKey(minecraftKey));
    return setNbtTag(CraftItemStack.asNewCraftStack(item));
  }

  @Override
  public String getItemStackDisplayName(ItemStack item) {
    return CraftItemStack.asNMSCopy(item).getName().toString();
  }
}
