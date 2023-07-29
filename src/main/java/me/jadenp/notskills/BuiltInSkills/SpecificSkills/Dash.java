package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.SkillTraits.BuiltInSkill;
import me.jadenp.notskills.BuiltInSkills.SkillHandler;
import org.bukkit.entity.LivingEntity;

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
    public static final Object[] defaultParameters = new Object[]{1, 2.0};
    public Dash(LivingEntity livingEntity, String[] requestedParameters){
        super(livingEntity);
        Object[] parameters = SkillHandler.fillParameters(defaultParameters, requestedParameters);
        registerParameters(parameters);
        this.speedMultiplier = (double) parameters[1];
    }


    @Override
    public boolean skillAction(){
        if (!super.skillAction())
            return false;
        livingEntity.setVelocity(livingEntity.getVelocity().add(livingEntity.getEyeLocation().getDirection().multiply(speedMultiplier)));
        skillAction();
        return true;
    }

}
