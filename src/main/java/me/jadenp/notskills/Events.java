package me.jadenp.notskills;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareInventoryResultEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.jadenp.notskills.utils.ConfigOptions.getPlayerData;
import static me.jadenp.notskills.utils.Items.skillSlotArtifact;

public class Events  implements Listener {
    public Events() {
        Bukkit.getPluginManager().registerEvents(new SkillsGUI(), NotSkills.getInstance());
        Bukkit.getPluginManager().registerEvents(new SkillTrigger(), NotSkills.getInstance());
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (e.getView().getType() == InventoryType.ANVIL) {
                AnvilInventory anvilInv = (AnvilInventory) e.getInventory();
                int slot = e.getRawSlot();

                if (slot == 2) {
                    ItemStack[] itemsInAnvil = anvilInv.getContents();

                    if (itemsInAnvil[0] != null && itemsInAnvil[1].isSimilar(skillSlotArtifact)) {
                        ItemStack result = itemsInAnvil[0];
                        ItemMeta meta = result.getItemMeta();
                        assert meta != null;
                        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                        Skills skill = new Skills(lore);
                        skill.addSkillSlots(itemsInAnvil[1].getAmount());
                        meta.setLore(skill.getLore());
                        result.setItemMeta(meta);
                        e.setCurrentItem(result);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (getPlayerData(event.getPlayer()) == null){
            NotSkills.getInstance().addData(event.getPlayer().getUniqueId());
        }
    }

}



