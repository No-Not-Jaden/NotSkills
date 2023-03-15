package me.jadenp.notskills;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

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

    public static void reloadOptions(){
        NotSkills.getInstance().reloadConfig();
        FileConfiguration config = NotSkills.getInstance().getConfig();

        defaultSST = getTrigger(config.getInt("skill-select.default"));
        playersChooseSST = config.getBoolean("skill-select.players-choose");
        pauseRatio = config.getDouble("pause-ratio");
        expireMS = config.getInt("expire-ms");

        splitReserved = skillSlotsReserved.split("\\{amount\\}");
        splitBind = skillBindIdentifier.split("\\{amount\\}");
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
}
