package me.jadenp.notskills.BuiltInSkills;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BuiltInSkill {
    protected final LivingEntity livingEntity;
    protected int actions;

    /**
     * A skill that is built into the plugin
     * @param livingEntity The entity that is using the skill
     * @param actions The amount of actions until the skill expires
     */
    public BuiltInSkill(LivingEntity livingEntity, int actions){
        this.livingEntity = livingEntity;
        this.actions = actions;
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
}
