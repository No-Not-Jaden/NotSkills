package me.jadenp.notskills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static me.jadenp.notskills.Items.*;

public class SkillEffects implements Listener {

    Map<UUID, SkillLong> selectedSkills = new HashMap<>();
    // 0-3 sword, 4-7 bow, 8-11 trident
    public SkillEffects(){}

    public void addSkill(Player p, int skill){
        selectedSkills.put(p.getUniqueId(), new SkillLong(skill, System.currentTimeMillis()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if (selectedSkills.containsKey(event.getPlayer().getUniqueId())) {
            SkillLong skillLong = selectedSkills.get(event.getPlayer().getUniqueId());
            if (event.getItem() != null) {
                if (event.getItem().isSimilar(sword)) {
                    if (skillLong.getSkill() == 0) {// sweeping strikes
                        if (skillLong.getTime() + 30000 < System.currentTimeMillis()){
                            selectedSkills.remove(event.getPlayer().getUniqueId(), skillLong);
                            return;
                        }
                        Location attack = event.getPlayer().getEyeLocation().add(event.getPlayer().getEyeLocation().getDirection().multiply(3));
                        for (Entity entity : event.getPlayer().getWorld().getNearbyEntities(attack, 3, 2, 3)) {
                            if (entity instanceof LivingEntity) {
                                if (entity != event.getPlayer()) {
                                    ((LivingEntity) entity).damage(5, event.getPlayer());
                                }
                            }
                        }
                        event.getPlayer().getWorld().spawnParticle(Particle.SWEEP_ATTACK, attack, 10, 3, 2, 3);
                    }
                }

            }
        }
    }
    @EventHandler
    public void onShoot(EntityShootBowEvent event){
        if (event.getEntity() instanceof Player){
            if (selectedSkills.containsKey(event.getEntity().getUniqueId())) {
                SkillLong skillLong = selectedSkills.get(event.getEntity().getUniqueId());
                if (event.getBow() != null) {
                    if (event.getBow().isSimilar(bow)) {
                        if (skillLong.getTime() + 30000 < System.currentTimeMillis()) {
                            selectedSkills.remove(event.getEntity().getUniqueId(), skillLong);
                            return;
                        }
                        if (skillLong.getSkill() == 4) {// super arrow
                            event.getProjectile().setVelocity(event.getProjectile().getVelocity().multiply(5));
                            selectedSkills.remove(event.getEntity().getUniqueId(), skillLong);
                        } else if (skillLong.getSkill() == 5){
                            // chicken
                            event.setProjectile(event.getEntity().getWorld().spawn(event.getProjectile().getLocation(), Chicken.class));
                            selectedSkills.remove(event.getEntity().getUniqueId(), skillLong);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player){
            if (selectedSkills.containsKey(event.getDamager().getUniqueId())) {
                SkillLong skillLong = selectedSkills.get(event.getDamager().getUniqueId());
                if (skillLong.getTime() + 30000 < System.currentTimeMillis()){
                    selectedSkills.remove(event.getDamager().getUniqueId(), skillLong);
                    return;
                }
                ((Player) event.getDamager()).getInventory().getItemInMainHand();
                if (((Player) event.getDamager()).getInventory().getItemInMainHand().isSimilar(sword)) {
                    if (skillLong.getSkill() == 1) {// cinder
                        event.getEntity().setFireTicks(20);
                        selectedSkills.remove(event.getDamager().getUniqueId(), skillLong);
                        event.getEntity().getWorld().spawnParticle(Particle.LAVA, event.getEntity().getLocation(), 10);
                    }
                }
            }
        }
    }

}
