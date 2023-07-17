package me.jadenp.notskills.BuiltInSkills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileSkill extends DelayedActionSkill implements IChargeSkill{
    private int chargeTicks;
    private long chargeReady = 0;

    /**
     * A skill that uses a launched projectile
     * @param livingEntity The entity that is using the skill
     * @param actions How many projectiles can be launched
     * @param expireTicks How long until the skill expires
     * @param itemBound true if the entity must be holding the skill item to use the skill
     * @param chargeTicks How long it takes to charge the skill weapon
     */
    public ProjectileSkill(LivingEntity livingEntity, int actions, int expireTicks, boolean itemBound, int chargeTicks) {
        super(livingEntity, actions, expireTicks, itemBound);
        this.chargeTicks = chargeTicks;
        startCharge();
    }

    public ProjectileSkill(LivingEntity livingEntity){
        super(livingEntity);
    }

    protected void registerParameters(Object[] parameters){
        super.registerParameters(parameters);
        this.chargeTicks = (int) parameters[3];
    }


    /**
     * Actions that occur when a player launches a projectile with this skill
     * @implNote Must be overridden
     * @param event The ProjectileLaunchEvent that triggers this skill
     */
    public boolean onLaunch(ProjectileLaunchEvent event){
        return super.skillAction();
    }

    @Override
    public void cancelAction(){
        super.cancelAction();
        cancelCharge();
    }

    @Override
    public boolean isCharged(){
        return System.currentTimeMillis() > chargeReady && chargeReady != 0;
    }

    @Override
    public void chargeAction() {

    }

    @Override
    public void cancelCharge(){
        chargeReady = 0;
    }

    @Override
    public int getCharge() {
        int toGo = ((int)(chargeReady - System.currentTimeMillis()) / 50);
        if (toGo < 0)
            return chargeTicks;
        return chargeTicks - toGo;
    }

    @Override
    public int getRequiredCharge() {
        return chargeTicks;
    }

    @Override
    public void startCharge(){
        chargeReady = System.currentTimeMillis() + chargeTicks * 50L;
    }


}
