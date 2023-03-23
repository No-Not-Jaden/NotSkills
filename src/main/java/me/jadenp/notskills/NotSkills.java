package me.jadenp.notskills;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.jadenp.notskills.utils.ConfigOptions;
import me.jadenp.notskills.utils.Items;
import me.jadenp.notskills.utils.Language;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class NotSkills extends JavaPlugin {

    // use clicks to cast spell
    // record time between clicks

    /**
     * language ~
     * unlock skills somehow -
     * combine artifacts -
     * skill remove doesn't work -
     * cannot unlock skills with multiple names
     * particles to spawn when you use a trigger click
     */

    private static NotSkills instance;
    public Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    public File playerData;
    public Gson gson;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        playerData = new File(getDataFolder() + File.separator + "playerdata.json");

        Items.addMaterialData();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Commands commands = new Commands();
        Objects.requireNonNull(getCommand("notskills")).setExecutor(commands);
        Objects.requireNonNull(getCommand("notskills")).setTabCompleter(commands);

        File language = new File(getDataFolder() + File.separator + "language.yml");
        if (!language.exists()){
            saveResource("language.yml", false);
        }
        Language.reloadLanguage();

        saveDefaultConfig();
        ConfigOptions.reloadOptions();

        // setup gson
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.registerTypeAdapter(PlayerData.class, new PlayerDataAdapter());
        gson = builder.create();


        // read saved playerdata
        try {
            // check to see if there is a file
            if (!playerData.createNewFile()) {
                Type mapType = new TypeToken<Map<UUID, PlayerData>>() {
                }.getType();
                playerDataMap = gson.fromJson(new String(Files.readAllBytes(Paths.get(playerData.getPath()))), mapType);
                if (playerDataMap == null){
                    playerDataMap = new HashMap<>();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // auto-save every 5 minutes
        new BukkitRunnable(){
            @Override
            public void run() {
                save();
            }
        }.runTaskTimer(this, 6000L, 6000L);

    }

    public void addData(UUID uuid){
        playerDataMap.put(uuid, new PlayerData(uuid));
    }

    public void unlockSkill(UUID uuid, String name, boolean unlock){
        if (playerDataMap.containsKey(uuid)){
            playerDataMap.get(uuid).setSkillUnlocked(name, unlock);
        }
    }

    public Map<UUID, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(playerData)) {
            gson.toJson(playerDataMap, writer);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to save player data.");
        }
    }

    public static NotSkills getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        save();
    }

}
