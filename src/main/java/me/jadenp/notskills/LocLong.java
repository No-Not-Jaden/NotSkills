package me.jadenp.notskills;


import org.bukkit.Location;

public class LocLong {
    private final Location location;
    private final long l;

    public LocLong(Location location, long l){
        this.location = location;
        this.l = l;
    }

    public Location getLocation() {
        return location;
    }

    public long getLong() {
        return l;
    }
}
