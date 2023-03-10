package me.jadenp.notskills;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import static me.jadenp.notskills.Items.*;

public class Commands implements CommandExecutor, TabCompleter {
    public Commands(){
    }
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String s, @NonNull String[] args) {
        if (command.getName().equalsIgnoreCase("notskills")){
            if (args.length == 1){
                if (sender instanceof Player) {
                    if (args[0].equalsIgnoreCase("trident")) {
                        ((Player) sender).getWorld().dropItem(((Player) sender).getLocation(), trident);
                    } else if (args[0].equalsIgnoreCase("sword")) {
                        ((Player) sender).getWorld().dropItem(((Player) sender).getLocation(), sword);
                    } else if (args[0].equalsIgnoreCase("bow")) {
                        ((Player) sender).getWorld().dropItem(((Player) sender).getLocation(), bow);
                    } else if (args[0].equalsIgnoreCase("wand")) {
                        ((Player) sender).getWorld().dropItem(((Player) sender).getLocation(), wand);
                    }
                }
                if (args[0].equalsIgnoreCase("reload")){
                    sender.sendMessage(ChatColor.GREEN + "Reloaded config!");
                    ConfigOptions.reloadOptions();
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String s, String[] args) {
        List<String> tab = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("notskills")){
            if (args.length == 1){
                tab.add("wand");
                tab.add("trident");
                tab.add("sword");
                tab.add("bow");
                tab.add("reload");
            }
        }
        return tab;
    }
}
