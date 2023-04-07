package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.BuiltInSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class Dash extends BuiltInSkill {
    private final double speedMultiplier;
    /**
     * Move forward in the blink of an eye
     *
     * @param livingEntity The entity that is using the skill
     * @param actions      The amount of actions until the skill expires
     */
    public Dash(LivingEntity livingEntity, int actions, double speedMultiplier) {
        super(livingEntity, actions);
        this.speedMultiplier = speedMultiplier;
        skillAction();
    }

    @Override
    public boolean skillAction(){
        if (!super.skillAction())
            return false;
        livingEntity.setVelocity(livingEntity.getVelocity().normalize().multiply(new Vector(speedMultiplier,1,speedMultiplier)));
        if (actions > 0)
            skillAction();
        return true;
    }

}
