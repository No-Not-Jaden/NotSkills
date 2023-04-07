package me.jadenp.notskills.MythicMobs;

import java.util.List;

public class MythicMobsOptions {
    private final int weight;
    private final List<String> includedMobs;

    public MythicMobsOptions(int weight, List<String> includedMobs){

        this.weight = weight;
        this.includedMobs = includedMobs;
    }

    public int getWeight() {
        return weight;
    }

    public List<String> getIncludedMobs() {
        return includedMobs;
    }
}
