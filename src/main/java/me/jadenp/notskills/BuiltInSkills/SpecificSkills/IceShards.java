package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.SkillTraits.BuiltInSkill;
import me.jadenp.notskills.BuiltInSkills.SkillHandler;
import me.jadenp.notskills.NotSkills;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class IceShards extends BuiltInSkill {
    private final double damageMultiplier;

    /**
     * A skill that is built into the plugin
     *
     * @param livingEntity The entity that is using the skill
     * @param actions      The amount of actions until the skill expires
     */
    public IceShards(LivingEntity livingEntity, int actions, double damageMultiplier) {
        super(livingEntity, actions);
        this.damageMultiplier = damageMultiplier;
        skillAction();
    }
    public static final Object[] defaultParameters = new Object[]{1, 1.0};
    public IceShards(LivingEntity livingEntity, String[] requestedParameters){
        super(livingEntity);
        Object[] parameters = SkillHandler.fillParameters(defaultParameters, requestedParameters);
        registerParameters(parameters);
        this.damageMultiplier = (double) parameters[1];
        skillAction();
    }
    @Override
    public boolean skillAction(){
        if (!super.skillAction())
            return false;
        Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(10, 50, 200), 1);
        Location front = livingEntity.getEyeLocation().add(livingEntity.getLocation().getDirection().multiply(1.3));
        livingEntity.getWorld().spawnParticle(Particle.REDSTONE, front, 1, dustOptions);
        Vector d = livingEntity.getLocation().getDirection();
        new BukkitRunnable() {
            int timer = 0;
            @Override
            public void run() {
                if (!livingEntity.isDead()) {
                    Location loc = front.add(d.multiply(1 + (timer / 10)));
                    livingEntity.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dustOptions);
                    timer++;
                    if (loc.getBlock().getType() != Material.AIR) {
                        this.cancel();
                        return;
                    }
                    loc = front.add(d.multiply(1 + (timer / 10)));
                    livingEntity.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, dustOptions);
                    timer++;
                    double radius = 2D;
                    List<Entity> near = Objects.requireNonNull(loc.getWorld()).getEntities();
                    for (Entity e : near) {
                        if (e.getLocation().distance(loc) <= radius) {
                            if (e instanceof LivingEntity) {
                                if (e != livingEntity) {

                                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                                    ((LivingEntity) e).damage(5 * damageMultiplier, livingEntity);
                                }
                            }
                        }
                    }
                    if (loc.getBlock().getType() != Material.AIR) {
                        this.cancel();
                    }
                }
                if (timer == 16) {
                    this.cancel();
                }
            }
        }.runTaskTimer(NotSkills.getInstance(), 0, 1L);
        if (actions > 0)
            skillAction();
        return true;
    }
}
