package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.SkillHandler;
import me.jadenp.notskills.BuiltInSkills.SkillTraits.IChargeSkill;
import me.jadenp.notskills.BuiltInSkills.SkillTraits.RepeatingSkill;
import me.jadenp.notskills.NotSkills;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Lock extends RepeatingSkill implements IChargeSkill {
    public static final Object[] defaultParameters = new Object[]{1, 60, true, 0, 10, 100.0, 40};
    private final double maxDistance;
    private int chargeTicks;
    private long chargeReady = 0;

    /**
     * Lock onto a player
     * @param livingEntity entity to do the locking
     * @param actions Amount of times a search is done. There is no point of increasing this
     * @param maxDistance Maximum distance away that the entity can see targets
     */
    public Lock(LivingEntity livingEntity, int actions, int expireTicks, boolean itemBound, int delayTicks, int timingTicks, double maxDistance, int chargeTicks) {
        super(livingEntity, actions, expireTicks, itemBound, delayTicks, timingTicks);
        this.maxDistance = maxDistance;
        this.chargeTicks = chargeTicks;
        runnable = new BukkitRunnable(){
            @Override
            public void run() {
                if (!skillAction())
                    this.cancel();
            }
        }.runTaskTimer(NotSkills.getInstance(), super.getDelayTicks(), super.getTimingTicks());
        startCharge();
    }

    public Lock(LivingEntity livingEntity, String[] requestedParameters){
        super(livingEntity);
        Object[] parameters = SkillHandler.fillParameters(defaultParameters, requestedParameters);
        registerParameters(parameters);
        maxDistance = (double) parameters[1];
        chargeTicks = (int) parameters[6];
        runnable = new BukkitRunnable(){
            @Override
            public void run() {
                if (!skillAction())
                    this.cancel();
            }
        }.runTaskTimer(NotSkills.getInstance(), super.getDelayTicks(), super.getTimingTicks());
        startCharge();
    }

    /**
     * Search through all online players and find the closest to the entity, then make the entity look at them.
     * @return true
     */
    @Override
    public boolean skillAction(){
        if (!super.skillAction()) return false;
        double closest = maxDistance + 1;
        Player target = null;
        for (Player player : Bukkit.getOnlinePlayers()){
            if (!player.getWorld().equals(livingEntity.getWorld()))
                continue;
            double distance = player.getLocation().distance(livingEntity.getLocation());
            if (distance < closest) {
                closest = distance;
                target = player;
            }
        }
        if (target != null){
            Vector direction = target.getEyeLocation().toVector().subtract(livingEntity.getEyeLocation().toVector()).normalize();
            Location lookLocation = livingEntity.getLocation().clone();
            lookLocation.setYaw((float) (180 - Math.toDegrees(Math.atan2(direction.getX(), direction.getZ()))));
            lookLocation.setPitch((float) (90 - Math.toDegrees(Math.acos(direction.getY()))));
            livingEntity.teleport(lookLocation);
            if (livingEntity instanceof Mob){
                ((Mob) livingEntity).setTarget(target);
            }
        }
        skillAction();
        return true;
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
    public void startCharge() {
        chargeReady = System.currentTimeMillis() + chargeTicks * 50L;
    }

    @Override
    public void cancelCharge() {
        chargeReady = 0;
    }

    @Override
    public void cancelAction(){
        super.cancelAction();
        cancelCharge();
    }

    @Override
    public boolean isCharged() {
        return System.currentTimeMillis() > chargeReady && chargeReady != 0;
    }

    @Override
    public void chargeAction() {

    }
}
