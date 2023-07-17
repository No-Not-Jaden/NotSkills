package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.ProjectileSkill;
import me.jadenp.notskills.BuiltInSkills.SkillHandler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

public class Snipe extends ProjectileSkill {

    private final double damageMultiplier;

    /**
     * Launches a powerful arrow that can reach distant targets
     *
     * @param livingEntity The entity that is using the skill
     * @param actions      How many projectiles can be launched
     * @param expireTicks  How long until the skill expires
     * @param itemBound    true if the entity must be holding the skill item to use the skill
     * @param chargeTicks  How long it takes to charge the skill weapon
     * @param damageMultiplier Extra damage applied to the projectile
     */
    public Snipe(LivingEntity livingEntity, int actions, int expireTicks, boolean itemBound, int chargeTicks, double damageMultiplier) {
        super(livingEntity, actions, expireTicks, itemBound, chargeTicks);
        this.damageMultiplier = damageMultiplier;
    }

    public static final Object[] defaultParameters = new Object[]{1, 300, true, 100, 1.5};
    public Snipe(LivingEntity livingEntity, String[] requestedParameters){
        super(livingEntity);
        Object[] parameters = SkillHandler.fillParameters(defaultParameters, requestedParameters);
        registerParameters(parameters);
        this.damageMultiplier = (double) parameters[4];
    }

    @Override
    public boolean onLaunch(ProjectileLaunchEvent event) {
        if (!super.onLaunch(event))
            return false;
        event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(3));
        if (event.getEntity() instanceof Arrow){
            ((Arrow) event.getEntity()).setDamage(((Arrow) event.getEntity()).getDamage() * damageMultiplier);
        }
        onLaunch(event);
        return true;
    }

    @Override
    public void chargeAction(){
        if (!livingEntity.isDead()) {
            Location front = livingEntity.getEyeLocation().add(livingEntity.getLocation().getDirection());
            Location relative = new Location(front.getWorld(), front.getX() + (Math.random() * 2 - 1), front.getY() + (Math.random() * 2 - 1), front.getZ() + (Math.random() * 2 - 1));
            Vector direction = front.toVector().subtract(relative.toVector()).multiply(0.5);

            front.getWorld().spawnParticle(Particle.REVERSE_PORTAL, relative, 0, direction.getX(), direction.getY(), direction.getZ());
        }
    }
}
