package me.jadenp.notskills;

import java.util.*;

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
        return skillCooldowns.containsKey(name);
    }

    public List<String> getSkillsUnlocked(){
        List<String> skills = new ArrayList<>();
        for (Object obj : skillCooldowns.keySet().toArray()){
            skills.add((String) obj);
        }
        return skills;
    }

    public long getCooldown(String name){
        if (skillCooldowns.containsKey(name)){
            return skillCooldowns.get(name);
        }
        return 0;
    }

    public void setCoolDown(String name, double seconds){
        if (skillCooldowns.containsKey(name)){
            skillCooldowns.replace(name, (long) (seconds * 1000));
        } else {
            skillCooldowns.put(name, (long) (seconds * 1000));
        }
    }

    public void setSkills(String[] skills){
        skillCooldowns.clear();
        for (String skill : skills){
            skillCooldowns.put(skill, 0L);
        }
    }
}
