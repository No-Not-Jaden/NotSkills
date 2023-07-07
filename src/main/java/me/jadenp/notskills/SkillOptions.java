package me.jadenp.notskills;

import me.jadenp.notskills.BuiltInSkills.EventTrigger;
import me.jadenp.notskills.BuiltInSkills.SpecificSkills.*;
import me.jadenp.notskills.MythicMobs.MythicMobsOptions;
import me.jadenp.notskills.utils.ConfigOptions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.jadenp.notskills.utils.ConfigOptions.*;
import static me.jadenp.notskills.utils.Language.magicFail;

public class SkillOptions {
    private final String name;
    private final double cooldown;
    private final List<String> actions;
    private final List<String> allowedItems;
    private final List<String> description;
    private final MythicMobsOptions mythicMobsOptions;

    public SkillOptions(ConfigurationSection config){
        name = color(config.getString("name"));
        cooldown = config.getDouble("cooldown");
        actions = config.getStringList("actions");
        allowedItems = config.getStringList("allowed-items");
        description = config.getStringList("description");

        MythicMobsOptions mythicMobsOptions = null;
        if (config.isConfigurationSection("mythic-mobs")){
            mythicMobsOptions = new MythicMobsOptions(Objects.requireNonNull(config.getConfigurationSection("mythic-mobs")));
        }
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
        long delayTicks = 0;
        Player castingPlayer = null;
        if (entity instanceof Player)
            castingPlayer = (Player)  entity;

        for (String action : actions) {
            final Player player = castingPlayer;
            if (action.startsWith("[delay] ")){
                try {
                    delayTicks+= Long.parseLong(action.substring(8));
                } catch (NumberFormatException e){
                    Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have \"{target<x>}\" formatted properly.");
                }
                continue;
            }
            new BukkitRunnable() {

                @Override
                public void run() {
                    String dimension = entity.getWorld().getEnvironment().toString();
                    if (dimension.equals("NORMAL") || dimension.equals("CUSTOM"))
                        dimension = "overworld";

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
                            if (!command.contains("execute in ")) {
                                String world = multiVerseEnabled ? "minecraft:" + entity.getWorld().getName() : dimension;
                                if (command.substring(0, 7).equalsIgnoreCase("execute")) {
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
                            if (mythicMobsEnabled) {
                                Location spawnLocation = entity.getLocation();
                                String mythicMob = command;
                                if (command.contains(" ")) {
                                    // prob has location
                                    mythicMob = command.substring(0, command.indexOf(" "));
                                    try {
                                        String pos = command.substring(command.indexOf(" ") + 1);
                                        int x = Integer.parseInt(pos.substring(0, pos.indexOf(" ")));
                                        int y = Integer.parseInt(pos.substring(pos.indexOf(" ") + 1, pos.lastIndexOf(" ")));
                                        int z = Integer.parseInt(pos.substring(pos.lastIndexOf(" ") + 1));
                                        spawnLocation = new Location(entity.getWorld(), x, y, z);
                                    } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
                                    }
                                }
                                if (!mythicAPI.spawnMob(mythicMob, spawnLocation, 1))
                                    Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " could not spawn " + mythicMob + ".");
                            }
                            break;
                        case "[mythicskills]":
                            if (mythicMobsEnabled) {
                                mythicAPI.castSkill(entity, command);
                            }
                            break;
                        case "[magic]":
                            if (magicAPIEnabled) {
                                String[] split = command.split(" ");
                                if (split.length == 0) {
                                    Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have a spell declared after [magic].");
                                    return;
                                }
                                CommandSender sender = player != null ? player : Bukkit.getConsoleSender();
                                String spellName = split[0];
                                boolean success;
                                try {
                                    System.arraycopy(split, 1, split, 0, split.length);
                                    success = magicAPI.castSpell(spellName, split, sender, entity);
                                } catch (IndexOutOfBoundsException e) {
                                    success = magicAPI.castSpell(spellName, new String[0], sender, entity);
                                }
                                if (!success && player != null)
                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(magicFail));

                            }
                            break;
                        case "[notskill]":
                            String[] split = command.split(" ");
                            switch (split[0].toUpperCase()) {
                                case "ABSORB":
                                    try {
                                        EventTrigger.addSkill(entity.getUniqueId(), new Absorb(entity, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Boolean.parseBoolean(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]), Double.parseDouble(split[6]), Double.parseDouble(split[7]), Integer.parseInt(split[8])));
                                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have correct arguments for [notskill] Absorb");
                                    }
                                    break;
                                case "ARIELSTRIKE":
                                    try {
                                        EventTrigger.addSkill(entity.getUniqueId(), new ArielStrike(entity, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Boolean.parseBoolean(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]), Integer.parseInt(split[6]), Integer.parseInt(split[7])));
                                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have correct arguments for [notskill] ArielStrike");
                                    }
                                    break;
                                case "BLOODSACRIFICE":
                                    try {
                                        new BloodSacrifice(entity, Integer.parseInt(split[1]), Double.parseDouble(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));
                                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have correct arguments for [notskill] BloodSacrifice");
                                    }
                                    break;
                                case "DASH":
                                    try {
                                        new Dash(entity, Integer.parseInt(split[1]), Double.parseDouble(split[2]));
                                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have correct arguments for [notskill] Dash");
                                    }
                                    break;
                                case "ICESHARDS":
                                    try {
                                        new IceShards(entity, Integer.parseInt(split[1]), Double.parseDouble(split[2]));
                                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have correct arguments for [notskill] IceShards");
                                    }
                                    break;
                                case "SNIPE":
                                    try {
                                        EventTrigger.addSkill(entity.getUniqueId(), new Snipe(entity, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Boolean.parseBoolean(split[3]), Integer.parseInt(split[4]), Double.parseDouble(split[5])));
                                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                                        Bukkit.getLogger().warning("Skill Action for " + name + " run by " + entity.getType() + " does not have correct arguments for [notskill] Snipe");
                                    }
                                    break;
                            }
                            break;
                    }

                }
            }.runTaskLater(NotSkills.getInstance(), delayTicks);
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


    public String getName() {
        return name;
    }

    public MythicMobsOptions getMythicMobsOptions() {
        return mythicMobsOptions;
    }
}
