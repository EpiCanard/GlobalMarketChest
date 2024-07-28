package fr.epicanard.globalmarketchest.nms;

import net.minecraft.server.v1_13_R1.ChatMessage;
import net.minecraft.server.v1_13_R1.EntityPlayer;
import net.minecraft.server.v1_13_R1.Item;
import net.minecraft.server.v1_13_R1.MinecraftKey;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import net.minecraft.server.v1_13_R1.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Nms_v1_13_R1 implements Nms {

  @Override
  public String getMinecraftKey(ItemStack item) {
    return Item.REGISTRY.b(CraftItemStack.asNMSCopy(item).getItem()).b(); // not sure
  }

  @Override
  public void updateInventoryName(Player player, String name) {
    EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
    Integer windowId = entityPlayer.activeContainer.windowId;
    Integer size = player.getOpenInventory().getTopInventory().getSize();
    PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(windowId, "minecraft:chest", new ChatMessage(name), size);

    entityPlayer.playerConnection.sendPacket(packet);
    entityPlayer.updateInventory(entityPlayer.activeContainer);
  }

  @Override
  public ItemStack setNbtTag(ItemStack item) {
    net.minecraft.server.v1_13_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(item);
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
    Item item = Item.REGISTRY.get(new MinecraftKey(minecraftKey));
    return setNbtTag(CraftItemStack.asNewCraftStack(item));
  }

  @Override
  public String getItemStackDisplayName(ItemStack item) {
    return CraftItemStack.asNMSCopy(item).getName().toString();
  }
}
