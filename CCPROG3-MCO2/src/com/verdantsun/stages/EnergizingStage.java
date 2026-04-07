package com.verdantsun.stages;

public class EnergizingStage extends Stage {

    public EnergizingStage() {
        super("Energizing");
    }

    @Override
    public boolean canGrow(boolean watered) {
        return !watered;
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
        return !watered ? 2 : 0;
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
