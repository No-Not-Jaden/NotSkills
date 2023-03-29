package me.jadenp.notskills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.jadenp.notskills.utils.ConfigOptions.*;
import static me.jadenp.notskills.utils.Language.prefix;


public class SkillTrigger implements Listener {

    private static SkillTrigger instance;

    private final Map<UUID, List<TriggerClick>> recordedClicks = new HashMap<>();
    private final Map<UUID, Trigger> selectedTriggers = new HashMap<>();
    private final Map<UUID, Long> interactCooldown = new HashMap<>();

    public SkillTrigger() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, NotSkills.getInstance());
    }

    public Trigger getTrigger(Player player) {
        if (playersChooseSST && selectedTriggers.containsKey(player.getUniqueId())) {
            return selectedTriggers.get(player.getUniqueId());
        }
        return defaultSST;
    }

    public void setTrigger(Player player, Trigger trigger) {
        if (selectedTriggers.containsKey(player.getUniqueId())) {
            selectedTriggers.replace(player.getUniqueId(), trigger);
        } else {
            selectedTriggers.put(player.getUniqueId(), trigger);
        }
    }

    public static SkillTrigger getInstance() {
        return instance;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (interactCooldown.containsKey(event.getPlayer().getUniqueId())) {
            if (interactCooldown.get(event.getPlayer().getUniqueId()) > System.currentTimeMillis()) {
                return;
            } else {
                interactCooldown.replace(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 20);
            }
        } else {
            interactCooldown.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 20);
        }


        if (Skills.hasSkill(event.getPlayer().getInventory().getItemInMainHand())) {
            Player player = event.getPlayer();
            List<TriggerClick> clicks = recordedClicks.containsKey(player.getUniqueId()) ? recordedClicks.get(player.getUniqueId()) : new ArrayList<>();
            clicks.removeIf(c -> c.getTime() + expireMS < System.currentTimeMillis());
            Location clickLocation = player.getEyeLocation().add(player.getEyeLocation().getDirection());
            clicks.add(new TriggerClick(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK, player.isSneaking(), ((LivingEntity) player).isOnGround(), clickLocation));

            int skillTrigger = 0;
            switch (getTrigger(player)) {
                case DIRECTIONAL_CLICK:
                    skillTrigger = Trigger.directionalTrigger(player, clicks);
                    break;
                case LEFT_RIGHT_CLICK:
                    skillTrigger = Trigger.leftRightTrigger(player, clicks);
                    break;
                case CROUCH_CLICK:
                    skillTrigger = Trigger.crouchTrigger(player, clicks);
                    clicks.clear();
                    break;
                case JUMP_CLICK:
                    skillTrigger = Trigger.jumpTrigger(player, clicks);
                    clicks.clear();
                    break;
                case CROUCH_JUMP_CLICK:
                    skillTrigger = Trigger.crouchJumpTrigger(player, clicks);
                    clicks.clear();
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
                    skillTrigger = Trigger.multiTrigger(player, clicks);
                    break;
            }


            if (skillTrigger > 0) {
                // activate skill
                clicks.clear();
                Skills skill = new Skills(Objects.requireNonNull(Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getLore()));
                String skillName = skill.getSkill(skillTrigger - 1);
                if (skillName == null)
                    return;
                SkillOptions skillOptions = getSkill(skillName);
                if (skillOptions == null)
                    return;
                PlayerData data = getPlayerData(player);
                if (data == null)
                    return;
                if (!data.isSkillUnlocked(skillName)) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                    player.sendMessage(ChatColor.BLUE + "You do not know how to perform this skill.");
                    return;
                }
                int maxSS = maxSkillSlots;
                for (int i = maxSkillSlots; i > 0; i--) {
                    if (player.hasPermission("notskills.max." + i)) {
                        maxSS = i;
                        break;
                    }
                }

                if (skillTrigger > maxSS) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                    player.sendMessage(ChatColor.BLUE + "You cannot use this many skill slots!");
                    return;
                }
                if (data.getCooldown(skillName) > System.currentTimeMillis()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_SQUID_SQUIRT, 1, 1);
                    return;
                }
                data.setCoolDown(skillOptions.getName(), skillOptions.getCooldown());
                skillOptions.runActions(player);
            }

            if (recordedClicks.containsKey(player.getUniqueId()))
                recordedClicks.replace(player.getUniqueId(), clicks);
            else
                recordedClicks.put(player.getUniqueId(), clicks);

        }


    }

    // remove clicks when the player switches hot bar slots
    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent event) {
        // see if clicking the number changes
        if (event.getNewSlot() != event.getPreviousSlot()) {
            if (Skills.hasSkill(event.getPlayer().getInventory().getItemInMainHand())) {
                recordedClicks.remove(event.getPlayer().getUniqueId());
                if (!naturalSkillUnlock)
                    return;
                ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());
                if (newItem == null)
                    return;
                if (!newItem.hasItemMeta())
                    return;
                ItemMeta meta = newItem.getItemMeta();
                assert meta != null;
                if (!meta.hasLore())
                    return;
                assert meta.getLore() != null;
                Skills skill = new Skills(meta.getLore());
                PlayerData data = getPlayerData(event.getPlayer());
                for (int i = 0; i < skill.getUsedSkillSlots(); i++) {
                    // check if player has skill unlocked already
                    if (!data.isSkillUnlocked(skill.getSkill(i))) {
                        // unlock skill
                        data.setSkillUnlocked(Objects.requireNonNull(getSkill(skill.getSkill(i))).getName(), true);
                        event.getPlayer().sendMessage(prefix + ChatColor.YELLOW + "You unlocked " + getSkill(skill.getSkill(i)).getName());
                    }
                }
            }
        }
    }


}
