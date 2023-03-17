package me.jadenp.notskills;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import java.sql.Array;
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
    public static String prefix = ChatColor.GRAY + "[" + ChatColor.BLUE + "Not" + ChatColor.LIGHT_PURPLE + "Skills" + ChatColor.GRAY + "] " + ChatColor.DARK_GRAY + "> ";
    public static String skillIdentifier = ChatColor.BLACK + "-=<0>=-";
    public static String skillSlotsReserved = ChatColor.WHITE + "" + ChatColor.BOLD + "{amount}" + ChatColor.GRAY + "x" + ChatColor.WHITE + " Skill Slots Available";
    public static String[] splitReserved;
    public static String skillBindIdentifier = ChatColor.GRAY + "<" + ChatColor.BLUE + "{amount}" + ChatColor.GRAY + "> ";
    public static String[] splitBind;
    public static String skillBreak = ChatColor.DARK_GRAY + "*****";
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

    public static void reloadOptions(){
        papiEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        NotSkills.getInstance().reloadConfig();
        FileConfiguration config = NotSkills.getInstance().getConfig();

        defaultSST = getTrigger(config.getInt("skill-select.default"));
        playersChooseSST = config.getBoolean("skill-select.players-choose");
        pauseRatio = config.getDouble("pause-ratio");
        expireMS = config.getInt("expire-ms");

        splitReserved = skillSlotsReserved.split("\\{amount}");
        splitBind = skillBindIdentifier.split("\\{amount}");

        skills.clear();
        for (int i = 1; config.isSet("skills." + i + ".name"); i++){
            skills.add(new SkillOptions(color(config.getString("skills." + i + ".name")), config.getDouble("skills." + i + ".cooldown"), config.getStringList("skills." + i + ".actions"), config.getStringList("skills." + i + ".allowed-items")));
        }

    }

    public static SkillOptions getSkill(String name){
        for (SkillOptions skill : skills){
            if (skill.getName().equalsIgnoreCase(name))
                return skill;
        }
        return null;
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
