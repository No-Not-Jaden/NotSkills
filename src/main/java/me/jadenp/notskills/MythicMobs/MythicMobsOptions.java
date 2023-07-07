package me.jadenp.notskills.MythicMobs;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MythicMobsOptions {
    private final int weight;
    private final Map<String, Double> includedMobs = new HashMap<>();


    public MythicMobsOptions(ConfigurationSection config){
        weight = config.getInt("weight");
        List<String> mobs = config.getStringList("included-mobs");
        for (String mob : mobs){
            String mobName = "";
            double level;
            try {
                mobName = mob.substring(0, mob.lastIndexOf(" "));
                level = Double.parseDouble(mob.substring(mob.lastIndexOf(" ")));
            } catch (IndexOutOfBoundsException e){
                mobName = mob;
                level = 1;
            } catch (NumberFormatException e){
                level = 1;
            }
            includedMobs.put(mobName, level);
        }

    }

    public boolean isValidMob(String name, double level){
        if (!includedMobs.containsKey(name))
            return false;
        return !(includedMobs.get(name) < level);
    }

    public int getWeight() {
        return weight;
    }
}

