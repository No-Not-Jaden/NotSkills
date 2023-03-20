package me.jadenp.notskills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.swing.*;
import java.util.*;

import static me.jadenp.notskills.ConfigOptions.*;
import static me.jadenp.notskills.Items.fill;

public class SkillsGUI implements Listener {

    private ItemStack[] skillMenuContents = new ItemStack[54];
    private final ItemStack nextArrow = new ItemStack(Material.SPECTRAL_ARROW);
    private final ItemStack backArrow = new ItemStack(Material.SPECTRAL_ARROW);
    private final ItemStack clearAllSkills = new ItemStack(Material.BARRIER);
    private static SkillsGUI instance;

    public static SkillsGUI getInstance() {
        return instance;
    }

    public SkillsGUI(){
        instance = this;

        Arrays.fill(skillMenuContents, fill);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) {
                skillMenuContents[i * 9 + 10 + j] = null;
            }
        }

        ItemMeta meta = clearAllSkills.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RED + "Remove Active Skills");
        clearAllSkills.setItemMeta(meta);
        skillMenuContents[4] = clearAllSkills;

        ItemStack yellowFill = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        meta = yellowFill.getItemMeta();
        assert meta != null;
        meta.setDisplayName("");
        yellowFill.setItemMeta(meta);
        skillMenuContents[48] = yellowFill;
        skillMenuContents[50] = yellowFill;

        meta = nextArrow.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.YELLOW + "Next");
        backArrow.setItemMeta(meta);
        meta = backArrow.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.YELLOW + "Back");
        backArrow.setItemMeta(meta);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (event.getView().getTitle().equals(skillMenu)){
            // viewing their skills
            event.setCancelled(true);

            // find original item spot in player inventory, then update it
            if (event.getCurrentItem() == null)
                return;
            if (event.getSlot() >= event.getView().getTopInventory().getSize())
                return;

            ItemStack[] contents = event.getInventory().getContents();
            ItemStack skillItem = contents[49];
            ItemMeta meta = skillItem.getItemMeta();
            assert meta != null;
            Skills skill = new Skills(Objects.requireNonNull(meta.getLore()));

            if (event.getCurrentItem().isSimilar(clearAllSkills)) {
                // reset skills
                skill.resetSkills();
            } else if (event.getCurrentItem().getType() == Material.ENCHANTED_BOOK){
                // unselect skill
                ItemMeta bookMeta = event.getCurrentItem().getItemMeta();
                assert bookMeta != null;
                // return if this fails, so nothing will break
                if (!skill.removeSkill(bookMeta.getDisplayName(), true))
                    return;
            } else if (event.getCurrentItem().getType() == Material.BOOK){
                // select skill
                ItemMeta bookMeta = event.getCurrentItem().getItemMeta();
                assert bookMeta != null;
                // return because not enough skill slots
                if (!skill.addSkill(bookMeta.getDisplayName()))
                    return;
            }
            else {
                // return so we don't waste resources updating the item
                return;
            }

            // find the item in the player's inventory
            ItemStack[] playerContents = event.getView().getBottomInventory().getContents();
            for (int i = 0; i < playerContents.length; i++) {
                if (playerContents[i] == null)
                    continue;
                if (playerContents[i].isSimilar(skillItem)){
                    // update the item
                    meta.setLore(skill.getLore());
                    skillItem.setItemMeta(meta);
                    playerContents[i] = skillItem;
                    openGUI((Player) event.getWhoClicked());
                    return;
                }
            }
        }
    }

    public void openGUI(Player player){
        PlayerData playerData = getPlayerData(player);
        Inventory inventory = Bukkit.createInventory(player, 54, skillMenu);
        ItemStack[] contents = Arrays.copyOf(skillMenuContents, skillMenuContents.length);

        ItemStack skillItem = player.getInventory().getItemInMainHand();
        contents[49] = skillItem;

        // get compatible skills with item
        List<SkillOptions> availableSkills = new ArrayList<>();
        for (SkillOptions skillOptions : skills){
            if (skillOptions.isAllowedItem(skillItem.getType())){
                availableSkills.add(skillOptions);
            }
        }

        // get the skills on the item
        ItemMeta meta = skillItem.getItemMeta();
        assert meta != null;
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        assert lore != null;
        Skills skill = new Skills(lore);

        // move current skills to a new list
        List<SkillOptions> currentSkills = new ArrayList<>();
        for (int i = 0; i < skill.getUsedSkillSlots(); i++) {
            SkillOptions skillOptions = getSkill(skill.getSkill(i), false);
            availableSkills.remove(skillOptions);
            currentSkills.add(skillOptions);
        }

        // move unlocked skills to a new list
        List<SkillOptions> unlockedSkills = new ArrayList<>();
        for (String str : playerData.getSkillsUnlocked()){
            SkillOptions skillOptions = getSkill(str, false);
            availableSkills.remove(skillOptions);
            unlockedSkills.add(skillOptions);
        }

        // display skills accordingly
        int sNum = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) {
                if (sNum < currentSkills.size()){
                    contents[i * 9 + 10 + j] = currentSkills.get(sNum).getDisplayItem(0);
                } else if (sNum - currentSkills.size() < unlockedSkills.size()){
                    contents[i * 9 + 10 + j] = unlockedSkills.get(sNum - currentSkills.size()).getDisplayItem(0);
                } else if (sNum - currentSkills.size() - unlockedSkills.size() < availableSkills.size()){
                    contents[i * 9 + 10 + j] = unlockedSkills.get(sNum - currentSkills.size() - unlockedSkills.size()).getDisplayItem(0);
                } else {
                    break;
                }
                sNum++;
            }
        }

        // update the inventory and open it for the player
        inventory.setContents(contents);
        player.openInventory(inventory);
    }

}
