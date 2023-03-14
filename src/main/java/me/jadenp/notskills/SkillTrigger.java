package me.jadenp.notskills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.*;

import static me.jadenp.notskills.ConfigOptions.*;


public class SkillTrigger implements Listener {

    private static SkillTrigger instance;

    private Map<UUID, List<TriggerClick>> recordedClicks = new HashMap<>();
    private Map<UUID, Trigger> selectedTriggers = new HashMap<>();
    public SkillTrigger(){
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, NotSkills.getInstance());
    }

    public Trigger getTrigger(Player player){
        if (playersChooseSST && selectedTriggers.containsKey(player.getUniqueId())){
            return selectedTriggers.get(player.getUniqueId());
        }
        return defaultSST;
    }

    public void setTrigger(Player player, Trigger trigger){
        if (selectedTriggers.containsKey(player.getUniqueId())){
            selectedTriggers.replace(player.getUniqueId(), trigger);
        } else {
            selectedTriggers.put(player.getUniqueId(), trigger);
        }
    }

    public static SkillTrigger getInstance(){
        return instance;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){

            if (Skills.hasSkill(event.getPlayer().getInventory().getItemInMainHand())){
                Player player = event.getPlayer();
                List<TriggerClick> clicks = recordedClicks.containsKey(player.getUniqueId()) ? recordedClicks.get(player.getUniqueId()) : new ArrayList<>();
                clicks.removeIf(c -> c.getTime() + expireMS < System.currentTimeMillis());
                Location clickLocation = player.getEyeLocation().add(player.getEyeLocation().getDirection());
                clicks.add(new TriggerClick(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK, player.isSneaking(), ((LivingEntity)player).isOnGround(), clickLocation));

                List<TriggerClick> validClicks =  new ArrayList<>();
                if (getTrigger(player) == Trigger.DIRECTIONAL_CLICK){
                    for (TriggerClick click : clicks){
                        if (click.isCrouching()){
                            validClicks.add(click);
                        }
                    }

                    if (validClicks.size() > 1) {
                        org.bukkit.util.Vector p2first = lastClick.getLocation().toVector().subtract(event.getPlayer().getEyeLocation().toVector());
                        Vector p2second = clickLocation.toVector().subtract(event.getPlayer().getEyeLocation().toVector());
                        double yaw = getYawAngle(p2first, p2second);
                        boolean left = getRelativeVector(p2first, p2second).equals("l");
                        double yDiff = clickLocation.getY() - lastClick.getLocation().getY();
                        if (Math.abs(yDiff) > yaw) {
                            // up or down
                            if (yDiff > 0) {
                                // up
                                useSkill(event.getPlayer(), event.getItem(), 0);
                            } else {
                                // down
                                useSkill(event.getPlayer(), event.getItem(), 2);
                            }
                        } else {
                            if (left) {
                                //left
                                useSkill(event.getPlayer(), event.getItem(), 3);
                            } else {
                                // right
                                useSkill(event.getPlayer(), event.getItem(), 1);
                            }
                        }

                        event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                    }

                }
            }

    }




}
