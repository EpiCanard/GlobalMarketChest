package fr.epicanard.globalmarketchest.nms;

import net.minecraft.resources.ResourceLocation;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;

/**
 * Hello world!
 *
 */
public class Nms_1_21 implements Nms
{
    public String getMinecraftKey()
    {
        ResourceLocation resourceLocation = ResourceLocation.parse("minecraft:stone");
        return "Pouet " + resourceLocation.getPath();
    }
}
