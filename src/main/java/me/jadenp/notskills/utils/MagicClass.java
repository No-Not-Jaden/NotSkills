package me.jadenp.notskills.utils;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class MagicClass {
    private final MagicAPI magicAPI;
    public MagicClass(Plugin plugin){
        magicAPI = (MagicAPI) plugin;
    }

    public boolean castSpell(String spellName, String[] parameters, CommandSender sender, Entity caster){
        return magicAPI.cast(spellName, parameters, sender, caster);
    }
}
