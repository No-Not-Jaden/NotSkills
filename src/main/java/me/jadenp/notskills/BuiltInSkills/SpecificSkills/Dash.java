package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.BuiltInSkill;
import org.bukkit.entity.LivingEntity;

public class Dash extends BuiltInSkill {
    /**
     * Move forward in the blink of an eye
     *
     * @param livingEntity The entity that is using the skill
     * @param actions      The amount of actions until the skill expires
     */
    public Dash(LivingEntity livingEntity, int actions) {
        super(livingEntity, actions);
    }
}
