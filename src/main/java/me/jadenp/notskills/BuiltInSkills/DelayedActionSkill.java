package me.jadenp.notskills.BuiltInSkills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DelayedActionSkill extends BuiltInSkill{
    private int expireTicks;
    protected long expireTime;
    private boolean itemBound;

    /**
     * A skill that can be triggered sometime after the skill is activated
     * @param livingEntity The entity that is using the skill
     * @param actions The amount of actions until the skill expires
     * @param expireTicks How many ticks until the skill expires
     * @param itemBound True if the skill must be used with the same item
     */
    public DelayedActionSkill(LivingEntity livingEntity, int actions, int expireTicks, boolean itemBound) {
        super(livingEntity, actions);
        setExpireTicks(expireTicks);
        this.itemBound = itemBound;
    }

    public DelayedActionSkill(LivingEntity livingEntity){
        super(livingEntity);
    }

    protected void registerParameters(Object[] parameters){
        super.registerParameters(parameters);
        setExpireTicks((int) parameters[1]);
        itemBound = (boolean) parameters[2];
    }

    private void setExpireTicks(int expireTicks){
        this.expireTicks = expireTicks;
        expireTime = System.currentTimeMillis() + expireTicks * 50L;
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
