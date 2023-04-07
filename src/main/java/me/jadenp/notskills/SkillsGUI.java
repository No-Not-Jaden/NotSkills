package me.jadenp.notskills;

import me.jadenp.notskills.utils.Skills;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static me.jadenp.notskills.utils.ConfigOptions.*;
import static me.jadenp.notskills.utils.Language.*;
import static me.jadenp.notskills.utils.Items.*;

public class SkillsGUI implements Listener {

    private final ItemStack[] skillMenuContents = new ItemStack[54];

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

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (isSkillGUI(event.getView().getTitle())){
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
            int page = getGUIPage(event.getView().getTitle());

            if (event.getCurrentItem().isSimilar(clearAllSkills)) {
                // reset skills
                skill.resetSkills();
            } else if (event.getCurrentItem().getType() == Material.ENCHANTED_BOOK){
                // unselect skill
                ItemMeta bookMeta = event.getCurrentItem().getItemMeta();
                assert bookMeta != null;
                // return if this fails, so nothing will break
                if (!skill.removeSkill(bookMeta.getDisplayName()))
                    return;
            } else if (event.getCurrentItem().getType() == Material.BOOK){
                // select skill
                ItemMeta bookMeta = event.getCurrentItem().getItemMeta();
                assert bookMeta != null;
                // return because not enough skill slots
                if (!skill.addSkill(bookMeta.getDisplayName()))
                    return;
                int maxSS = maxSkillSlots;
                for (int i = maxSkillSlots; i > 0; i--) {
                    if (event.getWhoClicked().hasPermission("notskills.max." + i)) {
                        maxSS = i;
                        break;
                    }
                }
                if (skill.getUsedSkillSlots() > maxSS) // return because they dont have permission for more skill slots
                    return;
            } else if (event.getCurrentItem().isSimilar(nextArrow)){
                openGUI((Player) event.getWhoClicked(), page + 1);
                return;
            } else if (event.getCurrentItem().isSimilar(backArrow)){
                openGUI((Player) event.getWhoClicked(), page - 1);
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
                    event.getView().getBottomInventory().setContents(playerContents);
                    openGUI((Player) event.getWhoClicked(), page);
                    return;
                }
            }
        }
    }

    public boolean isSkillGUI(String title){
        for (String s : splitSkillMenu){
            if (!title.contains(s))
                return false;
        }
        return true;
    }

    public int getGUIPage(String title){
        title = title.substring(splitSkillMenu[0].length());
        if (splitSkillMenu.length > 1){
            title = title.substring(0, title.indexOf(splitSkillMenu[1]));
        }
        int page = 1;
        try {
            page = Integer.parseInt(title);
        } catch (NumberFormatException ignored){}
        return page;
    }

    public String getGUITitle(int page){
        StringBuilder title = new StringBuilder(splitSkillMenu[0] + page);
        for (int i = 1; i < splitSkillMenu.length; i++) {
            title.append(splitSkillMenu[i]);
            if (i != splitSkillMenu.length - 1)
                title.append(page);
        }
        return title.toString();
    }

    public void openGUI(Player player, int page){
        PlayerData playerData = getPlayerData(player);
        Inventory inventory = Bukkit.createInventory(player, 54, getGUITitle(page));
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

        List<ItemStack> combinedContents = new ArrayList<>();

        // move current skills to combined contents
        for (int i = 0; i < skill.getUsedSkillSlots(); i++) {
            SkillOptions skillOptions = getSkill(skill.getSkill(i));
            availableSkills.remove(skillOptions);
            if (skillOptions != null)
                combinedContents.add(skillOptions.getDisplayItem(0));
        }

        // move unlocked skills to combines contents
        for (String str : playerData.getSkillsUnlocked()){
            SkillOptions skillOptions = getSkill(str);
            if (!availableSkills.remove(skillOptions)){
                continue;
            }
            if (skillOptions != null)
                combinedContents.add(skillOptions.getDisplayItem(1));
        }

        // move the rest of available skills to combined contents
        for (SkillOptions skillOptions : availableSkills){
            combinedContents.add(skillOptions.getDisplayItem(2));
        }

        // remove items that are on a previous page
        if ((page - 1) * 28 > 0) {
            combinedContents.subList(0, (page - 1) * 28).clear();
            contents[45] = backArrow; // if anything was removed, there was a previous page
        }

        // add next arrow if size is big enough
        if (combinedContents.size() > 28){
            contents[53] = nextArrow;
        }

        // display skills accordingly
        int sNum = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) {
                if (sNum < combinedContents.size()){
                    contents[i * 9 + 10 + j] = combinedContents.get(sNum);
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
