package me.jadenp.notskills;

import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static me.jadenp.notskills.utils.ConfigOptions.mythicMobsSkillChance;
import static me.jadenp.notskills.utils.ConfigOptions.skills;

public class MythicMobsListener implements Listener {
    public MythicMobsListener(){}

    @EventHandler
    public void onSpawn(MythicMobSpawnEvent event){
        if (Math.random() <= mythicMobsSkillChance){
            if (event.getEntity() instanceof LivingEntity){
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        LivingEntity entity = (LivingEntity) event.getEntity();
                        EntityEquipment equipment = entity.getEquipment();
                        if (equipment == null)
                            return;
                        ItemStack hand = equipment.getItemInMainHand();
                        if (hand.getType().isAir())
                            return;
                        // this list is a weight table, so there will be multiple of each skill if their weights are > 0
                        List<SkillOptions> validSkills = new ArrayList<>();
                        for (SkillOptions skill : skills){
                            //Bukkit.getLogger().info(hand.getType() + "");
                            if (!skill.isAllowedItem(hand.getType()))
                                continue;
                            if (skill.getMythicMobsOptions() == null)
                                continue;
                            MythicMobsOptions mythicMobsOptions = skill.getMythicMobsOptions();
                            //Bukkit.getLogger().info(event.getMob().getMobType());
                            if (!mythicMobsOptions.getIncludedMobs().contains(event.getMob().getMobType()))
                                continue;
                            for (int i = 0; i < mythicMobsOptions.getWeight(); i++) {
                                validSkills.add(skill);
                            }
                        }
                        if (validSkills.size() == 0)
                            return;
                        SkillOptions skillOptions = validSkills.get((int) (Math.random() * validSkills.size()));
                        ItemMeta meta = hand.getItemMeta();
                        assert meta != null;
                        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                        assert lore != null;
                        Skills skill = new Skills(lore).addSkillSlots(1);
                        skill.addSkill(skillOptions.getName());
                        meta.setLore(skill.getLore());
                        hand.setItemMeta(meta);
                        equipment.setItemInMainHand(hand);
                    }
                }.runTaskLater(NotSkills.getInstance(), 6);
            }
        }
    }
}
