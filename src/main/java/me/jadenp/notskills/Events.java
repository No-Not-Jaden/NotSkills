package me.jadenp.notskills;


import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareInventoryResultEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

import static me.jadenp.notskills.ConfigOptions.getPlayerData;
import static me.jadenp.notskills.Items.skillSlotArtifact;

public class Events  implements Listener {
    public Events() {
        Bukkit.getPluginManager().registerEvents(new SkillsGUI(), NotSkills.getInstance());
    }


    @EventHandler
    public void preparedResult(PrepareInventoryResultEvent event){
        if (event.getView().getType() == InventoryType.ANVIL){ // possible smithing table in the future
            ItemStack[] contents = event.getInventory().getContents();
            if (contents[1].isSimilar(skillSlotArtifact)){
                if (contents[0] != null){
                    ItemStack item = contents[0];
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    Skills skill = new Skills(Objects.requireNonNull(meta.getLore())).addSkillSlots(1);
                    meta.setLore(skill.getLore());
                    item.setItemMeta(meta);
                    event.setResult(item);
                }

            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (getPlayerData(event.getPlayer()) == null){
            NotSkills.getInstance().playerDataMap.put(event.getPlayer().getUniqueId(), new PlayerData(event.getPlayer().getUniqueId()));
        }
    }

}



