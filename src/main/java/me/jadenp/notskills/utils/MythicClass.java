package me.jadenp.notskills.utils;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class MythicClass {
    public MythicClass(){}

    public void castSkill(Entity entity, String skillName){
        MythicBukkit.inst().getAPIHelper().castSkill(entity, skillName);
    }

    public boolean spawnMob(String mythicMob, Location spawnLocation, double level){
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(mythicMob).orElse(null);
        if (mob == null)
            return false;
        mob.spawn(BukkitAdapter.adapt(spawnLocation), level);
        return true;
    }
}
