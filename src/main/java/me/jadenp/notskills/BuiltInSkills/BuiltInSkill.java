package me.jadenp.notskills.BuiltInSkills;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BuiltInSkill {
    protected final LivingEntity livingEntity;
    protected int actions;
    protected static final Object[] defaultParameters = new Object[]{1};

    /**
     * A skill that is built into the plugin
     * @param livingEntity The entity that is using the skill
     * @param actions The amount of actions until the skill expires
     */
    public BuiltInSkill(LivingEntity livingEntity, int actions){
        this.livingEntity = livingEntity;
        this.actions = actions;
    }

    public BuiltInSkill(LivingEntity livingEntity, String[] requestedParameters){
        this.livingEntity = livingEntity;
        Object[] parameters = SkillHandler.fillParameters(defaultParameters, requestedParameters);
        actions = (int) parameters[0];
    }

    /**
     * Create an empty skill to modify later
     * @param livingEntity Entity to be executing the skill
     */
    public BuiltInSkill(LivingEntity livingEntity){
        this.livingEntity = livingEntity;
    }

    protected void registerParameters(Object[] parameters){
        this.actions = (int) parameters[0];
    }

    /**
     * The action of the skill
     * @return true if the action is valid
     */
    public boolean skillAction(){
        actions--;
        return actions >= 0;
    }

    /**
     * @return the player that is preforming this skill
     */
    public @NonNull LivingEntity getLivingEntity(){
        return livingEntity;
    }
    /**
     * @return the remaining amount of times this skill should be triggered
     */
    public int getRemainingActions(){
        return actions;
    }

    public void cancelAction(){
        if (livingEntity instanceof Player)
            ((Player) livingEntity).playSound(livingEntity.getLocation(), Sound.ITEM_SHIELD_BREAK,1,1);
    }

    public boolean isExpired(){
        return actions <= 0;
    }

    public static Object[] getDefaultParameters() {
        return defaultParameters;
    }
}
