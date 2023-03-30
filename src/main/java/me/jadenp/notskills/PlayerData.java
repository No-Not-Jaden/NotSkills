package me.jadenp.notskills;

import org.bukkit.ChatColor;

import java.util.*;

import static me.jadenp.notskills.utils.ConfigOptions.color;

public class PlayerData {
    private final Map<String, Long> skillCooldowns = new HashMap<>();
    private UUID uuid;
    public PlayerData(UUID uuid){
        this.uuid = uuid;
    }
    public PlayerData(){}

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, Long> getSkillCooldowns() {
        return skillCooldowns;
    }

    public boolean isSkillUnlocked(String name){
        name = ChatColor.stripColor(color(name));
        return skillCooldowns.containsKey(name);
    }

    public void setSkillUnlocked(String name, boolean unlocked){
        name = ChatColor.stripColor(color(name));
        if (unlocked){
            if (!skillCooldowns.containsKey(name))
                skillCooldowns.put(name, 0L);
        } else {
            skillCooldowns.remove(name);
        }
    }

    public List<String> getSkillsUnlocked(){
        List<String> skills = new ArrayList<>();
        for (Object obj : skillCooldowns.keySet().toArray()){
            skills.add((String) obj);
        }
        return skills;
    }

    public long getCooldown(String name){
        name = ChatColor.stripColor(color(name));
        if (skillCooldowns.containsKey(name)){
            return skillCooldowns.get(name);
        }
        return 0;
    }

    public void setCoolDown(String name, double seconds){
        name = ChatColor.stripColor(color(name));
        if (skillCooldowns.containsKey(name)){
            skillCooldowns.replace(name, System.currentTimeMillis() + (long) (seconds * 1000));
        } else {
            skillCooldowns.put(name, System.currentTimeMillis() + (long) (seconds * 1000));
        }
    }

    public void setSkills(String[] skills){
        skillCooldowns.clear();
        for (String skill : skills){
            skill = ChatColor.stripColor(color(skill));
            skillCooldowns.put(skill, 0L);
        }
    }
}
