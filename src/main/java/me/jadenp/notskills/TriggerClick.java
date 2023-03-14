package me.jadenp.notskills;

import org.bukkit.Location;

public class TriggerClick {
    private final boolean leftClick;
    private final boolean crouching;
    private final boolean jumping;
    private final Location clickLocation;
    private final long time;

    public TriggerClick(boolean leftClick, boolean crouching, boolean jumping, Location clickLocation){

        this.leftClick = leftClick;
        this.crouching = crouching;
        this.jumping = jumping;
        this.clickLocation = clickLocation;
        time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public Location getClickLocation() {
        return clickLocation;
    }

    public boolean isCrouching() {
        return crouching;
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isLeftClick() {
        return leftClick;
    }
}
