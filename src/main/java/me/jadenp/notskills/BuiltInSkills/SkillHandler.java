package me.jadenp.notskills.BuiltInSkills;

import me.jadenp.notskills.BuiltInSkills.SkillTraits.BuiltInSkill;
import me.jadenp.notskills.BuiltInSkills.SkillTraits.DelayedActionSkill;
import me.jadenp.notskills.utils.ConfigOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;



public class SkillHandler {


    private static final List<String> skillNames = Arrays.asList("ABSORB","ARIELSTRIKE","BLOODSACRIFICE","DASH","ICESHARDS","SNIPE","VEIL","LOCK");



    public static Object[] fillParameters(Object[] defaultParameters, String[] givenParameters){
        for (int i = 0; i < givenParameters.length; i++) {
            defaultParameters[i] = matchValue(defaultParameters[i], givenParameters[i]);
        }
        return defaultParameters;
    }

    /**
     * Changes the toMatch data type from string format to the data type of the originalValue
     * @param originalValue data type to match
     * @param toMatch String value of data type
     * @return toMatch but in the same data type as originalValue
     */
    private static Object matchValue(Object originalValue, String toMatch){
        try {
            if (originalValue instanceof Long)
                return Long.parseLong(toMatch);
            if (originalValue instanceof Integer)
                return Integer.parseInt(toMatch);
            if (originalValue instanceof Double)
                return Double.parseDouble(toMatch);
            if (originalValue instanceof Float)
                return Float.parseFloat(toMatch);
            if (originalValue instanceof Boolean)
                return Boolean.parseBoolean(toMatch);
        } catch (RuntimeException e){
            return toMatch;
        }
        return toMatch;
    }

    /**
     * Find the skill type and execute the skill
     * @param name Name of the skill (used only for debugging purposes)
     * @param command Skill type and parameters
     * @param executer LivingEntity to execute the skill
     */
    public static void executeSkill(String name, String[] command, LivingEntity executer){
        if (command.length == 0)
            // no skill type
            return;
        if (!skillNames.contains(command[0].toUpperCase())) {
            // skill type is not registered
            Bukkit.getLogger().warning("[NotSkills] " + command[0] + " for skill " + name + " is not a valid skill type");
            return;
        }

        // Get class from skill type
        Class<? extends BuiltInSkill> skillType;
        try {
            skillType = Class.forName("me.jadenp.notskills.BuiltInSkills.SpecificSkills." + command[0]).asSubclass(BuiltInSkill.class);
        } catch (ClassNotFoundException | ClassCastException e) {
            Bukkit.getLogger().warning("[NotSkills] " + command[0] + " for skill " + name + " is not a valid skill type");
            return;
        }

        // Get constructor from class
        Constructor<?> constructor;
        try {
             constructor = skillType.getConstructor(LivingEntity.class, String[].class);
        } catch (NoSuchMethodException e) {
            if (ConfigOptions.debug){
                Bukkit.getLogger().info("[NotSkills] No such method for " + command[0] + " in skill " + name);
            }
            return;
        } catch (SecurityException e) {
            if (ConfigOptions.debug){
                Bukkit.getLogger().info("[NotSkills] Security Exception for " + command[0] + " in skill " + name);
            }
            return;
        }

        // remove the skill type from the command
        String[] parameters = new String[command.length-1];
        System.arraycopy(command, 1, parameters, 0, command.length - 1);

        // Create a new instance of the skill from the constructor
        Object skillInstance;
        try {
             skillInstance = constructor.newInstance(executer, parameters);
        }catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            if (ConfigOptions.debug)
                e.printStackTrace();
            return;
        }

        // add to EventTrigger if it is a DelayedActionSkill
        if (skillInstance instanceof DelayedActionSkill) {
            EventTrigger.addSkill(executer.getUniqueId(), (DelayedActionSkill) skillInstance);
        }



    }
}
