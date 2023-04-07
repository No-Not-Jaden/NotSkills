package me.jadenp.notskills.BuiltInSkills;

import me.jadenp.notskills.NotSkills;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RepeatingSkill extends DelayedActionSkill{
    private final int delayTicks;
    private final int timingTicks;
    protected BukkitTask runnable;
    /**
     * A skill that is triggered repeatedly for a certain amount of time
     *
     * @param livingEntity The entity that is using the skill
     * @param actions      The amount of actions until the skill expires
     * @param expireTicks  How many total ticks until the skill expires
     * @param itemBound    True if the skill must be used with the same item
     * @param delayTicks   Number of ticks until the skill begins
     * @param timingTicks  Number of ticks between actions
     */
    public RepeatingSkill(LivingEntity livingEntity, int actions, int expireTicks, boolean itemBound, int delayTicks, int timingTicks) {
        super(livingEntity, actions, expireTicks, itemBound);
        this.delayTicks = delayTicks;
        this.timingTicks = timingTicks;
        runnable = null; // runnable is set in child class
    }

    /**
     * The action that will be repeated
     */
    @Override
    public boolean skillAction(){
        if (runnable != null) {
            if (isExpired() || livingEntity.isDead()) {
                runnable.cancel();
                runnable = null;
                return false;
            }
        }
        actions--;
        return true;
    }

    public int getDelayTicks() {
        return delayTicks;
    }

    public int getTimingTicks() {
        return timingTicks;
    }

    public BukkitTask getRunnable() {
        return runnable;
    }
}
