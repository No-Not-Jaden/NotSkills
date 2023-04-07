package me.jadenp.notskills.BuiltInSkills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DelayedActionSkill extends BuiltInSkill{
    private final int expireTicks;
    protected final long expireTime;
    private final boolean itemBound;

    /**
     * A skill that can be triggered sometime after the skill is activated
     * @param livingEntity The entity that is using the skill
     * @param actions The amount of actions until the skill expires
     * @param expireTicks How many ticks until the skill expires
     * @param itemBound True if the skill must be used with the same item
     */
    public DelayedActionSkill(LivingEntity livingEntity, int actions, int expireTicks, boolean itemBound) {
        super(livingEntity, actions);
        this.expireTicks = expireTicks;
        expireTime = System.currentTimeMillis() + expireTicks * 50L;
        this.itemBound = itemBound;
    }

    /**
     *
     * @return true if the skill has expired
     */
    public boolean isExpired(){
        return System.currentTimeMillis() > expireTime || super.isExpired();
    }

    /**
     *
     * @return true if the player must use the skill item to trigger an action
     */
    public boolean isItemBound() {
        return itemBound;
    }

    /**
     *
     * @return the total amount of ticks the player has to use the skill
     */
    public int getExpireTicks() {
        return expireTicks;
    }
}
