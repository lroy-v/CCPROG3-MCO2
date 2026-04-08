package com.verdantsun;

import com.verdantsun.stages.*;

public class Tile {

    private String soilType;
    private Plant plant;
    private Fertilizer fertilizer;
    private boolean meteoriteAffected;
    private boolean permanentlyFertilized;
    private String originalSoilType;

    public Tile(String soilType) {
        this.soilType = soilType;
        this.originalSoilType = soilType;
        this.plant = null;
        this.fertilizer = null;
        this.meteoriteAffected = false;
        this.permanentlyFertilized = false;
    }

    public boolean plantSeed(Plant plant) {
        if (this.plant != null) {
            return false;
        }

        if(this.meteoriteAffected) {
            return false;
        }

        this.plant = plant;
        return true;
    }

    public void removePlant() {
        this.plant = null;
    }

    public int harvestPlant() {
        if (this.plant == null) {
            return 0;
        }

        int value = this.plant.harvest();

        this.plant = null;

        return value;
    }

    public boolean waterPlant() {
        if (this.plant == null) {
            return false;
        }

        if (this.plant.isWatered() || !this.plant.getCurrentStage().allowsWatering()) {
            return false;
        }

        this.plant.water();
        return true;
    }

    public boolean applyFertilizer(Fertilizer fertilizer) {
        if (this.fertilizer != null || this.meteoriteAffected) {
            return false;
        }

        this.fertilizer = fertilizer;
        return true;
    }

    public void growPlant() {
        if (this.plant == null) {
            return;
        }

        boolean fertilized = this.permanentlyFertilized || (this.fertilizer != null && !this.fertilizer.isExpired());

        Stage stage = plant.getCurrentStage();

        if (stage.canGrow(plant.isWatered())) {
            plant.grow(this.soilType, fertilized);

            if (this.fertilizer != null && !this.permanentlyFertilized) {

                int reduction = stage.getFertilizerConsumption(plant.isWatered());

                for (int i = 0; i < reduction; i++) {
                    this.fertilizer.decreaseEffect();
                }

                if (this.fertilizer.isExpired()) {
                    this.fertilizer = null;
                }
            }
        }
        plant.resetWater();
    }

    public void excavate() {
        this.meteoriteAffected = false;
        this.soilType = this.originalSoilType;
        this.permanentlyFertilized = true;
    }

    public boolean hasPlant() {
        return this.plant != null;
    }

    public boolean isEmpty() {
        return this.plant == null;
    }

    public boolean isMeteoriteAffected() {
        return meteoriteAffected;
    }

    public boolean isPermanentlyFertilized() {
        return permanentlyFertilized;
    }

    public String getSoilType() {
        return this.soilType;
    }

    public void setMeteoriteAffected(boolean value) {
        this.meteoriteAffected = value;
    }

    public Plant getPlant() {
        return this.plant;
    }

    public Fertilizer getFertilizer() {
        return this.fertilizer;
    }
}
