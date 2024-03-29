package me.jadenp.notskills;

import me.jadenp.notskills.ItemTrigger.SkillTrigger;
import me.jadenp.notskills.ItemTrigger.Trigger;
import me.jadenp.notskills.utils.NBTClass;
import me.jadenp.notskills.utils.Skills;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

import static me.jadenp.notskills.utils.Items.*;
import static me.jadenp.notskills.utils.ConfigOptions.*;
import static me.jadenp.notskills.utils.Language.*;

public class Commands implements CommandExecutor, TabCompleter {
    public Commands() {
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String s, @NonNull String[] args) {
        if (!command.getName().equalsIgnoreCase("notskills"))
            return true;
        if (sender.hasPermission("notskills.sst")){
            if (args.length > 1){
                if (args[0].equalsIgnoreCase("select")){
                    if (!(sender instanceof Player)){
                        sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                        return true;
                    }

                    if (playersChooseSST) {
                        try {
                            Trigger trigger = Trigger.valueOf(args[1].toUpperCase());
                            SkillTrigger.getInstance().setTrigger(((Player) sender), trigger);
                            sender.sendMessage(parse(prefix + sstChange, (Player) sender, trigger));
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage(parse(prefix + unknownSst, (Player) sender));
                            return true;
                        }
                    } else {
                        sender.sendMessage(parse(prefix + sstDisabled, (Player) sender));
                    }
                    return true;
                }
            }
        }
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(prefix + ChatColor.YELLOW + "Here are a list of commands:");
                if (sender.hasPermission("notskills.admin")) {
                    sender.sendMessage(ChatColor.BLUE + "/skill add (amount) <player>" + ChatColor.GRAY + " <=> " + ChatColor.DARK_AQUA + "Adds a skill slot to held item.");
                    sender.sendMessage(ChatColor.BLUE + "/skill remove <amount> <player>" + ChatColor.GRAY + " <=> " + ChatColor.DARK_AQUA + "Removes a skill slot from held item.");
                    sender.sendMessage(ChatColor.BLUE + "/skill give (player) (item) (amount) (skill slots)" + ChatColor.GRAY + " <=> " + ChatColor.DARK_AQUA + "Gives a player an item with skill slots.");
                    sender.sendMessage(ChatColor.BLUE + "/skill unlock (player) (skill)" + ChatColor.GRAY + " <=> " + ChatColor.DARK_AQUA + "Unlocks a skill.");
                    sender.sendMessage(ChatColor.BLUE + "/skill lock (player) (skill)" + ChatColor.GRAY + " <=> " + ChatColor.DARK_AQUA + "Locks a skill.");
                }
                if (sender.hasPermission("notskills.sst")) {
                    sender.sendMessage(ChatColor.BLUE + "/skill select (type)" + ChatColor.GRAY + " <=> " + ChatColor.DARK_AQUA + "Changes skill select type.");
                }
                sender.sendMessage(ChatColor.BLUE + "/skill" + ChatColor.GRAY + " <=> " + ChatColor.DARK_AQUA + "Opens skill menu for the held item.");
                return true;
            }
            if (!sender.hasPermission("notskills.admin")) {
                sender.sendMessage(noPermission);
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                reloadOptions();
                reloadLanguage();
                sender.sendMessage(prefix + ChatColor.YELLOW + "Reloaded NotSkills version " + NotSkills.getInstance().getDescription().getVersion() + "!");
            } else if (args[0].equalsIgnoreCase("debug")){
                debug = !debug;
                sender.sendMessage(prefix + ChatColor.YELLOW + "Debug is not set to " + debug + ".");
            } else if (args[0].equalsIgnoreCase("give")) {
                if (args.length >= 3) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage(parse(prefix + unknownPlayer, null));
                        return true;
                    }

                    boolean artifact = false;
                    // get material
                    Material m = args[2].contains("{") ? Material.getMaterial(args[2].substring(0, args[2].indexOf("{")).toUpperCase()) : Material.getMaterial(args[2].toUpperCase());
                    if (m == null) {
                        if (args[2].equalsIgnoreCase("artifact")){
                            m = Material.PAPER;
                            artifact = true;
                        } else {
                            sender.sendMessage(parse(prefix + unknownItem, player));
                            return true;
                        }

                    }

                    ItemStack item = artifact ? skillSlotArtifact : new ItemStack(m);

                    if (args[2].contains("{")) {
                        // has nbt
                        if (NBTAPIEnabled)
                            item = NBTClass.addNBTToItem(item, args[2].substring(args[2].indexOf("{")));
                    }

                    // get item amount
                    int amount = 1;
                    if (args.length >= 4) {
                        try {
                            amount = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(prefix + ChatColor.RED + "Not a valid item amount!");
                            return true;
                        }
                        item.setAmount(amount);
                    }

