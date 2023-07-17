package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.IChargeSkill;
import me.jadenp.notskills.BuiltInSkills.RepeatingSkill;
import me.jadenp.notskills.BuiltInSkills.SkillHandler;
import me.jadenp.notskills.NotSkills;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.intellij.lang.annotations.Language;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Absorb extends RepeatingSkill implements IChargeSkill {
    private int slot = -1;
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 0, 0), 10);
    private final Location location;
    private final Random rand = new Random();
    private final double damageMultiplier;
    private final double radius;
    private final int chargeTicks;
    private long chargeReady = 0;

    /**
     * Create a black hole that sucks in nearby entities
     *
     * @param livingEntity The entity that is using the skill
     * @param actions      The amount of actions until the skill expires
     * @param expireTicks  How many total ticks until the skill expires
     * @param itemBound    True if the skill must be used with the same item
     * @param delayTicks   Number of ticks until the skill begins
     * @param timingTicks  Number of ticks between actions
     */
    public Absorb(LivingEntity livingEntity, int actions, int expireTicks, boolean itemBound, int delayTicks, int timingTicks, double damageMultiplier, double radius, int chargeTicks) {
        super(livingEntity, actions, expireTicks, itemBound, delayTicks + chargeTicks, timingTicks);

        this.damageMultiplier = damageMultiplier;
        this.radius = radius;
        this.chargeTicks = chargeTicks;
        location = livingEntity.getEyeLocation().add(livingEntity.getEyeLocation().getDirection().multiply(3));
        if (livingEntity instanceof Player)
            slot = ((Player) livingEntity).getInventory().getHeldItemSlot();

        // start the skill
        runnable = new BukkitRunnable(){
            @Override
            public void run() {
                if (!skillAction())
                    this.cancel();
            }
        }.runTaskTimer(NotSkills.getInstance(), super.getDelayTicks(), timingTicks);
    }

    public static final Object[] defaultParameters = new Object[]{1, 2000, true, 3, 300, 1.0, 3.0, 300};
    public Absorb(LivingEntity livingEntity, String[] requestedParameters){
        super(livingEntity);
        Object[] parameters = SkillHandler.fillParameters(defaultParameters, requestedParameters);
        // adding charge ticks to delay ticks
        parameters[3] = (int) parameters[3] + (int) parameters[7];
        registerParameters(parameters);

        this.damageMultiplier = (double) parameters[5];
        this.radius = (double) parameters[6];
        this.chargeTicks = (int) parameters[7];
        location = livingEntity.getEyeLocation().add(livingEntity.getEyeLocation().getDirection().multiply(3));
        if (livingEntity instanceof Player)
            slot = ((Player) livingEntity).getInventory().getHeldItemSlot();

        // start the skill
        runnable = new BukkitRunnable(){
            @Override
            public void run() {
                if (!skillAction())
                    this.cancel();
            }
        }.runTaskTimer(NotSkills.getInstance(), super.getDelayTicks(), (int) parameters[4]);
    }

    @Override
    public boolean skillAction() {
        if (!super.skillAction())
            return false;
        if (runnable != null) {
            if (livingEntity instanceof Player)
                ((Player) livingEntity).getInventory().setHeldItemSlot(slot); // keep the player from doing anything
            assert location.getWorld() != null;
            for (int i = 0; i < 10; i++)
                location.getWorld().spawnParticle(Particle.REDSTONE, new Location(location.getWorld(), location.getX() + (((double) rand.nextInt(8) / 10) - 0.4), location.getY() + (((double) rand.nextInt(8) / 10) - 0.4), location.getZ() + (((double) rand.nextInt(8) / 10) - 0.4)), 1, dustOptions);
            Location particle = new Location(location.getWorld(), location.getX() + (((double) rand.nextInt(20)) - 10), location.getY() + (((double) rand.nextInt(20)) - 10), location.getZ() + (((double) rand.nextInt(20)) - 10));
            Vector vector = location.toVector().subtract(particle.toVector()).normalize();
            location.getWorld().spawnParticle(Particle.SQUID_INK, particle, 0, vector.getX(), vector.getY(), vector.getZ());
            location.getWorld().spawnParticle(Particle.CRIT_MAGIC, livingEntity.getLocation(), 15, 1, 1, 1);
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15, 20));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 15, 250));
            Collection<Entity> near = Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, radius, radius, radius);
            for (Entity e : near) {
                if (e.getLocation().distance(location) <= radius) {
                    if (e instanceof LivingEntity) {
                        if (e != livingEntity) {

                            if (e.getLocation().distance(location) <= 2) {
                                e.setVelocity(new Vector(0, 0, 0));
                                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15, 1));
                                if (((LivingEntity) e).getHealth() < 30 && !(e instanceof Player)) {
                                    location.getWorld().playSound(e.getLocation(), Sound.ENTITY_STRIDER_EAT, 1, 1);
                                    location.getWorld().spawnParticle(Particle.SUSPENDED, e.getLocation(), 20, 1, 1, 1);
                                    e.remove();
                                } else {
                                    ((LivingEntity) e).damage(15 * damageMultiplier, livingEntity);
                                }

                            } else {
                                Vector v = location.toVector().subtract(e.getLocation().toVector()).normalize();
                                e.setVelocity(e.getVelocity().add(v.multiply((10 - e.getLocation().distance(location)) / 5)));
                            }
                        }

                    }
                }
            }
        }
        return true;
    }

    @Override
    public void cancelAction(){
        super.cancelAction();
        cancelCharge();
    }

    @Override
    public void cancelCharge() {
        getRunnable().cancel();
        chargeReady = 0;
    }

    @Override
    public boolean isCharged(){
        return System.currentTimeMillis() > chargeReady && chargeReady != 0;
    }

    @Override
    public void chargeAction() {

    }


    @Override
    public int getCharge() {
        int toGo = ((int)(chargeReady - System.currentTimeMillis()) / 50);
        if (toGo < 0)
            return chargeTicks;
        return chargeTicks - toGo;
    }

    @Override
    public int getRequiredCharge() {
        return chargeTicks;
    }

    @Override
    public void startCharge(){
        chargeReady = System.currentTimeMillis() + chargeTicks * 50L;
    }

}
