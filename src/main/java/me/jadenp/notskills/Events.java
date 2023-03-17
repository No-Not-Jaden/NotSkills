package me.jadenp.notskills;



import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareInventoryResultEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.jadenp.notskills.ConfigOptions.*;
import static me.jadenp.notskills.Items.*;

public class Events  implements Listener {
    public Events() {

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

    }

    @EventHandler
    public void preparedResult(PrepareInventoryResultEvent event){
        if (event.getView().getType() == InventoryType.ANVIL){ // possible smithing table in the future
            ItemStack[] contents = event.getInventory().getContents();
            if (contents[1].isSimilar(skillSlotArtifact)){
                if (contents[0] != null){
                    ItemStack item = contents[0];
                    ItemMeta meta = item.getItemMeta();
                    Skills skill = new Skills(meta.getLore()).addSkillSlots(1);
                    meta.setLore(skill.getLore());
                    item.setItemMeta(meta);
                    event.setResult(item);
                }

            }
        }
    }

}



