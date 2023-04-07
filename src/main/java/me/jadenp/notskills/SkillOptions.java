package me.jadenp.notskills;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.jadenp.notskills.MythicMobs.MythicMobsOptions;
import me.jadenp.notskills.utils.ConfigOptions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.jadenp.notskills.utils.ConfigOptions.*;

public class SkillOptions {
    private final String name;
    private final double cooldown;
    private final List<String> actions;
    private final List<String> allowedItems;
    private final List<String> description;
    private final MythicMobsOptions mythicMobsOptions;

    public SkillOptions(String name, double cooldown, List<String> actions, @Nullable List<String> allowedItems, List<String> description, @Nullable MythicMobsOptions mythicMobsOptions) {

        this.name = name;
        this.cooldown = cooldown;
        this.actions = actions;
        this.allowedItems = allowedItems;
        this.description = description;
        this.mythicMobsOptions = mythicMobsOptions;
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

    public void runActions(LivingEntity entity) {
        Player player = null;
        if (entity instanceof Player)
            player = (Player)  entity;
        String dimension = entity.getWorld().getEnvironment().toString();
        if (dimension.equals("NORMAL") || dimension.equals("CUSTOM"))
            dimension = "overworld";
        for (String action : actions) {
            // get first action type
            String type = action.substring(0, action.indexOf(" "));
            String command = action.substring(type.length() + 1);
            if (player != null) {
                command = ConfigOptions.getPlaceholders(command, player);
                command = command.replaceAll("\\{player}", player.getName());
            } else {
                command = command.replaceAll("\\{player}", "@e[type=minecraft:" + entity.getType().toString().toLowerCase() + ",limit=1,x=" + entity.getLocation().getX() + ",y=" + entity.getLocation().getY() + ",z=" + entity.getLocation().getZ() + ", distance=...01]");
            }
            command = command.replaceAll("\\{dimension}", dimension);
            command = command.replace("\\{world}", entity.getWorld().getName());
            while (command.contains("{target")) {
                String distance = command.substring(command.indexOf("{target") + 7, command.substring(command.indexOf("{target") + 7).indexOf("}") + command.indexOf("{target") + 7);
                int d;
                try {
                    d = Integer.parseInt(distance);
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have \"{target<x>}\" formatted properly.");
                    continue;
                }
                Block target = entity.getTargetBlock(null, d);
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
                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have \"{random<y>_<x>}\" formatted properly.");
                        continue;
                    }
                } else {
                    try {
                        upperBounds = Integer.parseInt(bounds);
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have \"{random<x>}\" formatted properly.");
                        continue;
                    }
                }

                int rand = (int) ((Math.random() * (upperBounds - lowerBounds)) + lowerBounds);
                command = command.substring(0, command.indexOf("{random")) + rand + command.substring(command.indexOf("{random") + bounds.length() + 8);
                if (player == null)
                    if (!command.contains("execute in ")){
                        String world = multiVerseEnabled ? "minecraft:" + entity.getWorld().getName() : dimension;
                        if (command.substring(0, 7).equalsIgnoreCase("execute")){
                            command = command.substring(0, 7) + " in " + world + command.substring(7);
                        } else {
                            command = "execute in " + world + " " + command;
                        }
                    }
            }
            command = ConfigOptions.color(command);
            switch (type) {
                case "[console]":
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                    break;
                case "[player]":
                    if (player != null)
                        Bukkit.getServer().dispatchCommand(player, command);
                    break;
                case "[message]":
                    if (player != null)
                        player.sendMessage(command);
                    break;
                case "[mythicmobs]":
                    if (mythicMobsEnabled){
                        Location spawnLocation = entity.getLocation();
                        String mythicMob = command;
                        if (command.contains(" ")){
                            // prob has location
                            mythicMob = command.substring(0, command.indexOf(" "));
                            try {
                                String pos = command.substring(command.indexOf(" ") + 1);
                                int x = Integer.parseInt(pos.substring(0, pos.indexOf(" ")));
                                int y = Integer.parseInt(pos.substring(pos.indexOf(" ") + 1, pos.lastIndexOf(" ")));
                                int z = Integer.parseInt(pos.substring(pos.lastIndexOf(" ") + 1));
                                spawnLocation = new Location(entity.getWorld(), x, y, z);
                            } catch (IndexOutOfBoundsException | NumberFormatException ignored){}
                        }
                        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(mythicMob).orElse(null);
                        if(mob != null){
                            // spawns mob
                            mob.spawn(BukkitAdapter.adapt(spawnLocation),1);
                        } else {
                            Bukkit.getLogger().warning("Could not spawn Mythic Mob!");
                        }
                    }
                    break;
                case "[mythicskills]":
                    if (mythicMobsEnabled) {
                        MythicBukkit.inst().getAPIHelper().castSkill(entity, command);
                    }
                    break;
                case "[magic]":
                    if (magicAPIEnabled){
                        String[] split = command.split(" ");
                        if (split.length == 0){
                            Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have a spell declared after [magic].");
                            return;
                        }
                        String spellName = split[0];
                        try {
                            System.arraycopy(split, 1, split, 0, split.length);
                            magicAPI.cast(spellName, split);
                        } catch (IndexOutOfBoundsException e){
                            magicAPI.cast(spellName, new String[0]);
                        }

                    }
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

    public MythicMobsOptions getMythicMobsOptions() {
        return mythicMobsOptions;
    }
}
