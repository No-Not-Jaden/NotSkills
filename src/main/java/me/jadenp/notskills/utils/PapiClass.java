package me.jadenp.notskills.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PapiClass {
    public static String parse(String text, OfflinePlayer offlinePlayer){
        return PlaceholderAPI.setPlaceholders(offlinePlayer, text);
    }
}