                    // get skill slot amount
                    int ssAmount = 1;
                    if (args.length == 5) {
                        try {
                            ssAmount = Integer.parseInt(args[4]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(prefix + ChatColor.RED + "Not a valid skill slot amount!");
                            return true;
                        }
                    }
                    if (ssAmount < 0){
                        sender.sendMessage(prefix + ChatColor.RED + "You cannot have negative skills!");
                        return true;
                    }

                    if (!artifact) {
                        // add skill slot lore
                        ItemMeta meta = item.getItemMeta();
                        assert meta != null;
                        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>(); // create lore if needed
                        assert lore != null;
                        Skills skill = new Skills(lore);
                        skill.addSkillSlots(ssAmount);
                        if (skill.getUsedSkillSlots() + skill.getEmptySkillSlots() > maxSkillSlots){
                           sender.sendMessage(prefix + ChatColor.RED + "Skill slots capped at: " + ChatColor.DARK_RED + maxSkillSlots);
                            skill.setSkillSlots(maxSkillSlots);
                        }
                        meta.setLore(skill.getLore());
                        item.setItemMeta(meta);
                    }

                    // give player item
                    givePlayer(player, item);
                    sender.sendMessage(prefix + ChatColor.GREEN + "Gave " + ChatColor.DARK_GREEN + player.getName() + " " + amount + ChatColor.GREEN + "x " + ChatColor.DARK_GREEN + m + ChatColor.GREEN + ".");
                }
            } else if (args[0].equalsIgnoreCase("add")) {
                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(prefix + ChatColor.RED + "Not a valid skill amount!");
                    return true;
                }

                if (amount < 0){
                    sender.sendMessage(prefix + ChatColor.RED + "You cannot have negative skills!");
                    return true;
                }

                Player player;
                if (args.length == 3){
                    player = Bukkit.getPlayer(args[1]);
                    if (player == null){
                        sender.sendMessage(parse(prefix + unknownPlayer, null));
                        return true;
                    }
                } else {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(prefix + ChatColor.RED + "Only players can use this version of the command!");
                        return true;
                    }
                    player = (Player) sender;
                }
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.getType().isAir()) {
                    sender.sendMessage(prefix + ChatColor.RED + "You cannot add a skill to air!");
                }
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                Skills skill = meta.hasLore() ? new Skills(Objects.requireNonNull(meta.getLore())).addSkillSlots(amount) : new Skills(new ArrayList<>()).addSkillSlots(amount);
                if (skill.getUsedSkillSlots() + skill.getEmptySkillSlots() > maxSkillSlots){
                    sender.sendMessage(prefix + ChatColor.RED + "Skill slots capped at: " + ChatColor.DARK_RED + maxSkillSlots);
                    skill.setSkillSlots(maxSkillSlots);
                }
                meta.setLore(skill.getLore());
                item.setItemMeta(meta);
                player.getInventory().setItemInMainHand(item);
                player.updateInventory();

            } else if (args[0].equalsIgnoreCase("remove")) {
                // /ns remove <amount> <player>
                if (args.length == 2) {
                    // remove a specific amount or from a player
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        // number
                        int amount;
                        try {
                            amount = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(prefix + ChatColor.RED + "Not a valid amount!");
                            return true;
                        }
                        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                        if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasLore()) {
                            ItemMeta meta = item.getItemMeta();
                            Skills skill = new Skills(Objects.requireNonNull(meta.getLore())).removeSkillSlots(amount);
                            meta.setLore(skill.getLore());
                            item.setItemMeta(meta);
                            ((Player)sender).getInventory().setItemInMainHand(item);
                        } else {
                            sender.sendMessage(parse(prefix + noSkillSlots, null));
                            return true;
                        }
                        sender.sendMessage(prefix + ChatColor.GREEN + "Successfully removed " + amount + " skills.");
                    } else {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasLore()) {
                            ItemMeta meta = item.getItemMeta();
                            Skills skill = new Skills(Objects.requireNonNull(meta.getLore())).removeAllSkills();
                            meta.setLore(skill.getLore());
                            item.setItemMeta(meta);
                            player.getInventory().setItemInMainHand(item);
                        } else {
                            sender.sendMessage(parse(prefix + noSkillSlots, player));
                            return true;
                        }
                        sender.sendMessage(prefix + ChatColor.GREEN + "Successfully removed all skills from " + player.getName() + "'s held item.");
                    }
                } else if (args.length == 3) {
                    Player player = Bukkit.getPlayer(args[2]);
                    if (player == null) {
                        sender.sendMessage(parse(prefix + unknownPlayer, null));
                    } else {
                        // number
                        int amount;
                        try {
                            amount = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(prefix + ChatColor.RED + "Not a valid amount!");
                            return true;
                        }

                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasLore()) {
                            ItemMeta meta = item.getItemMeta();
                            Skills skill = new Skills(Objects.requireNonNull(meta.getLore())).removeSkillSlots(amount);
                            meta.setLore(skill.getLore());
                            item.setItemMeta(meta);
                            player.getInventory().setItemInMainHand(item);
                        } else {
                            sender.sendMessage(parse(prefix + noSkillSlots, player));
                            return true;
                        }
                        sender.sendMessage(prefix + ChatColor.GREEN + "Successfully removed " + amount + " skills from " + player.getName() + "'s held item.");
                    }

                }
            } else if (args[0].equalsIgnoreCase("unlock") || args[0].equalsIgnoreCase("lock")){
                if (args.length > 1) {
                    Player player = null;
                    if (args.length >= 3) {
                        // could have player
                        player = Bukkit.getPlayer(args[1]);
                    }
                    StringBuilder skillName = new StringBuilder();
                    if (player == null){
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(prefix + ChatColor.RED + "Only players can use this version of the command!");
                            return true;
                        }
                        player = (Player) sender;
                        // skill is args 1-end
                        for (int i = 1; i < args.length; i++) {
                            skillName.append(args[i]).append(" ");
                        }
                    } else {
                        // skill is after player, so args2-end
                        for (int i = 2; i < args.length; i++) {
                            skillName.append(args[i]).append(" ");
                        }
                    }
                    // delete the last space if the name isn't empty - it really shouldn't be empty, but we check jic
                    if (skillName.length() > 0)
                        skillName.deleteCharAt(skillName.length() - 1);
                    SkillOptions skill = getSkill(skillName.toString());
                    if (skill == null) {
                        sender.sendMessage(prefix + ChatColor.RED + "Unknown skill!");
                        return true;
                    }
                    PlayerData playerdata = getPlayerData(player);
                    if (args[0].equalsIgnoreCase("unlock")) {
                        playerdata.setSkillUnlocked(skill.getName(), true);
                        sender.sendMessage(prefix + ChatColor.GREEN + "Unlocked " + ChatColor.DARK_GREEN + skill.getName() + ChatColor.GREEN + " for " + ChatColor.DARK_GREEN + player.getName());
                    } else {
                        playerdata.setSkillUnlocked(skill.getName(), false);
                        sender.sendMessage(prefix + ChatColor.GREEN + "Locked " + ChatColor.DARK_GREEN + skill.getName() + ChatColor.GREEN + " for " + ChatColor.DARK_GREEN + player.getName());
                    }
                } else {
                    sender.sendMessage(prefix + ChatColor.RED + "Please specify arguments.");
                }
            }
            else {
                sender.sendMessage(prefix + ChatColor.RED + "Unknown command.");
            }
        } else {
            if (sender instanceof Player) {
                if (!Skills.hasSkill(((Player) sender).getInventory().getItemInMainHand())) {
                    sender.sendMessage(parse(prefix + noSkillSlots, (Player) sender));
                    return true;
                }
                SkillsGUI.getInstance().openGUI((Player) sender, 1);
            }
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String s, String[] args) {
        List<String> tab = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("notskills")) {
            if (args.length == 1) {
                tab.add("help");
            }
            if (sender.hasPermission("notskills.admin")){
                if (args.length == 1) {
                    tab.add("add");
                    tab.add("remove");
                    tab.add("give");
                    tab.add("reload");
                    tab.add("lock");
                    tab.add("unlock");
                } else if (args.length == 2){
                    if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")){
                        tab.add("#");
                    }
                    if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("lock") || args[0].equalsIgnoreCase("unlock")){
                        for (Player player : Bukkit.getOnlinePlayers()){
                            tab.add(player.getName());
                        }
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")){
                        for (Player player : Bukkit.getOnlinePlayers()){
                            tab.add(player.getName());
                        }
                    } else if (args[0].equalsIgnoreCase("give")){
                        tab.add("artifact");
                    }
                }
                if (args.length > 1){
                    if (args[0].equalsIgnoreCase("lock") || args[0].equalsIgnoreCase("unlock")){
                        for (SkillOptions skillOptions : skills){
                            tab.add(ChatColor.stripColor((skillOptions.getName())));
                        }
                    }
                }
            }
            if (sender.hasPermission("notskills.sst")){
                if (args.length == 1) {
                    tab.add("select");
                } else if (args.length == 2){
                    if (args[0].equalsIgnoreCase("select")){
                        tab.add("LEFT_RIGHT_CLICK");
                        tab.add("CROUCH_CLICK");
                        tab.add("JUMP_CLICK");
                        tab.add("CROUCH_JUMP_CLICK");
                        tab.add("DIRECTIONAL_CLICK");
                        tab.add("TIMED_CLICK");
                        tab.add("DOUBLE_CLICK");
                        tab.add("TRIPLE_CLICK");
                        tab.add("MULTI_CLICK");
                    }
                }
            }
            String typed = args[args.length - 1];
            tab.removeIf(test -> test.toLowerCase(Locale.ROOT).indexOf(typed.toLowerCase(Locale.ROOT)) != 0);
            Collections.sort(tab);
        }
        return tab;
    }
}
