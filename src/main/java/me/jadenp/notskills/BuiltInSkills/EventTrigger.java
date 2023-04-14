package me.jadenp.notskills.BuiltInSkills;

import me.jadenp.notskills.NotSkills;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EventTrigger implements Listener {
    public static Map<UUID, List<DelayedActionSkill>> eventSkills = new HashMap<>();
    /**
     * Listens to events to trigger built-in skills
     */
    public EventTrigger(){
        // Removes expired skills and displays skill charge
        new BukkitRunnable(){
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() > 0){
                    for (Map.Entry<UUID, List<DelayedActionSkill>> entry : eventSkills.entrySet()){
                        List<DelayedActionSkill> skillList = entry.getValue();
                        ListIterator<DelayedActionSkill> skillListIterator = skillList.listIterator();
                        boolean updated = false;
                        while (skillListIterator.hasNext()){
                            DelayedActionSkill skill = skillListIterator.next();
                            if (skill.isExpired() || skill.getLivingEntity().isDead()){
                                updated = true;
                                skillListIterator.remove();
                            } else if (skill instanceof IChargeSkill){
                                if (skill.getLivingEntity() instanceof Player) {
                                    // display skill
                                    int icons = 10; // this is one side, so in reality there are icons * 2
                                    int activeIcons = (int) (icons * ((float) ((IChargeSkill) skill).getCharge() / ((IChargeSkill) skill).getRequiredCharge()));
                                    int inactiveIcons = icons - activeIcons;
                                    StringBuilder builder = new StringBuilder(ChatColor.LIGHT_PURPLE + "|" + ChatColor.BLUE);
                                    for (int i = 0; i < inactiveIcons; i++)
                                        builder.append(" ");
                                    for (int i = 0; i < activeIcons * 2; i++)
                                        builder.append("|");
                                    for (int i = 0; i < inactiveIcons; i++)
                                        builder.append(" ");
                                    builder.append(ChatColor.LIGHT_PURPLE).append("|");

                                    ((Player) skill.getLivingEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(builder.toString()));
                                }
                                ((IChargeSkill) skill).chargeAction();
                            }
                        }
                        if (updated)
                            if (skillList.size() > 0)
                                eventSkills.replace(entry.getKey(), skillList);
                            else
                                eventSkills.remove(entry.getKey());
                    }
                }
            }
        }.runTaskTimer(NotSkills.getInstance(),10,10);
    }


    /**
     * Removes skills that are item bound
     */
    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent event){
        if (event.getNewSlot() == event.getPreviousSlot())
            return;
        if (!eventSkills.containsKey(event.getPlayer().getUniqueId()))
            return;
        List<DelayedActionSkill> delayedActionSkills = eventSkills.get(event.getPlayer().getUniqueId());
        ListIterator<DelayedActionSkill> delayedActionSkillListIterator = delayedActionSkills.listIterator();
        boolean update = false;
        while (delayedActionSkillListIterator.hasNext()){
            DelayedActionSkill skill = delayedActionSkillListIterator.next();
            if (skill.isItemBound()) {
                update = true;
                skill.cancelAction();
                delayedActionSkillListIterator.remove();
            }

        }
        if (update)
            if (delayedActionSkills.size() > 0)
                eventSkills.replace(event.getPlayer().getUniqueId(), delayedActionSkills);
            else
                eventSkills.remove(event.getPlayer().getUniqueId());
    }

    public static void addSkill(UUID uuid, DelayedActionSkill delayedActionSkill){
        List<DelayedActionSkill> delayedActionSkills = eventSkills.containsKey(uuid) ? eventSkills.get(uuid) : new ArrayList<>();
        delayedActionSkills.add(delayedActionSkill);
        eventSkills.put(uuid, delayedActionSkills);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        if (event.getEntity().hasMetadata("remove"))
            event.getEntity().remove();
    }

    /**
     * Activates projectile launch skills
     */
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event){
        if (event.getEntity().getShooter() instanceof LivingEntity) {
            UUID uuid = ((LivingEntity) event.getEntity().getShooter()).getUniqueId();
            if (!eventSkills.containsKey(uuid))
                return;
            List<DelayedActionSkill> delayedActionSkills = eventSkills.get(uuid);
            ListIterator<DelayedActionSkill> delayedActionSkillListIterator = delayedActionSkills.listIterator();
            boolean update = false;
            while (delayedActionSkillListIterator.hasNext()){
                DelayedActionSkill skill = delayedActionSkillListIterator.next();
                if (!(skill instanceof ProjectileSkill))
                    return;
                ProjectileSkill projectileSkill = (ProjectileSkill) skill;
                if (skill.isExpired()){
                    delayedActionSkillListIterator.remove();
                    update = true;
                } else if (!projectileSkill.isCharged()){
                    delayedActionSkillListIterator.remove();
                    update = true;
                    projectileSkill.cancelAction();
                } else {
                    projectileSkill.onLaunch(event);
                }
            }
            if (update)
                if (delayedActionSkills.size() > 0)
                    eventSkills.replace(uuid, delayedActionSkills);
                else
                    eventSkills.remove(uuid);
        }


    }

}
