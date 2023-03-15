package me.jadenp.notskills;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SkillOptions {
    private final String name;
    private final double cooldown;
    private final List<String> actions;
    private final List<String> allowedItems;

    public SkillOptions(String name, double cooldown, List<String> actions, @Nullable List<String> allowedItems) {

        this.name = name;
        this.cooldown = cooldown;
        this.actions = actions;
        this.allowedItems = allowedItems;
    }

    public boolean isAllowedItem(Material material) {
        if (allowedItems != null)
            for (String item : allowedItems) {
                if (item.charAt(0) == '*') {
                    if (material.toString().equals(item.substring(1).toUpperCase())) {
                        return true;
                    }
                    continue;
                }
                if (material.toString().contains(item.toUpperCase())) {
                    return true;
                }
            }
        return false;
    }

    public void runActions(Player player){
        for (String action : actions){
            // get first action type
            String type = action.substring(0, action.indexOf(" "));
            String command = action.substring(type.length() + 1);
            command = ConfigOptions.getPlaceholders(command, player);
            command = command.replaceAll("\\{player}", player.getName());
            while (command.contains("{target")){
                String distance = command.substring(command.indexOf("{target") + 7, command.substring(command.indexOf("{target") + 7).indexOf("}") + command.indexOf("{target") + 7);
                int d;
                try {
                   d = Integer.parseInt(distance);
                } catch (NumberFormatException e){
                    Bukkit.getLogger().warning("Skill Action for " + name + " run by " + player.getName() + " does not have \"{target<x>}\" formatted properly.");
                    continue;
                }
                Block target = player.getTargetBlock(null, d);
                command = command.substring(0, command.indexOf("{target")) + target.getX() + " " + target.getY() + " " +  target.getZ() + command.substring(command.indexOf("{target") + distance.length() + 8);
            }
            while (command.contains("{random")){
                String bounds = command.substring(command.indexOf("{random") + 7, command.substring(command.indexOf("{random") + 7).indexOf("}") + command.indexOf("{random") + 7);
                int upperBounds = 1;
                int lowerBounds = 0;
                if (bounds.contains("_")){
                    try {
                        upperBounds = Integer.parseInt(bounds.substring(bounds.indexOf("_") + 1));
                        lowerBounds = Integer.parseInt(bounds.substring(0, bounds.indexOf("_")));
                    } catch (NumberFormatException e){
                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + player.getName() + " does not have \"{random<y>_<x>}\" formatted properly.");
                        continue;
                    }
                } else {
                    try {
                        upperBounds = Integer.parseInt(bounds);
                    } catch (NumberFormatException e){
                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + player.getName() + " does not have \"{random<x>}\" formatted properly.");
                        continue;
                    }
                }

                int rand = (int) ((Math.random() * (upperBounds - lowerBounds)) - lowerBounds);
                command = command.substring(0, command.indexOf("{random")) + rand + command.substring(command.indexOf("{random") + bounds.length() + 8);
                command = ConfigOptions.color(command);
            }
            switch (type){
                case "[console]":
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                    break;
                case "[player]":
                    Bukkit.getServer().dispatchCommand(player, command);
                    break;
                case "[message]":
                    player.sendMessage(command);
                    break;
            }
        }
    }

    public double getCooldown() {
        return cooldown;
    }

    public List<String> getActions() {
        return actions;
    }

    public List<String> getAllowedItems() {
        return allowedItems;
    }

    public String getName() {
        return name;
    }
}
