package me.jadenp.notskills;

import jdk.internal.net.http.common.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.jadenp.notskills.ConfigOptions.*;
import static me.jadenp.notskills.Items.isMagicItem;
import static me.jadenp.notskills.Items.wand;

public class Events  implements Listener {
    public Map<UUID, ArrayList<Long>> casting = new HashMap<>(); // used for timed clicks
    public Map<UUID, Pair<Location, Long>> lastClicks = new HashMap<>();

    private static final boolean[][] registeredTriggers = new boolean[][]{
            {false, false, false},
            {true, false, true},
            {true, true, false},
            {true, false, false}
    };
    public Events(){}
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if (event.getPlayer().getItemInUse() != null)
            if (event.getPlayer().isSneaking()) // so you don't accidentally cast a spell or skill
                if (isMagicItem(event.getPlayer().getItemInUse())){
                    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR) {
                        if (skillSelectType == 0) {
                            ArrayList<Long> clickTimes;
                            // grab previous clicks
                            if (casting.containsKey(event.getPlayer().getUniqueId())) {
                                clickTimes = casting.get(event.getPlayer().getUniqueId());
                                // clear up expired clicks
                                while (clickTimes.size() > 0 && clickTimes.get(0) + expireMS < System.currentTimeMillis()) {
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
                                    event.getPlayer().sendMessage(ChatColor.GREEN + "You have triggered slot: " + index + 1);
                                    if (event.getPlayer().getItemInUse().isSimilar(wand)) {
                                        Spells.castIndexSpell(index, event.getPlayer(), NotSkills.getInstance());
                                    }
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.GOLD + "Not a valid pattern.");
                                }
                            }
                            if (casting.containsKey(event.getPlayer().getUniqueId())) {
                                casting.replace(event.getPlayer().getUniqueId(), clickTimes);
                            } else {
                                casting.put(event.getPlayer().getUniqueId(), clickTimes);
                            }
                        } else if (skillSelectType == 1){
                            Location clickLocation = event.getPlayer().getEyeLocation().add(event.getPlayer().getEyeLocation().getDirection());
                            if (lastClicks.containsKey(event.getPlayer().getUniqueId())){
                                Pair<Location, Long> lastClick = lastClicks.get(event.getPlayer().getUniqueId());
                                if (lastClick.second + expireMS < System.currentTimeMillis()){
                                    lastClick = new Pair<>(clickLocation, System.currentTimeMillis());
                                    lastClicks.replace(event.getPlayer().getUniqueId(), lastClick);
                                } else {
                                    Vector p2first = lastClick.first.toVector().subtract(event.getPlayer().getEyeLocation().toVector());
                                    Vector p2second = clickLocation.toVector().subtract(event.getPlayer().getEyeLocation().toVector());
                                    double yaw = getYawAngle(p2first, p2second);
                                    boolean left = getRelativeVector(p2first, p2second).equals("l");
                                    double yDiff = clickLocation.getY() - lastClick.first.getY();
                                    if (yDiff > 0.75){
                                        // up
                                        event.getPlayer().sendMessage(ChatColor.GREEN + "You have triggered slot: 1");
                                        if (event.getPlayer().getItemInUse().isSimilar(wand))
                                            Spells.castIndexSpell(0, event.getPlayer(), NotSkills.getInstance());
                                    } else if (!left && yaw > Math.PI / 4) {
                                        // right
                                        event.getPlayer().sendMessage(ChatColor.GREEN + "You have triggered slot: 2");
                                        if (event.getPlayer().getItemInUse().isSimilar(wand))
                                            Spells.castIndexSpell(1, event.getPlayer(), NotSkills.getInstance());
                                    } else if (yDiff < 0.75) {
                                        // down
                                        event.getPlayer().sendMessage(ChatColor.GREEN + "You have triggered slot: 3");
                                        if (event.getPlayer().getItemInUse().isSimilar(wand))
                                            Spells.castIndexSpell(2, event.getPlayer(), NotSkills.getInstance());
                                    } else if (left && yaw > Math.PI / 4) {
                                        // right
                                        event.getPlayer().sendMessage(ChatColor.GREEN + "You have triggered slot: 4");
                                        if (event.getPlayer().getItemInUse().isSimilar(wand))
                                            Spells.castIndexSpell(3, event.getPlayer(), NotSkills.getInstance());
                                    }
                                }
                            } else {
                                Pair<Location,Long> lastClick = new Pair<>(clickLocation, System.currentTimeMillis());
                                lastClicks.put(event.getPlayer().getUniqueId(), lastClick);
                            }
                        }
                    }
            }
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
