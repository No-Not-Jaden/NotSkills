package me.jadenp.notskills;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Items {

    public static ItemStack skillSlotArtifact = new ItemStack(Material.PAPER);
    public static ItemStack fill = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    public static final ItemStack nextArrow = new ItemStack(Material.SPECTRAL_ARROW);
    public static final ItemStack backArrow = new ItemStack(Material.SPECTRAL_ARROW);

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

        meta = fill.getItemMeta();
        assert meta != null;
        meta.setDisplayName("");
        fill.setItemMeta(meta);

        meta = nextArrow.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.YELLOW + "Next");
        backArrow.setItemMeta(meta);
        meta = backArrow.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.YELLOW + "Back");
        backArrow.setItemMeta(meta);


    }



    public static void givePlayer(Player player, ItemStack itemStack){
       for (Map.Entry<Integer, ItemStack> entry : player.getInventory().addItem(itemStack).entrySet()){
           player.getWorld().dropItem(player.getLocation(), entry.getValue());
       }
    }
}
