package me.jadenp.notskills;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class Items {
    public static ItemStack wand = new ItemStack(Material.STICK);
    public static ItemStack sword = new ItemStack(Material.IRON_SWORD);
    public static ItemStack bow = new ItemStack(Material.BOW);
    public static ItemStack trident = new ItemStack(Material.TRIDENT);

    public static void addMaterialData(){
        ItemMeta meta = wand.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GREEN + "Magic Wand");
        meta.setLore(new ArrayList<>(Arrays.asList("", ChatColor.DARK_GREEN + "Not your ordinary stick...", ChatColor.DARK_GREEN + "This one is in the shape of a gun", "")));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        wand.setItemMeta(meta);
        wand.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

        meta = sword.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GREEN + "Skill Sword");
        meta.setLore(new ArrayList<>(Arrays.asList("", ChatColor.DARK_GREEN + "*This sword stares back at you*", "")));
        sword.setItemMeta(meta);

        meta = bow.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GREEN + "Skill Bow");
        meta.setLore(new ArrayList<>(Arrays.asList("", ChatColor.DARK_GREEN + "Strung with the hairs",ChatColor.DARK_GREEN + "that fall out of your head", "")));
        bow.setItemMeta(meta);

        meta = trident.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GREEN + "Skill Trident");
        meta.setLore(new ArrayList<>(Arrays.asList("", ChatColor.DARK_GREEN + "Borrowed from Aqua-man", "")));
        trident.setItemMeta(meta);
        trident.addEnchantment(Enchantment.LOYALTY, 3);
    }

    public static boolean isMagicItem(ItemStack itemStack){
        return itemStack.isSimilar(wand) || itemStack.isSimilar(bow) || itemStack.isSimilar(sword) || itemStack.isSimilar(trident);
    }
}
