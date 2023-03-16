package me.jadenp.notskills;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.jadenp.notskills.Items.*;
import static me.jadenp.notskills.ConfigOptions.*;

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
                    if (playersChooseSST) {
                        try {
                            Trigger trigger = Trigger.valueOf(args[1].toUpperCase());
                            SkillTrigger.getInstance().setTrigger(((Player) sender), trigger);
                            sender.sendMessage(prefix + ChatColor.GREEN + "Changed your skill select type to " + ChatColor.DARK_GREEN + trigger);
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage(prefix + ChatColor.RED + "Unknown skill select type!");
                            return true;
                        }
                    } else {
                        sender.sendMessage(prefix + ChatColor.YELLOW + "Choosing your own skill select type is not enabled.");
                    }

                }
            }
        }
        if (!sender.hasPermission("notskills.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to access this command.");
            return true;
        }
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                reloadOptions();
                sender.sendMessage(prefix + ChatColor.YELLOW + "Reloaded NotSkills version " + NotSkills.getInstance().getDescription().getVersion() + "!");
            } else if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(prefix + ChatColor.YELLOW + "Here are a list of commands:");
                sender.sendMessage(ChatColor.BLUE + "/ns add (amount) <player>" + ChatColor.GRAY + " <=> " + ChatColor.DARK_AQUA + "Adds a skill slot to held item.");
                sender.sendMessage(ChatColor.BLUE + "/ns remove <amount> <player>" + ChatColor.GRAY + " <=> " + ChatColor.DARK_AQUA + "Removes a skill slot from held item.");
                sender.sendMessage(ChatColor.BLUE + "/ns give (player) (item) (amount) (skill slots)" + ChatColor.GRAY + " <=> " + ChatColor.DARK_AQUA + "Gives a player an item with skill slots");
                sender.sendMessage(ChatColor.BLUE + "/ns select (type)" + ChatColor.GRAY + " <=> " + ChatColor.DARK_AQUA + "Changes skill select type");
            }
            if (args[0].equalsIgnoreCase("give")) {
                if (args.length >= 3) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage(prefix + ChatColor.RED + "Unknown player!");
                        return true;
                    }

                    boolean artifact = false;
                    // get material
                    Material m = args[2].contains("{") ? Material.getMaterial(args[2].substring(0, args[2].indexOf("{")).toUpperCase()) : Material.getMaterial(args[2]);
                    if (m == null) {
                        if (args[2].equalsIgnoreCase("artifact")){
                            m = Material.PAPER;
                            artifact = true;
                        } else {
                            sender.sendMessage(prefix + ChatColor.RED + "Unknown item!");
                            return true;
                        }

                    }

                    ItemStack item = artifact ? skillSlotArtifact : new ItemStack(m);

                    if (args[2].contains("{")) {
                        // has nbt
                        try {
                            NBTItem nbti = new NBTItem(item);
                            ReadWriteNBT nbt = NBT.parseNBT(args[2].substring(args[2].indexOf("{")));
                            nbti.mergeCompound(nbt);

                            item = nbti.getItem();
                        } catch (NbtApiException e) {
                            sender.sendMessage(prefix + ChatColor.RED + "NBT Error!");
                            return true;
                        }
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

                    if (!artifact) {
                        // add skill slot lore
                        ItemMeta meta = item.getItemMeta();
                        assert meta != null;
                        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>(); // create lore if needed
                        assert lore != null;
                        Skills skill = new Skills(lore);
                        skill.addSkillSlots(ssAmount);
                        meta.setLore(skill.getLore());
                        item.setItemMeta(meta);
                    }

                    // give player item
                    givePlayer(player, item);
                    sender.sendMessage(prefix + ChatColor.GREEN + "Gave " + ChatColor.DARK_GREEN + player.getName() + amount + ChatColor.GREEN + "x " + ChatColor.DARK_GREEN + m + ChatColor.GREEN + ".");
                }
            }

            if (args[0].equalsIgnoreCase("add")) {
                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(prefix + ChatColor.RED + "Not a valid amount!");
                    return true;
                }

                if (args.length == 2){
                    // using another player
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null){
                        sender.sendMessage(prefix + ChatColor.RED + "Unknown player!");
                        return true;
                    }
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item.getType().isAir()) {
                        sender.sendMessage(prefix + ChatColor.RED + "You cannot add a skill to air!");
                    }
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    List<String> lore = meta.hasLore() ? new Skills(Objects.requireNonNull(meta.getLore())).addSkillSlots(amount).getLore() : new Skills(new ArrayList<>()).addSkillSlots(amount).getLore();
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    player.getInventory().setItemInMainHand(item);
                    player.updateInventory();

                } else {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(prefix + ChatColor.RED + "Only players can use this version of the command!");
                        return true;
                    }
                    ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                    if (item.getType().isAir()) {
                        sender.sendMessage(prefix + ChatColor.RED + "You cannot add a skill to air!");
                    }
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    List<String> lore = meta.hasLore() ? new Skills(Objects.requireNonNull(meta.getLore())).addSkillSlots(amount).getLore() : new Skills(new ArrayList<>()).addSkillSlots(amount).getLore();
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    ((Player)sender).getInventory().setItemInMainHand(item);
                    ((Player)sender).updateInventory();
                }
            }


            if (args[0].equalsIgnoreCase("remove")) {
                if (args.length == 2) {
                    // remove a specific amount or from a player
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        int amount;
                        try {
                            amount = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(prefix + ChatColor.RED + "Not a valid amount!");
                            return true;
                        }

                    } else {

                    }
                } else {
                    // remove all

                }
            }
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String s, String[] args) {
        List<String> tab = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("notskills")) {
            if (args.length == 1) {
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
