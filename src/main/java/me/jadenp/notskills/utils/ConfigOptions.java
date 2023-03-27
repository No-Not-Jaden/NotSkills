package me.jadenp.notskills.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.jadenp.notskills.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class ConfigOptions {
    public static Trigger defaultSST = Trigger.DIRECTIONAL_CLICK;
    public static boolean playersChooseSST = true;
    public static double pauseRatio = 1.75;
    public static int expireMS = 10000;
    public static boolean particles = true;

    public static boolean[][] threeTypePatterns = { // left is true, right is false
            {true, true, true},
            {true, true, false},
            {true, false, true},
            {true, false, false},
            {false, true, true},
            {false, true, false},
            {false, false, true},
            {false, false, false}
    };
    public static long multiClickResetTime = 500L;
    public static List<SkillOptions> skills = new ArrayList<>();
    public static boolean papiEnabled = false;
    public static boolean mythicMobsEnabled = false;
    public static int maxSkillSlots = 8;
    public static double mythicMobsSkillChance = 0.5;
    public static boolean multiVerseEnabled = false;
    public static boolean naturalSkillUnlock = true;

    public static void reloadOptions(){
        papiEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        mythicMobsEnabled = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
        multiVerseEnabled = Bukkit.getPluginManager().isPluginEnabled("MultiVerse-Core");

        NotSkills.getInstance().reloadConfig();
        FileConfiguration config = NotSkills.getInstance().getConfig();

        if (!config.isSet("skill-select.default"))
            config.set("skill-select.default", 8);
        if (!config.isSet("skill-select.players-choose"))
            config.set("skill-select.players-choose", true);
        if (!config.isSet("pause-ratio"))
            config.set("pause-ratio", 1.75);
        if (!config.isSet("expire-ms"))
            config.set("expire-ms", 10000);
        if (!config.isSet("multi-click-trigger"))
            config.set("multi-click-trigger", 500);
        if (!config.isSet("particles"))
            config.set("particles", true);
        if (!config.isSet("mythic-mobs-skill-chance"))
            config.set("mythic-mobs-skill-chance", 0.5);
        if (!config.isSet("max-skills"))
            config.set("max-skills", 8);
        if(!config.isSet("natural-skill-unlock"))
            config.set("natural-skill-unlock", true);

        NotSkills.getInstance().saveConfig();

        defaultSST = getTrigger(config.getInt("skill-select.default"));
        playersChooseSST = config.getBoolean("skill-select.players-choose");
        pauseRatio = config.getDouble("pause-ratio");
        expireMS = config.getInt("expire-ms");
        multiClickResetTime = config.getLong("multi-click-trigger");
        particles = config.getBoolean("particles");
        mythicMobsSkillChance = config.getDouble("mythic-mobs-skill-chance");
        maxSkillSlots = config.getInt("max-skills");
        naturalSkillUnlock = config.getBoolean("natural-skill-unlock");


        skills.clear();
        for (int i = 1; config.isSet("skills." + i + ".name"); i++){
            MythicMobsOptions mythicMobsOptions = null;
            if (config.isSet("skills." + i + ".mythic-mobs.weight")){
                mythicMobsOptions = new MythicMobsOptions(config.getInt("skills." + i + ".mythic-mobs.weight"), config.getStringList("skills." + i + ".mythic-mobs.included-mobs"));
            }
            skills.add(new SkillOptions(color(config.getString("skills." + i + ".name")), config.getDouble("skills." + i + ".cooldown"), config.getStringList("skills." + i + ".actions"), config.getStringList("skills." + i + ".allowed-items"), config.getStringList("skills." + i + ".description"), mythicMobsOptions));
        }

    }

    public static SkillOptions getSkill(String name){
        name = ChatColor.stripColor(color(name));
        for (SkillOptions skill : skills){
            String skillName = skill.getName();
            skillName = ChatColor.stripColor(color(skillName));
            if (skillName.equalsIgnoreCase(name))
                return skill;
        }
        return null;
    }

    public static PlayerData getPlayerData(Player player){
        return NotSkills.getInstance().getPlayerDataMap().get(player.getUniqueId());
    }

    public static String getPlaceholders(String str, OfflinePlayer player){
        return papiEnabled ? PlaceholderAPI.setPlaceholders(player, str) : str;
    }

    public static String color(String str){
        str = net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', str);
        return translateHexColorCodes("&#","", str);
    }
    public static String translateHexColorCodes(String startTag, String endTag, String message)
    {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }

    public static Trigger getTrigger(int index){
        switch (index) {
            case 0:
                return Trigger.LEFT_RIGHT_CLICK;
            case 1:
                return Trigger.CROUCH_CLICK;
            case 2:
                return Trigger.JUMP_CLICK;
            case 3:
                return Trigger.CROUCH_JUMP_CLICK;
            case 4:
                return Trigger.TIMED_CLICK;
            case 5:
                return Trigger.DOUBLE_CLICK;
            case 6:
                return Trigger.TRIPLE_CLICK;
            case 7:
                return Trigger.MULTI_CLICK;
            default:
                return Trigger.DIRECTIONAL_CLICK;
        }
    }
    public static double getYawAngle(Vector v1, Vector v2){
        double x = v1.getX();
        double z = v1.getZ();
        double x2 = v2.getX();
        double z2 = v2.getZ();
        return Math.acos((x*x2 + z*z2) / (Math.sqrt(Math.pow(x, 2)+Math.pow(z, 2)) * Math.sqrt(Math.pow(x2, 2)+Math.pow(z2, 2))));
    }
    public static float getLookAtYaw(Vector motion) {
        double dx = motion.getX();
        double dz = motion.getZ();
        double yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }
        return (float) (yaw * 180 / Math.PI);
    }
    // comparing where v2 is relative to v1
    public static String getRelativeVector(Vector v1, Vector v2){
        double a1 = getLookAtYaw(v2);
        double a2 = a1 + 360;
        double a3 = a1 - 360;
        double v = getLookAtYaw(v1);
        double d1 = v - a1;
        double d2 = v - a2;
        double d3 = v - a3;
        if (Math.abs(d1) < Math.abs(d2) && Math.abs(d1) < Math.abs(d3)){
            if (d1 > 0){
                return "r";
            } else {
                return "l";
            }
        } else if (Math.abs(d2) < Math.abs(d1) && Math.abs(d2) < Math.abs(d3)){
            if (d2 > 0){
                return "r";
            } else {
                return "l";
            }
        } else if (Math.abs(d3) < Math.abs(d1) && Math.abs(d3) < Math.abs(d2)){
            if (d3 > 0){
                return "r";
            } else {
                return "l";
            }
        } else {
            return "i";
        }
    }
}
