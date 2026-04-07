package com.verdantsun;

public class WateringCan {

    private int maxWaterLevel;
    private int currentWaterLevel;

    public WateringCan(int maxWaterLevel) {
        this.maxWaterLevel = maxWaterLevel;
        this.currentWaterLevel = maxWaterLevel;
    }

    public boolean water() {
        if (this.currentWaterLevel > 0) {
            this.currentWaterLevel--;
            return true;
        }
        return false;
    }

    public void refill() {

        this.currentWaterLevel = this.maxWaterLevel;
    }

    public boolean isEmpty() {

        return this.currentWaterLevel == 0;
    }

    public int getCurrentWaterLevel() {

        return this.currentWaterLevel;
    }
}
