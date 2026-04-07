package com.verdantsun.stages;

public class FullyMatureStage extends Stage {

    public FullyMatureStage() {
        super("Fully Mature");
    }

    @Override
    public boolean canGrow(boolean watered) {
        return false;
    }

    @Override
    public int getGrowthBonus(boolean soilMatch, boolean fertilized) {
        return 0;
    }

    @Override
    public double getYieldMultiplier() {
        return 2.0;
    }

    @Override
    public int getFertilizerConsumption(boolean watered) {
        return 0;
    }

    @Override
    public boolean allowsWatering() {
        return true;
    }

    @Override
    public boolean isFinalStage() {
        return true;
    }

    @Override
    public boolean canHarvest() {
        return true;
    }

    @Override
    public boolean needsWater() {
        return false;
    }
}
