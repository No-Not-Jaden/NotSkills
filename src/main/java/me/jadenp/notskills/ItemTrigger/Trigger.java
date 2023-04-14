package me.jadenp.notskills.ItemTrigger;

import me.jadenp.notskills.utils.ConfigOptions;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static me.jadenp.notskills.utils.ConfigOptions.*;
import static me.jadenp.notskills.utils.ConfigOptions.pauseRatio;

public enum Trigger {
    LEFT_RIGHT_CLICK(8), // java only
    CROUCH_CLICK(1),
    JUMP_CLICK(1),
    CROUCH_JUMP_CLICK(2),
    DIRECTIONAL_CLICK(4),
    TIMED_CLICK(7),
    DOUBLE_CLICK(1),
    TRIPLE_CLICK(1),
    MULTI_CLICK(3); // double, triple, & quad click

    private final int maxSkills;
    private final static float particleSize = 2.0f;
    Trigger(int maxSkills){
        this.maxSkills = maxSkills;
    }

    public int getMaxSkills(){
        return maxSkills;
    }
    public static int directionalTrigger(Player player, List<TriggerClick> clicks){
        List<TriggerClick> validClicks =  new ArrayList<>();
        for (TriggerClick click : clicks){
            if (click.isCrouching()){
                validClicks.add(click);
            }
        }
        if (validClicks.size() == 0)
            return 0;

        if (validClicks.size() > 1) {
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(0, 255, 0), particleSize));
            org.bukkit.util.Vector p2first = validClicks.get(0).getClickLocation().toVector().subtract(player.getEyeLocation().toVector());
            Vector p2second = validClicks.get(1).getClickLocation().toVector().subtract(player.getEyeLocation().toVector());
            double yaw = getYawAngle(p2first, p2second);
            boolean left = getRelativeVector(p2first, p2second).equals("l");
            double yDiff = validClicks.get(1).getClickLocation().getY() - validClicks.get(0).getClickLocation().getY();
            if (Math.abs(yDiff) > yaw) {
                // up or down
                if (yDiff > 0) {
                    // up
                    return 1;
                } else {
                    // down
                    return 3;
                }
            } else {
                if (left) {
                    //left
                    return 4;
                } else {
                    // right
                    return 2;
                }
            }

        }
        if (clicks.get(clicks.size()-1).isCrouching()) {
            player.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(252, 186, 3), particleSize));
        }
        return 0;

    }

    public static int leftRightTrigger(Player player, List<TriggerClick> clicks){
        List<TriggerClick> validClicks =  new ArrayList<>();
        for (TriggerClick click : clicks){
            if (click.isCrouching()){
                validClicks.add(click);
            }
        }
        if (validClicks.size() == 0)
            return 0;

        for (int i = 0; i < ConfigOptions.threeTypePatterns.length; i++) {
            for (int j = 0; j < ConfigOptions.threeTypePatterns[0].length; j++) {
                if (ConfigOptions.threeTypePatterns[i][j] != validClicks.get(j).isLeftClick()){
                    break;
                }
                if (j == ConfigOptions.threeTypePatterns[0].length - 1){
                    player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                    if (particles)
                        player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(0, 255, 0), particleSize));
                    return i + 1;
                }
            }
        }
        if (clicks.get(clicks.size()-1).isCrouching()) {
            player.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(252, 186, 3), particleSize));
        }
        return 0;
    }

    public static int crouchTrigger(Player player, List<TriggerClick> clicks){
        if (clicks.get(0).isCrouching()) {
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(0, 255, 0), particleSize));
            return 1;
        }
        return 0;
    }

    public static int jumpTrigger(Player player, List<TriggerClick> clicks){
        if (clicks.get(0).isJumping()) {
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(0, 255, 0), particleSize));
            return 1;
        }
        return 0;
    }

    public static int crouchJumpTrigger(Player player, List<TriggerClick> clicks){
        if (clicks.get(0).isCrouching()) {
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(0, 255, 0), particleSize));
            return 1;
        }
        if (clicks.get(0).isJumping()) {
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(0, 255, 0), particleSize));
            return 2;
        }
        return 0;
    }

    public static int timedTrigger(Player player, List<TriggerClick> clicks){
        List<TriggerClick> validClicks =  new ArrayList<>();
        for (TriggerClick click : clicks){
            if (click.isCrouching()){
                validClicks.add(click);
            }
        }
        if (validClicks.size() == 0)
            return 0;

        // check if they have enough to cast a spell
        if (clicks.size() >= 4) {
            // get the time paused between the first 4 clicks
            long[] spaces = new long[3];
            for (int i = 1; i < 4; i++) {
                spaces[i - 1] = validClicks.get(i).getTime() - validClicks.get(i - 1).getTime();
            }

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
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1,1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(0, 255, 0), particleSize));
            // check with registered triggers to see if the pattern is valid
            for (int i = 1; i < ConfigOptions.threeTypePatterns.length; i++) {
                for (int j = 0; j < ConfigOptions.threeTypePatterns[0].length; j++) {
                    if (ConfigOptions.threeTypePatterns[i][j] != trigger[i]){
                        break;
                    }
                    if (j == ConfigOptions.threeTypePatterns[0].length - 1){
                        return i;
                    }
                }
            }

        } else {
            if (clicks.get(clicks.size()-1).isCrouching()) {
                player.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1, 1);
                if (particles)
                    player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(252, 186, 3), particleSize));
            }
        }

        return 0;
    }

    public static int doubleTrigger(Player player, List<TriggerClick> clicks){
        List<TriggerClick> validClicks =  new ArrayList<>();
        for (TriggerClick click : clicks){
            if (click.isCrouching()){
                validClicks.add(click);
            }
        }
        if (validClicks.size() == 0)
            return 0;

        if (validClicks.size() > 1){
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(0, 255, 0), particleSize));
            return 1;
        }
        if (clicks.get(clicks.size()-1).isCrouching()) {
            player.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(252, 186, 3), particleSize));
        }
        return 0;
    }

    public static int tripleTrigger(Player player, List<TriggerClick> clicks){
        List<TriggerClick> validClicks =  new ArrayList<>();
        for (TriggerClick click : clicks){
            if (click.isCrouching()){
                validClicks.add(click);
            }
        }
        if (validClicks.size() == 0)
            return 0;

        if (validClicks.size() > 2){
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(0, 255, 0), particleSize));
            return 1;
        }
        if (clicks.get(clicks.size()-1).isCrouching()) {
            player.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(252, 186, 3), particleSize));
        }
        return 0;
    }

    public static int multiTrigger(Player player, List<TriggerClick> clicks){
        List<TriggerClick> validClicks =  new ArrayList<>();
        for (TriggerClick click : clicks){
            if (click.isCrouching()){
                validClicks.add(click);
            }
        }
        if (validClicks.size() == 0)
            return 0;
        // when time since start is > multiClickResetTime
        if (validClicks.get(validClicks.size() - 1).getTime() - validClicks.get(0).getTime() > multiClickResetTime){
            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(0, 255, 0), particleSize));
            if (validClicks.size() == 2){
                return 1;
            } else if (validClicks.size() == 3){
                return 2;
            }
            return 3;
        }

        if (clicks.get(clicks.size()-1).isCrouching()) {
            player.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1, 1);
            if (particles)
                player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(player.getEyeLocation().getDirection()), 1, new Particle.DustOptions(Color.fromRGB(252, 186, 3), particleSize));
        }
        return 0;
    }
}
