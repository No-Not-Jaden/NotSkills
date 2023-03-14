package me.jadenp.notskills;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private Map<String, Long> skillCooldowns = new HashMap<>();
    private final UUID uuid;
    public PlayerData(UUID uuid){
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

    public long getCooldown(String name){
        if (skillCooldowns.containsKey(name)){
            return skillCooldowns.get(name);
        }
        return 0;
    }
}
