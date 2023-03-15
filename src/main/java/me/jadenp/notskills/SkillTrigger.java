package me.jadenp.notskills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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

                int skillTrigger = 0;
                switch (getTrigger(player)){
                    case DIRECTIONAL_CLICK:
                        skillTrigger = Trigger.directionalTrigger(player, clicks);
                        break;
                    case LEFT_RIGHT_CLICK:
                        skillTrigger = Trigger.leftRightTrigger(player, clicks);
                        break;
                    case CROUCH_CLICK:
                        skillTrigger = Trigger.crouchTrigger(player, clicks);
                        break;
                    case JUMP_CLICK:
                        skillTrigger = Trigger.jumpTrigger(player, clicks);
                        break;
                    case CROUCH_JUMP_CLICK:
                        skillTrigger = Trigger.crouchJumpTrigger(player, clicks);
                        break;
                    case TIMED_CLICK:
                        skillTrigger = Trigger.timedTrigger(player, clicks);
                        break;
                    case DOUBLE_CLICK:
                        skillTrigger = Trigger.doubleTrigger(player, clicks);
                        break;
                    case TRIPLE_CLICK:
                        skillTrigger = Trigger.tripleTrigger(player, clicks);
                        break;
                    case MULTI_CLICK:
                        skillTrigger = Trigger.multiTrigger(player,clicks);
                        break;
                }


                if (skillTrigger > 0) {
                    // activate skill
                    clicks.clear();

                }

                if (recordedClicks.containsKey(player.getUniqueId()))
                    recordedClicks.replace(player.getUniqueId(), clicks);
                else
                    recordedClicks.put(player.getUniqueId(), clicks);

            }


    }



}
