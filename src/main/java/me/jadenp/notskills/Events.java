package me.jadenp.notskills;


import me.jadenp.notskills.MythicMobs.MythicMobsListener;
import me.jadenp.notskills.Trigger.SkillTrigger;
import me.jadenp.notskills.utils.Skills;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static me.jadenp.notskills.utils.ConfigOptions.*;
import static me.jadenp.notskills.utils.Items.skillSlotArtifact;

public class Events  implements Listener {

    private final Map<UUID, Long> mobCooldowns = new HashMap<>();
    public Events() {
        Plugin plugin = NotSkills.getInstance();
        Bukkit.getPluginManager().registerEvents(new SkillsGUI(), plugin);
        Bukkit.getPluginManager().registerEvents(new SkillTrigger(), plugin);
        if (mythicMobsEnabled)
            Bukkit.getPluginManager().registerEvents(new MythicMobsListener(), plugin);
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
    public void onPrepareGrindStone(PrepareGrindstoneEvent event){
        ItemStack[] contents = event.getInventory().getContents();
        if (contents[0] == null && contents[1] == null)
            return;
        ItemStack item = contents[0] == null ? contents[1].clone() : contents[0].clone();
        if (!item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        if (!meta.hasLore())
            return;
        Skills skill = new Skills(Objects.requireNonNull(meta.getLore()));
        if (skill.getEmptySkillSlots() == 0)
            return;
        skill.removeSkillSlots(1);
        meta.setLore(skill.getLore());
        item.setItemMeta(meta);
        event.setResult(item);
        ((Player)event.getView().getPlayer()).updateInventory();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (getPlayerData(event.getPlayer()) == null){
            NotSkills.playerDataMap.put(event.getPlayer().getUniqueId(), new PlayerData(event.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        // skill use for non-players
        if (!(event.getDamager() instanceof LivingEntity))
            return;
        if (event.getDamager() instanceof Player)
            return;
        if (Math.random() > 0.25) // add some variability
            return;

        // check if the entity has a skill item
        EntityEquipment equipment = ((LivingEntity)event.getDamager()).getEquipment();
        if (equipment == null) // no inventory
            return;
        ItemStack hand = equipment.getItemInMainHand();
        ItemMeta meta = hand.getItemMeta();
        if (meta == null) // nothing in hand
            return;
        if (!meta.hasLore()) // no skill
            return;
        Skills skill = new Skills(Objects.requireNonNull(meta.getLore()));
        if (skill.getUsedSkillSlots() == 0) // no used skill
            return;

        SkillOptions skillOptions = getSkill(skill.getSkill(0));
        if (skillOptions == null) // skill doesn't exist
            return;
        // add to mob cooldowns so mobs have to wait just as long as players to use skills
        if (mobCooldowns.containsKey(event.getDamager().getUniqueId())){
            if (mobCooldowns.get(event.getDamager().getUniqueId()) > System.currentTimeMillis())
                return;
            mobCooldowns.replace(event.getDamager().getUniqueId(), (long) (System.currentTimeMillis() + skillOptions.getCooldown() * 1000L));
        } else {
            mobCooldowns.put(event.getDamager().getUniqueId(), (long) (System.currentTimeMillis() + skillOptions.getCooldown() * 1000L));
        }
        // do skill
        skillOptions.runActions((LivingEntity) event.getDamager());
    }

}



