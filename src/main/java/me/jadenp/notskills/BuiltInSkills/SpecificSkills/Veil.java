package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.RepeatingSkill;
import me.jadenp.notskills.BuiltInSkills.SkillHandler;
import me.jadenp.notskills.NotSkills;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class Veil extends RepeatingSkill {

    private final Location location;
    private final double radius;
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
    public Veil(LivingEntity livingEntity, int actions, int expireTicks, boolean itemBound, int delayTicks, int timingTicks, double radius) {
        super(livingEntity, actions, expireTicks, itemBound, delayTicks, timingTicks);
        location = livingEntity.getLocation();
        this.radius = radius;
        // start the skill
        // start the skill
        runnable = new BukkitRunnable(){
            @Override
            public void run() {
                if (!skillAction())
                    this.cancel();
            }
        }.runTaskTimer(NotSkills.getInstance(), super.getDelayTicks(), timingTicks);
    }

    public static final Object[] defaultParameters = new Object[]{1, 300, true, 100, 1.0};
    public Veil(LivingEntity livingEntity, String[] requestedParameters){
        super(livingEntity);
        Object[] parameters = SkillHandler.fillParameters(defaultParameters, requestedParameters);
        registerParameters(parameters);

        location = livingEntity.getLocation();
        this.radius = (double) parameters[5];
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

        }
        return true;
    }
}
