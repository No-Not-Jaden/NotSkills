package me.jadenp.notskills;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import me.jadenp.notskills.utils.Items;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MythicMobDrop implements IItemDrop {
    private ItemStack item;

    public MythicMobDrop(MythicLineConfig config, String argument) {
        String str = config.getString(new String[] {"type", "t"}, "STONE", argument);


        if (str.equalsIgnoreCase("artifact"))
            item = Items.skillSlotArtifact;
        else 
            item = new ItemStack(Material.valueOf(str.toUpperCase()), 1);
        
    }

    @Override
    public AbstractItemStack getDrop(DropMetadata data, double amount) {
        item.setAmount((int) amount);
        return new BukkitItemStack(item);
    }
}
