package me.jadenp.notskills.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.jadenp.notskills.NotSkills;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static me.jadenp.notskills.utils.ConfigOptions.color;

public class Language {
    private static final File languageFile = new File(NotSkills.getInstance().getDataFolder() + File.separator + "language.yml");
    public static String prefix;
    public static String skillIdentifier;
    public static String skillSlotsReserved;
    public static String[] splitReserved;
    public static String skillBindIdentifier;
    public static String[] splitBind;
    public static String skillBreak;
    public static String skillMenu;
    public static String[] splitSkillMenu;

    public static String noPermission;
    public static String sstChange;
    public static String unknownSst;
    public static String sstDisabled;
    public static String unknownPlayer;
    public static String unknownItem;
    public static String noSkillSlots;
    public static void reloadLanguage(){

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(languageFile);

        if (!configuration.isSet("prefix"))
            configuration.set("prefix", "&7[&9Not&dSkills&7] &8> ");
        if (!configuration.isSet("skill-identifier"))
            configuration.set("skill-identifier", "&0-=<0>=-");
        if (!configuration.isSet("skill-slots-available"))
            configuration.set("skill-slots-available", "&f&l{amount}&7x &fSkill Slots Available");
        if (!configuration.isSet("skill-bind-identifier"))
            configuration.set("skill-bind-identifier", "&7<&9{amount}&7> ");
        if (!configuration.isSet("skill-break"))
            configuration.set("skill-break", "");
        if (!configuration.isSet("skill-menu"))
            configuration.set("skill-menu", "&d&lAvailable Skills ");
        if (!configuration.isSet("no-permission"))
            configuration.set("no-permission", "&cYou do not have permission to access this command.");
        if (!configuration.isSet("sst-change"))
            configuration.set("sst-change", "&aChanged your skill select type to &2{type}&a.");
        if (!configuration.isSet("unknown-sst"))
            configuration.set("unknown-sst", "&cUnknown skill select type");
        if (!configuration.isSet("sst-disabled"))
            configuration.set("sst-disabled", "&eChoosing your own skill select type is not enabled.");
        if (!configuration.isSet("unknown-player"))
            configuration.set("unknown-player", "&cUnknown Player!");
        if (!configuration.isSet("unknown-item"))
            configuration.set("unknown-item", "&cUnknown Item!");
        if (!configuration.isSet("no-skill-slots"))
            configuration.set("no-skill-slots", "&cThis item doesn't have any skill slots.");

        try {
            configuration.save(languageFile);
        } catch (IOException ignored){}

        prefix = color(configuration.getString("prefix"));
        skillIdentifier = color(configuration.getString("skill-identifier"));
        skillSlotsReserved = color(configuration.getString("skill-slots-available"));
        skillBindIdentifier = color(configuration.getString("skill-bind-identifier"));
        skillBreak = color(configuration.getString("skill-break"));
        skillMenu = color(configuration.getString("skill-menu"));

        noPermission = color(configuration.getString("no-permission"));
        sstChange = color(configuration.getString("sst-change"));
        unknownSst = color(configuration.getString("unknown-sst"));
        sstDisabled = color(configuration.getString("sst-disabled"));
        unknownPlayer = color(configuration.getString("unknown-player"));
        unknownItem = color(configuration.getString("unknown-item"));
        noSkillSlots = color(configuration.getString("no-skill-slots"));

        splitReserved = skillSlotsReserved.split("\\{amount}");
        splitBind = skillBindIdentifier.split("\\{amount}");
        splitSkillMenu = skillMenu.split("\\{page}");
    }

    public static String parse(String text, OfflinePlayer player, Object... replacements){
        String playerName = player != null && player.getName() != null ? player.getName() : "Player";
        text = text.replaceAll("\\{player}", playerName);

        int replacement = 0;
        while (text.contains("{") && replacement < replacements.length){
            text = text.substring(0, text.indexOf("{")) + replacements[replacement].toString() + text.substring(text.indexOf("}") + 1);
            replacement++;
        }

        if (ConfigOptions.papiEnabled){
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

}
