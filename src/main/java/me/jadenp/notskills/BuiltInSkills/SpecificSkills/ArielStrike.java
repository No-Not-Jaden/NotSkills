package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.ProjectileSkill;
import me.jadenp.notskills.NotSkills;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ArielStrike extends ProjectileSkill {
    private final Location target;
    private final int spread;
    private final int delay;
    /**
     * A skill that fires a volley of projectiles at the target from the sky
     *
     * @param livingEntity The entity that is using the skill
     * @param actions      How many projectiles will spawn
     * @param expireTicks  How long until the skill expires
     * @param itemBound    true if the entity must be holding the skill item to use the skill
     * @param chargeTicks  How long it takes to charge the skill weapon
     * @param range        How many blocks away the entity can use it
     * @param spread       How many blocks away from the target can projectiles spawn
     * @param delay        Delay in-between projectile launches
     */
    public ArielStrike(LivingEntity livingEntity, int actions, int expireTicks, boolean itemBound, int chargeTicks, int range, int spread, int delay) {
        super(livingEntity, actions, expireTicks, itemBound, chargeTicks);
        this.spread = spread;
        this.delay = delay;
        target = livingEntity.getTargetBlock(null, range).getLocation();
    }

    @Override
    public boolean onLaunch(ProjectileLaunchEvent event){
        if (!super.onLaunch(event))
            return false;
        if (!event.isCancelled())
            event.setCancelled(true);
        new BukkitRunnable(){
            @Override
            public void run() {
                Location spawnLocation = new Location(target.getWorld(), target.getX() + Math.random() * spread * 2 - spread, target.getY() + 10, target.getZ() + Math.random() * spread * 2 - spread);
                Projectile projectile = target.getWorld().spawn(spawnLocation, event.getEntity().getClass());
                projectile.setVelocity(new Vector(0, -1, 0));
                projectile.setMetadata("remove", new FixedMetadataValue(NotSkills.getInstance(), true));
            }
        }.runTaskLater(NotSkills.getInstance(), (long) actions * delay);
        onLaunch(event);
        return true;
    }

    @Override
    public void chargeAction(){
        target.getWorld().spawnParticle(Particle.CLOUD, target, 0, 0, 0.1, 0);
    }
}
