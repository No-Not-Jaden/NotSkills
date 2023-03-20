package me.jadenp.notskills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.jadenp.notskills.ConfigOptions.*;

public class Skills {
    private final static int maxSkillSlots = 8;

    private List<String> lore;
    private int emptySkillSlots;
    private String[] usedSkillSlots = new String[maxSkillSlots];

    public Skills(List<String> lore){
        this.lore = lore;
        boolean inSkillArea = false;
        for (String str : lore){
            // flip if we are in the skill area
            if (str.equals(skillIdentifier)){
                inSkillArea = !inSkillArea;
                continue;
            }
            if (str.equals(skillBreak))
                continue;
            if (inSkillArea){
                // check if string is reserved
                int slots = getEmptySlots(str);
                if (slots == -1){

                    int bind;
                    try {
                        // grabbing the bind slot attached to the skill
                        // have to parse around the special characters denoted as skillBindIdentifier in ConfigOptions.java
                        bind = Integer.parseInt(str.substring(str.indexOf(splitBind[0] + splitBind[0].length()), str.indexOf(splitBind[1])));
                    } catch (NumberFormatException | IndexOutOfBoundsException e){
                        Bukkit.getLogger().warning("Error reading skill bind!");
                        continue;
                    }
                    // some checks here ^^vv so just in case the lore is broken, there aren't big errors in console
                    if (usedSkillSlots.length <= bind){
                        Bukkit.getLogger().warning("Skill bind number is grater than max skill slots!");
                        continue;
                    }

                    // these 2 lines are what actually matter
                    String fullBindText = splitBind[0] + bind + splitBind[1]; // get stuff we don't want in the skill name
                    usedSkillSlots[bind - 1] = str.substring(fullBindText.length()); // put everything we want into skill slot array

                    continue;
                }
                emptySkillSlots+= slots;
            }
        }
    }

    public List<String> getLore() {
        reconstructLore();
        return lore;
    }

    public int getEmptySkillSlots() {
        return emptySkillSlots;
    }

    public int getUsedSkillSlots() {
        for (int i = 0; i < usedSkillSlots.length; i++) {
            if (usedSkillSlots[i] == null)
                return i;
        }
        return usedSkillSlots.length;
    }

    // -1 for not an empty slot string
    private int getEmptySlots(String str){

        for (String s : splitReserved){
            if (!str.contains(s)){
                return -1;
            }
        }
        String amount = skillSlotsReserved.charAt(0) == '{' ? str.substring(0, str.indexOf(splitReserved[0])) : str.substring(splitReserved[0].length(), str.substring(splitReserved[0].length()).indexOf(splitReserved[1]) + splitReserved[0].length());
        int a = 0;
        try {
            a = Integer.parseInt(amount);
        } catch (NumberFormatException e){
            Bukkit.getLogger().warning("Empty skill slots do not display a number!");
        }
        return a;
    }

    public List<String> getOriginalLore(){
        List<String> newLore = new ArrayList<>();
        boolean inSkills = false;
        for (String line : lore){
            if (line.equals(skillIdentifier))
                inSkills = !inSkills;
            if (inSkills)
                continue;
            newLore.add(line);
        }
        return newLore;
    }

    private void reconstructLore(){
        List<String> newLore = getOriginalLore();
        if (emptySkillSlots > 0 || getUsedSkillSlots() > 0) {
            newLore.add(skillIdentifier);
            for (int i = 0; i < usedSkillSlots.length; i++) {
                if (usedSkillSlots[i] == null)
                    break;
                if (i > 0)
                    newLore.add(skillBreak);
                newLore.add(splitBind[0] + (i + 1) + splitBind[1] + usedSkillSlots[i]);
            }
            if (emptySkillSlots > 0) {
                if (getUsedSkillSlots() > 0)
                    newLore.add(skillBreak);
                newLore.add(splitReserved[0] + emptySkillSlots + splitReserved[1]);
            }
            newLore.add(skillIdentifier);
        }
        lore = newLore;
    }


    public Skills addSkillSlots(int amount){
        emptySkillSlots+= amount;
        if (emptySkillSlots + getUsedSkillSlots() > maxSkillSlots){
            Bukkit.getLogger().warning("Too many skill slots on an item!");
            emptySkillSlots = maxSkillSlots;
        }
        return this;
    }

    public boolean addSkill(String name){
        if (emptySkillSlots == 0)
            return false;
        for (int i = 0; i < usedSkillSlots.length; i++) {
            if (usedSkillSlots[i] == null){
                emptySkillSlots--;
                usedSkillSlots[i] = name;
                return true;
            }
        }
        return false;
    }

    public static boolean hasSkill(@NotNull ItemStack itemStack){
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
    
    public int getSkill(String name){
        for (int i = 0; i < usedSkillSlots.length; i++) {
            if (usedSkillSlots[i] == null)
                continue;
            if (usedSkillSlots[i].equalsIgnoreCase(name))
                return i + 1;
        }
        return 0;
    }

    public String getSkill(int index){
        if (index > usedSkillSlots.length)
            return null;
        return usedSkillSlots[index-1];
    }

    public boolean hasSkill(String name){
        for (String str : usedSkillSlots){
            if (str == null)
                continue;
            if (str.equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    // deletes skills
    public Skills removeAllSkills(){
        emptySkillSlots = 0;
        Arrays.fill(usedSkillSlots, null);
        return this;
    }

    // changes used skills into empty skills
    public Skills resetSkills(){
        emptySkillSlots+= getUsedSkillSlots();
        Arrays.fill(usedSkillSlots, null);
        return this;
    }

    public Skills removeSkillSlots(int amount){
        // remove skills from lore, start with empty skill slots, then the highest skill
        if (emptySkillSlots > amount){
            emptySkillSlots-= amount;
            return this;
        }
        amount-= emptySkillSlots;
        emptySkillSlots = 0;
        for (int i = usedSkillSlots.length - 1; i >= 0; i--) {
            if (amount == 0)
                return this;
            if (usedSkillSlots[i] == null)
                continue;
            usedSkillSlots[i] = null;
            amount--;
        }
        return this;
    }

    public boolean removeSkill(String skill){
        // remove named skill
        skill = ChatColor.stripColor(color(skill));
        for (int i = 0; i < usedSkillSlots.length; i++) {
            if (usedSkillSlots[i] == null)
                continue;
            String skillName = usedSkillSlots[i];
            skillName = ChatColor.stripColor(color(skillName));
            if (skillName.equalsIgnoreCase(skill)){
                usedSkillSlots[i] = null;
                return true;
            }
        }
        return false;
    }

}
