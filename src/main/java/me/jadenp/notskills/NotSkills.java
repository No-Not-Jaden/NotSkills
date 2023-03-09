package me.jadenp.notskills;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class NotSkills extends JavaPlugin {

    // use clicks to cast spell
    // record time between clicks

    private static NotSkills instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Items.addMaterialData();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        getCommand("notskills").setExecutor(new Commands());
        saveDefaultConfig();
        ConfigOptions.reloadOptions();
    }

    public static Plugin getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}