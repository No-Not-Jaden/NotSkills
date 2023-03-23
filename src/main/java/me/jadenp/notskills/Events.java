package me.jadenp.notskills;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static me.jadenp.notskills.utils.ConfigOptions.*;
import static me.jadenp.notskills.utils.Items.backArrow;
import static me.jadenp.notskills.utils.Items.skillSlotArtifact;

public class Events  implements Listener {
    public Events() {
        Bukkit.getPluginManager().registerEvents(new SkillsGUI(), NotSkills.getInstance());
        Bukkit.getPluginManager().registerEvents(new SkillTrigger(), NotSkills.getInstance());
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event){
        ItemStack[] contents = event.getInventory().getContents();
        //Bukkit.getLogger().info(Arrays.toString(contents));
        if (contents[0] == null || contents[1] == null)
            return;
        if (!contents[1].isSimilar(skillSlotArtifact))
            return;
        ItemStack result = contents[0].clone();
        ItemMeta meta = result.getItemMeta();
        assert meta != null;
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        assert lore != null;
        Skills skill = new Skills(lore);
        if (skill.getEmptySkillSlots() + skill.getUsedSkillSlots() + contents[1].getAmount() > maxSkillSlots)
            return;
        event.getInventory().setRepairCost((skill.getEmptySkillSlots() + skill.getUsedSkillSlots() + contents[1].getAmount()) * 5);
        skill.addSkillSlots(contents[1].getAmount());
        meta.setLore(skill.getLore());
        result.setItemMeta(meta);


        event.setResult(result);

        ((Player)event.getView().getPlayer()).updateInventory();
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (getPlayerData(event.getPlayer()) == null){
            NotSkills.getInstance().addData(event.getPlayer().getUniqueId());
        }
    }

}



