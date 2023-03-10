package me.jadenp.notskills;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigOptions {
    public static int skillSelectType = 0;
    public static double pauseRatio = 1.75;
    public static int expireMS = 10000;

    public static void reloadOptions(){
        NotSkills.getInstance().reloadConfig();
        FileConfiguration config = NotSkills.getInstance().getConfig();
        skillSelectType = config.getInt("skill-select-type");
        pauseRatio = config.getDouble("pause-ratio");
        expireMS = config.getInt("expire-ms");
    }
}
