package com.verdantsun.stages;

public class HighProductiveStage extends Stage {

    public HighProductiveStage() {
        super("High Productive");
    }

    @Override
    public boolean canGrow(boolean watered) {
        return watered;
    }

    @Override
    public int getGrowthBonus(boolean soilMatch, boolean fertilized) {
        int bonus = 0;

        if (soilMatch) {
            bonus += 1;
        }

        if (fertilized) {
            bonus += 1;
        }

        return bonus;
    }

    @Override
    public double getYieldMultiplier() {
        return 2.0;
    }

    @Override
    public int getFertilizerConsumption(boolean watered) {
        return watered ? 1 : 0;
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
        return true;
    }

    @Override
    public boolean needsWater() {
        return true;
    }
}
