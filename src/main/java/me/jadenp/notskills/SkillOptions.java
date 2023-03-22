package me.jadenp.notskills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.jadenp.notskills.ConfigOptions.*;

public class SkillOptions {
    private final String name;
    private final double cooldown;
    private final List<String> actions;
    private final List<String> allowedItems;
    private final List<String> description;

    public SkillOptions(String name, double cooldown, List<String> actions, @Nullable List<String> allowedItems, List<String> description) {

        this.name = name;
        this.cooldown = cooldown;
        this.actions = actions;
        this.allowedItems = allowedItems;
        this.description = description;
    }

    public boolean isAllowedItem(Material material) {
        if (allowedItems == null || allowedItems.isEmpty())
            return true;
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

    public void runActions(Player player) {
        for (String action : actions) {
            // get first action type
            String type = action.substring(0, action.indexOf(" "));
            String command = action.substring(type.length() + 1);
            command = ConfigOptions.getPlaceholders(command, player);
            command = command.replaceAll("\\{player}", player.getName());
            while (command.contains("{target")) {
                String distance = command.substring(command.indexOf("{target") + 7, command.substring(command.indexOf("{target") + 7).indexOf("}") + command.indexOf("{target") + 7);
                int d;
                try {
                    d = Integer.parseInt(distance);
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().warning("Skill Action for " + name + " run by " + player.getName() + " does not have \"{target<x>}\" formatted properly.");
                    continue;
                }
                Block target = player.getTargetBlock(null, d);
                command = command.substring(0, command.indexOf("{target")) + target.getX() + " " + target.getY() + " " + target.getZ() + command.substring(command.indexOf("{target") + distance.length() + 8);
            }
            while (command.contains("{random")) {
                String bounds = command.substring(command.indexOf("{random") + 7, command.substring(command.indexOf("{random") + 7).indexOf("}") + command.indexOf("{random") + 7);
                int upperBounds;
                int lowerBounds = 0;
                if (bounds.contains("_")) {
                    try {
                        upperBounds = Integer.parseInt(bounds.substring(bounds.indexOf("_") + 1));
                        lowerBounds = Integer.parseInt(bounds.substring(0, bounds.indexOf("_")));
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + player.getName() + " does not have \"{random<y>_<x>}\" formatted properly.");
                        continue;
                    }
                } else {
                    try {
                        upperBounds = Integer.parseInt(bounds);
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + player.getName() + " does not have \"{random<x>}\" formatted properly.");
                        continue;
                    }
                }

                int rand = (int) ((Math.random() * (upperBounds - lowerBounds)) - lowerBounds);
                command = command.substring(0, command.indexOf("{random")) + rand + command.substring(command.indexOf("{random") + bounds.length() + 8);
                command = ConfigOptions.color(command);
            }
            switch (type) {
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

    public ItemStack getDisplayItem(int state) {
        ItemStack itemStack = new ItemStack(Material.STRUCTURE_VOID);
        if (state == 0) {
            itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        } else if (state == 1) {
            itemStack = new ItemStack(Material.BOOK);
        }
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        if (state == 0 || state == 1) {
            meta.setDisplayName(color(name));
            List<String> lore = new ArrayList<>();
            for (String str : description) {
                lore.add(color(str));
            }
            meta.setLore(lore);
        }
        if (state == 2) {
            meta.setDisplayName(color(name));
            List<String> lore = new ArrayList<>();
            for (String str : description) {
                StringBuilder builder = new StringBuilder(ChatColor.GRAY + "");
                for (int i = 0; i < str.length(); i++) {
                    if ((int) (Math.random() * 2) == 1){
                        builder.append(ChatColor.MAGIC);
                    } else {
                        builder.append(ChatColor.GRAY);
                    }
                    builder.append("*");
                }
                lore.add(builder.toString());
            }
            meta.setLore(lore);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
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
