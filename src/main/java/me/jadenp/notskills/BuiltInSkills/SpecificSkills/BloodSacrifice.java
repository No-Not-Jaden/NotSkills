package me.jadenp.notskills.BuiltInSkills.SpecificSkills;

import me.jadenp.notskills.BuiltInSkills.BuiltInSkill;
import me.jadenp.notskills.BuiltInSkills.SkillHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BloodSacrifice extends BuiltInSkill {
    private final double damage;
    private final int effectStrength;
    private final int duration;
    /**
     * Grant an increase in stats in exchange for some blood
     *
     * @param livingEntity The entity that is using the skill
     * @param actions      The amount of actions until the skill expires
     */
    public BloodSacrifice(LivingEntity livingEntity, int actions, double damage, int effectStrength, int duration) {
        super(livingEntity, actions);
        this.damage = damage;
        this.effectStrength = effectStrength;
        this.duration = duration;
        skillAction();
    }

    public static final Object[] defaultParameters = new Object[]{1, 5.0, 1, 600};
    public BloodSacrifice(LivingEntity livingEntity, String[] requestedParameters){
        super(livingEntity);
        Object[] parameters = SkillHandler.fillParameters(defaultParameters, requestedParameters);
        registerParameters(parameters);
        this.damage = (double) parameters[1];
        this.effectStrength = (int) parameters[2];
        this.duration = (int) parameters[3];
        skillAction();
    }

    @Override
    public boolean skillAction(){
        if (!super.skillAction())
            return false;
        livingEntity.damage(damage);
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, effectStrength));
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, effectStrength));
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, effectStrength));
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, duration, effectStrength));
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, duration, effectStrength));
        return true;
    }
}
