package me.jadenp.notskills.BuiltInSkills;

import me.jadenp.notskills.BuiltInSkills.SpecificSkills.Snipe;

public enum ESkill {

     SNIPE(Snipe.class);

     private final Class<? extends BuiltInSkill> skillclass;
    ESkill(Class<? extends BuiltInSkill> skillClass) {
        this.skillclass = skillClass;
    }

    public Class<? extends BuiltInSkill> getSkillclass() {
        return skillclass;
    }
}
