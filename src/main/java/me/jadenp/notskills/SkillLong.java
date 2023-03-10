package me.jadenp.notskills;

public class SkillLong {
    private final int skill;
    private final long time;

    public SkillLong(int skill, long time){

        this.skill = skill;
        this.time = time;
    }

    public int getSkill() {
        return skill;
    }

    public long getTime() {
        return time;
    }
}
