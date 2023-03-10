package me.jadenp.notskills;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Items {

    public static ItemStack skillSlotArtifact = new ItemStack(Material.PAPER);

    public static void addMaterialData(){
        ItemMeta meta = skillSlotArtifact.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GOLD + "Skill Slot Artifact");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Ancient Technology used to further");
        lore.add(ChatColor.GRAY + "the capabilities of modern weapons");
        lore.add("");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        skillSlotArtifact.setItemMeta(meta);
        skillSlotArtifact.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

    }



    public static void givePlayer(Player player, ItemStack itemStack){
       for (Map.Entry<Integer, ItemStack> entry : player.getInventory().addItem(itemStack).entrySet()){
           player.getWorld().dropItem(player.getLocation(), entry.getValue());
       }
    }
}
