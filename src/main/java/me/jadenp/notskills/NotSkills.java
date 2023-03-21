package me.jadenp.notskills;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
                Type mapType = new TypeToken<Map<String, PlayerData>>() {
                }.getType();
                playerDataMap = gson.fromJson(new String(Files.readAllBytes(Paths.get(playerData.getPath()))), mapType);
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
        }.runTaskTimer(this, 3600L, 3600L);

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
