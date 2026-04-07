package com.verdantsun.stages;

public abstract class Stage {

    private String name;

    public Stage(String name) {
        this.name = name;
    }

    public abstract boolean canGrow(boolean watered);

    public abstract int getGrowthBonus(boolean soilMatch, boolean fertilized);

    public abstract double getYieldMultiplier();

    public abstract int getFertilizerConsumption(boolean watered);

    public abstract boolean allowsWatering();

    public abstract boolean isFinalStage();

    public boolean canHarvest() {
        return getYieldMultiplier() > 0;
    }

    public boolean needsWater() {
        return allowsWatering();
    }

    public String getStageName() {
        return this.name;
    }
}
