package com.verdantsun.stages;

public class DormantStage extends Stage {

    public DormantStage() {
        super("Dormant");
    }

    @Override
    public boolean canGrow(boolean watered) {
        return true;
    }

    @Override
    public int getGrowthBonus(boolean soilMatch, boolean fertilized) {
        return 0;
    }

    @Override
    public double getYieldMultiplier() {
        return 0;
    }

    @Override
    public int getFertilizerConsumption(boolean watered) {
        return 1;
    }

    @Override
    public boolean allowsWatering() {
        return true;
    }

    @Override
    public boolean isFinalStage() {
        return false;
    }

    @Override
    public boolean canHarvest() {
        return false;
    }

    @Override
    public boolean needsWater() {
        return false;
    }
}
