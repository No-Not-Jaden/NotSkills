package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.SkillTraits.RepeatingSkill;
import me.jadenp.notskills.BuiltInSkills.SkillHandler;
import me.jadenp.notskills.NotSkills;
import me.jadenp.notskills.utils.ConfigOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Veil extends RepeatingSkill {

    private final Location location;
    private final double radius;
    private final List<Location> spawnLocations = new ArrayList<>();
    private Material blockMaterial;
    /**
     * A skill that is triggered repeatedly for a certain amount of time
     *
     * @param livingEntity The entity that is using the skill
     * @param actions      The amount of actions until the skill expires
     * @param expireTicks  How many total ticks until the skill expires
     * @param itemBound    True if the skill must be used with the same item
     * @param delayTicks   Number of ticks until the skill begins
     * @param timingTicks  Number of ticks between actions
     * @param radius       Radius of the veil
     */
    public Veil(LivingEntity livingEntity, int actions, int expireTicks, boolean itemBound, int delayTicks, int timingTicks, double radius, String material) {
        super(livingEntity, actions, expireTicks, itemBound, delayTicks, timingTicks);
        location = livingEntity.getLocation();
        this.radius = radius;
        try {
            blockMaterial = Material.valueOf(material.toUpperCase());
        } catch (IllegalArgumentException e){
            if (ConfigOptions.debug)
                Bukkit.getLogger().info("[NotSkills] (Veil Skill) Couldn't get material from: " + material);
            blockMaterial = Material.SCULK;
        }
        calculateParticleLocations();
        // start the skill
        runnable = new BukkitRunnable(){
            @Override
            public void run() {
                if (!skillAction())
                    this.cancel();
            }
        }.runTaskTimer(NotSkills.getInstance(), super.getDelayTicks(), super.getTimingTicks());
    }

    public static final Object[] defaultParameters = new Object[]{10, 300, false, 0, 80, 25.0, "SCULK"};
    public Veil(LivingEntity livingEntity, String[] requestedParameters){
        super(livingEntity);
        Object[] parameters = SkillHandler.fillParameters(defaultParameters, requestedParameters);
        registerParameters(parameters);

        location = livingEntity.getLocation();
        this.radius = (double) parameters[5];
        try {
            blockMaterial = Material.valueOf(((String) parameters[6]).toUpperCase());
        } catch (IllegalArgumentException e){
            if (ConfigOptions.debug)
                Bukkit.getLogger().info("[NotSkills] (Veil Skill) Couldn't get material from: " + parameters[6]);
            blockMaterial = Material.SCULK;
        }

        calculateParticleLocations();

        // start the skill
        runnable = new BukkitRunnable(){
            @Override
            public void run() {
                if (!skillAction())
                    this.cancel();
            }
        }.runTaskTimer(NotSkills.getInstance(), super.getDelayTicks(), super.getTimingTicks());
    }

    public void calculateParticleLocations(){
        spawnLocations.clear();
        for (double thetaY = 0; thetaY < Math.PI ; thetaY+= 1 / radius) {
            for (double thetaX = 0; thetaX < 2 * Math.PI; thetaX+= (0.8 + Math.abs(Math.cos(thetaY))) / radius ) {
                double x = radius * Math.cos(thetaX) * Math.sin(thetaY);
                double y = radius * Math.cos(thetaY);
                double z = radius * Math.sin(thetaX) * Math.sin(thetaY);
                spawnLocations.add(new Location(location.getWorld(), location.getX() + x, location.getY() + y, location.getZ() + z));
            }
        }
    }

    @Override
    public boolean skillAction() {
        if (!super.skillAction())
            return false;
        if (runnable != null) {
            if (location.getChunk().isLoaded()) {
                for (Location particleLocation : spawnLocations) {
                    particleLocation.getWorld().spawnParticle(Particle.BLOCK_MARKER, particleLocation, 1, 0,0,0,1, blockMaterial.createBlockData(), true);
                }
            }
        }
        return true;
    }
}
