package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.RepeatingSkill;
import org.bukkit.entity.LivingEntity;

public class Veil extends RepeatingSkill {
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
    public Veil(LivingEntity livingEntity, int actions, int expireTicks, boolean itemBound, int delayTicks, int timingTicks) {
        super(livingEntity, actions, expireTicks, itemBound, delayTicks, timingTicks);
    }
}
