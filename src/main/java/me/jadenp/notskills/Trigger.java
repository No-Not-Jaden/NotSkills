package me.jadenp.notskills;

public enum Trigger {
    LEFT_RIGHT_CLICK(8), // java only
    CROUCH_CLICK(1),
    JUMP_CLICK(1),
    CROUCH_JUMP_CLICK(2),
    DIRECTIONAL_CLICK(4),
    TIMED_CLICK(7),
    DOUBLE_CLICK(1),
    TRIPLE_CLICK(1),
    MULTI_CLICK(3); // double, triple, & quad click

    private final int maxSkills;
    Trigger(int maxSkills){
        this.maxSkills = maxSkills;
    }

    public int getMaxSkills(){
        return maxSkills;
    }
}
