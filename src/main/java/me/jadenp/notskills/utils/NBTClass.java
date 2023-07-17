package me.jadenp.notskills.utils;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class NBTClass {

    public static ItemStack addNBTToItem(ItemStack item, String nbtData){
        try {
            NBTItem nbti = new NBTItem(item);
            ReadWriteNBT nbt = NBT.parseNBT(nbtData);
            nbti.mergeCompound(nbt);

            item = nbti.getItem();
        } catch (NbtApiException e) {
            Bukkit.getLogger().warning("[NotSkills] NBT Error!");
        }
        return item;
    }
}
