package me.jadenp.notskills.BuiltInSkills;

import me.jadenp.notskills.utils.ConfigOptions;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SkillHandlerTest {

    @org.junit.jupiter.api.Test
    void fillParameters() {
        SkillHandler.fillParameters(new Object[]{1,"true", 3.3, "hello"}, new String[]{"3", "false"});
    }

    @org.junit.jupiter.api.Test
    void executeSkill(){
        SkillHandler.executeSkill("test", new String[]{"Snipe"}, null);
    }

    @org.junit.jupiter.api.Test
    void getSkillNames(){
        List<String> skillNames = new ArrayList<>();
        // Register all classes in the SpecificSkills folder as skills that can be executed
            List<String> names = new ArrayList<>();
            try {
                final ClassLoader loader = Thread.currentThread().getContextClassLoader();
                final InputStream is = loader.getResourceAsStream("me/jadenp/notskills/BuiltInSkills/SpecificSkills");
                if (is != null) {
                    final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                    final BufferedReader br = new BufferedReader(isr);
                    names = br.lines().collect(Collectors.toList());
                } else {
                        System.out.println("Could not find SpecificSkills folder!");
                }
            } catch (SecurityException | NullPointerException e) {
                if (ConfigOptions.debug)
                    e.printStackTrace();
            }



            if (!names.isEmpty()) {
                for (String name : names) {
                    if (name.contains("$1"))
                        continue;
                    skillNames.add(name.substring(0, name.length() - 6).toUpperCase());
                }
                System.out.println("Registered " + skillNames.size() + " built-in skills");
            } else {
                System.out.println("No built-in skills registered");
            }

            for (String s : skillNames){
                System.out.print("\"" + s + "\",");
            }
            if (skillNames.size() > 0)
                System.out.println("\b");
    }
}