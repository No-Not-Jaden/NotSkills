package me.jadenp.notskills;



import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.jadenp.notskills.ConfigOptions.*;

public class Events  implements Listener {
    public Map<UUID, ArrayList<Long>> casting = new HashMap<>(); // used for timed clicks
    public Map<UUID, LocLong> lastClicks = new HashMap<>();

    private static final boolean[][] registeredTriggers = new boolean[][]{
            {false, false, false},
            {true, false, true},
            {true, true, false},
            {true, false, false}
    };

    public SkillEffects skillEffects;

    public Events() {
        skillEffects = new SkillEffects();
        Bukkit.getPluginManager().registerEvents(skillEffects, NotSkills.getInstance());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getPlayer().isSneaking()) { // so you don't accidentally cast a spell or skill
                if (isMagicItem(event.getItem())) {
                    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        if (defaultSST == 0) {
                            ArrayList<Long> clickTimes;
                            // grab previous clicks
                            if (casting.containsKey(event.getPlayer().getUniqueId())) {
                                clickTimes = casting.get(event.getPlayer().getUniqueId());
                                // clear up expired clicks
                                while (clickTimes.size() > 0 && clickTimes.get(0) + expireMS < System.currentTimeMillis()) {
                                    Bukkit.getLogger().info(clickTimes.get(0) + " | " + expireMS + " | " + System.currentTimeMillis());
                                    clickTimes.remove(0);

                                }
                            } else {
                                clickTimes = new ArrayList<>();
                            }
                            clickTimes.add(System.currentTimeMillis());

                            // check if they have enough to cast a spell
                            if (clickTimes.size() >= 4) {
                                // get the time paused between the first 4 clicks
                                long[] spaces = new long[3];
                                for (int i = 1; i < 4; i++) {
                                    spaces[i - 1] = clickTimes.get(i) - clickTimes.get(i - 1);
                                }
                                clickTimes.clear();
                                // convert the spaces into either long or short spaces
                                boolean[] trigger = new boolean[3]; // true is a big space
                                boolean knownSizes = false; // if we know what a big and small space look like
                                for (int i = 1; i < trigger.length; i++) {
                                    if (!knownSizes) {
                                        if (spaces[i] * pauseRatio < spaces[i - 1]) {
                                            // [i-1] is a big space and [i] is a small space
                                            knownSizes = true;
                                            for (int j = 0; j < i; j++) {
                                                trigger[j] = true;
                                            }
                                        } else if (spaces[i - 1] * pauseRatio < spaces[i]) {
                                            // [i] is a big space and [i-1] is a small space
                                            trigger[i] = true;
                                            knownSizes = true;
                                        }
                                    } else {
                                        if (trigger[i - 1]) {
                                            // [i-1] is a big space
                                            if (spaces[i] * pauseRatio >= spaces[i - 1]) {
                                                // [i] is a big space
                                                trigger[i] = true;
                                            }
                                        } else {
                                            // [i-1] is a small space
                                            if (spaces[i - 1] * pauseRatio < spaces[i]) {
                                                // [i] is a big space
                                                trigger[i] = true;
                                            }
                                        }
                                    }
                                }
                                // check with registered triggers to see if the pattern is valid
                                int index = checkPattern(trigger);
                                if (index != -1) {
                                    useSkill(event.getPlayer(), event.getItem(), index);
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.GOLD + "Not a valid pattern.");
                                }
                                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_ARROW_HIT_PLAYER, 1,1);
                            } else {
                                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_LEASH_KNOT_BREAK, 1,1);
                            }
                            if (casting.containsKey(event.getPlayer().getUniqueId())) {
                                casting.replace(event.getPlayer().getUniqueId(), clickTimes);
                            } else {
                                casting.put(event.getPlayer().getUniqueId(), clickTimes);
                            }
                        } else if (defaultSST == 1) {
                            Location clickLocation = event.getPlayer().getEyeLocation().add(event.getPlayer().getEyeLocation().getDirection());
                            if (lastClicks.containsKey(event.getPlayer().getUniqueId())) {
                                LocLong lastClick = lastClicks.get(event.getPlayer().getUniqueId());
                                if (lastClick.getLong() + expireMS < System.currentTimeMillis()) {
                                    lastClick = new LocLong(clickLocation, System.currentTimeMillis());
                                    lastClicks.replace(event.getPlayer().getUniqueId(), lastClick);
                                } else {
                                    Vector p2first = lastClick.getLocation().toVector().subtract(event.getPlayer().getEyeLocation().toVector());
                                    Vector p2second = clickLocation.toVector().subtract(event.getPlayer().getEyeLocation().toVector());
                                    double yaw = getYawAngle(p2first, p2second);
                                    boolean left = getRelativeVector(p2first, p2second).equals("l");
                                    double yDiff = clickLocation.getY() - lastClick.getLocation().getY();
                                    if (Math.abs(yDiff) > yaw){
                                        // up or down
                                        if (yDiff > 0){
                                            // up
                                            useSkill(event.getPlayer(), event.getItem(), 0);
                                        } else {
                                            // down
                                            useSkill(event.getPlayer(), event.getItem(), 2);
                                        }
                                    } else {
                                        if (left){
                                            //left
                                            useSkill(event.getPlayer(), event.getItem(), 3);
                                        } else {
                                            // right
                                            useSkill(event.getPlayer(), event.getItem(), 1);
                                        }
                                    }

                                    event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_ARROW_HIT_PLAYER, 1,1);
                                    lastClicks.remove(event.getPlayer().getUniqueId());
                                }
                            } else {
                                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_LEASH_KNOT_BREAK, 1,1);
                                LocLong lastClick = new LocLong(clickLocation, System.currentTimeMillis());
                                lastClicks.put(event.getPlayer().getUniqueId(), lastClick);
                            }
                        }
                    }
                }
            }
    }

}

    public void useSkill(Player player, ItemStack hand, int slot){
        // check which skill type it should be
        if (hand.isSimilar(wand))
            Spells.castIndexSpell(slot, player);
        if (hand.isSimilar(sword))
            skillEffects.addSkill(player, slot);
        if (hand.isSimilar(bow))
            skillEffects.addSkill(player, slot + 4);
        if (hand.isSimilar(trident))
            skillEffects.addSkill(player, slot + 8);
        // lil notification message
        player.sendMessage(ChatColor.GREEN + "You have triggered slot: " + (slot + 1));
        String message = ChatColor.GREEN + "Skill Slot " + (slot + 1) + " Activated";
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    private double getYawAngle(Vector v1, Vector v2){
        double x = v1.getX();
        double z = v1.getZ();
        double x2 = v2.getX();
        double z2 = v2.getZ();
        return Math.acos((x*x2 + z*z2) / (Math.sqrt(Math.pow(x, 2)+Math.pow(z, 2)) * Math.sqrt(Math.pow(x2, 2)+Math.pow(z2, 2))));
    }
    public static float getLookAtYaw(Vector motion) {
        double dx = motion.getX();
        double dz = motion.getZ();
        double yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }
        return (float) (yaw * 180 / Math.PI);
    }
    // comparing where v2 is relative to v1
    public String getRelativeVector(Vector v1, Vector v2){
        double a1 = getLookAtYaw(v2);
        double a2 = a1 + 360;
        double a3 = a1 - 360;
        double v = getLookAtYaw(v1);
        double d1 = v - a1;
        double d2 = v - a2;
        double d3 = v - a3;
        if (Math.abs(d1) < Math.abs(d2) && Math.abs(d1) < Math.abs(d3)){
            if (d1 > 0){
                return "r";
            } else {
                return "l";
            }
        } else if (Math.abs(d2) < Math.abs(d1) && Math.abs(d2) < Math.abs(d3)){
            if (d2 > 0){
                return "r";
            } else {
                return "l";
            }
        } else if (Math.abs(d3) < Math.abs(d1) && Math.abs(d3) < Math.abs(d2)){
            if (d3 > 0){
                return "r";
            } else {
                return "l";
            }
        } else {
            return "i";
        }
    }

    private int checkPattern(boolean[] trigger){
        for (int i = 0; i < registeredTriggers.length; i++) {
            for (int j = 0; j < registeredTriggers[0].length; j++) {
                if (registeredTriggers[i][j] != trigger[j]){
                    break;
                }
                if (j == trigger.length - 1){
                    // match
                    return i;
                }
            }
        }
        return -1;
    }

}
