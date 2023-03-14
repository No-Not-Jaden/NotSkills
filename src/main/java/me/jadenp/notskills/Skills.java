package me.jadenp.notskills;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.jadenp.notskills.ConfigOptions.skillIdentifier;
import static me.jadenp.notskills.ConfigOptions.skillSlotsReserved;

public class Skills {

    private List<String> lore = new ArrayList<>();
    private int emptySkillSlots;
    private int usedSkillSlots;

    public Skills(List<String> lore){
        this.lore = lore;
        boolean inSkillArea = false;
        for (String str : lore){
            // flip if we are in the skill area
            if (str.equals(skillIdentifier)){
                inSkillArea = !inSkillArea;
                continue;
            }
            if (inSkillArea){
                // check if string is reserved
                int slots = getEmptySlots(str);
                if (slots == -1){
                    usedSkillSlots++;
                    continue;
                }
                emptySkillSlots+= slots;
            }
        }
    }
    public Skills(int skillSlots, String... skills){
        lore = addSkillSlots(new ArrayList<>(), skillSlots);

        for (String s : skills){
            lore.add(0, s);
        }
    }
    public Skills(List<String> previousLore, int skillSlots, String... skills){
        lore = addSkillSlots(previousLore, skillSlots);

        for (String s : skills){
            lore.add(0, s);
        }
    }

    public List<String> getLore() {
        return lore;
    }

    public int getEmptySkillSlots() {
        return emptySkillSlots;
    }

    public int getUsedSkillSlots() {
        return usedSkillSlots;
    }

    // -1 for not an empty slot string
    private int getEmptySlots(String str){
        String[] splitReserved = skillSlotsReserved.split("\\{amount\\}");
        for (String s : splitReserved){
            if (!str.contains(s)){
                return -1;
            }
        }
        String amount = skillSlotsReserved.charAt(0) == '{' ? str.substring(0, str.indexOf(splitReserved[0])) : str.substring(splitReserved[0].length(), str.substring(splitReserved[0].length()).indexOf(splitReserved[1]) + splitReserved[0].length());
        int a = 0;
        try {
            a = Integer.parseInt(amount);
        } catch (NumberFormatException ignored){

        }
        return a;
    }

    private List<String> addSkillSlots(List<String> startingLore, int amount){
        startingLore.add(skillIdentifier); // used to identify skills used by this plugin
        String skillSlots = skillSlotsReserved; // used to identify how many skill slots this item has
        while (skillSlots.contains("{amount}")) {
            skillSlots = skillSlots.replace("{amount}", amount + "");
        }
        startingLore.add(skillSlots);
        startingLore.add(skillIdentifier);
        return startingLore;
    }

    public static boolean hasSkill(ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        if (meta.hasLore()){
            List<String> lore = meta.getLore();
            assert lore != null;
            for (String str : lore){
                if (str.equals(skillIdentifier)){
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> removeSkillSlots(int amount){
        // remove skills from lore, start with empty skill slots, then the highest skill
        if (emptySkillSlots > amount){

        }
    }

    private void removeSkill(String skill){
        // remove names skill

    }

}
