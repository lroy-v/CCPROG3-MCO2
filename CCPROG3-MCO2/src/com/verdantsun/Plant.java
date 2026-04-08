package com.verdantsun;

import com.verdantsun.stages.*;
import java.util.*;

public class Plant {

    private String name;
    private int seedPrice;
    private int yield;
    private int maxGrowth;
    private int currentGrowth;
    private String preferredSoil;
    private boolean watered;
    private char symbol;

    private List<Stage> stages;

    public Plant(String name, int seedPrice, int yield, String preferredSoil, List<Stage> stages) {
        this.name = name;
        this.seedPrice = seedPrice;
        this.yield = yield;
        this.preferredSoil = preferredSoil;
        this.symbol = generateSymbol(name);

        this.currentGrowth = 0;
        this.watered = false;

        this.stages = stages;
        this.maxGrowth = stages.size() - 1;
    }

    public Stage getCurrentStage() {
        if (currentGrowth >= stages.size()) {
            return stages.get(stages.size() - 1);
        }
        return stages.get(currentGrowth);
    }

    public void water() {
        if (getCurrentStage().allowsWatering()) {
            this.watered = true;
        }
    }

    public void grow(String soilType, boolean fertilized) {

        Stage stage = getCurrentStage();

        if (!stage.canGrow(this.watered)) {
            return;
        }

        int baseGrowth = 1;

        boolean soilMatch = soilType.equalsIgnoreCase(this.preferredSoil);

        int growth = baseGrowth + stage.getGrowthBonus(soilMatch, fertilized);

        this.currentGrowth += growth;

        if (this.currentGrowth >= this.stages.size()) {
            this.currentGrowth = this.stages.size() - 1;
        }
    }

    public void resetWater() {
        this.watered = false;
    }

    public int harvest() {

        Stage stage = getCurrentStage();

        if (!stage.canHarvest()) {
            return 0;
        }

        int pricePerPiece = getCropPricePerPiece();

        if (pricePerPiece == 0) {
            return 0;
        }

        int totalYield = this.yield;

        totalYield = (int) (totalYield * stage.getYieldMultiplier());

        int totalValue = totalYield * pricePerPiece;

        if (stage instanceof HighProductiveStage && isRootCrop()) {
            totalValue *= 1.5;
        }

        return totalValue;
    }

    private boolean isRootCrop() {
        return name.equalsIgnoreCase("Turnip") || name.equalsIgnoreCase("Potato");
    }

    public String getName() {
        return name;
    }

    public int getSeedPrice() {
        return this.seedPrice;
    }

    public int getYield() {
        return this.yield;
    }

    public int getMaxGrowth() {
        return this.maxGrowth;
    }

    public int getCurrentGrowth() {
        return this.currentGrowth;
    }

    public String getPreferredSoil() {
        return this.preferredSoil;
    }

    public char getSymbol() {
        return this.symbol;
    }

    public boolean isWatered() {
        return this.watered;
    }

    public boolean isInPreferredSoil(String soilType) {
        return soilType.equalsIgnoreCase(this.preferredSoil);
    }

    private char generateSymbol(String name) {
        return switch (name.toLowerCase()) {
            case "potato" -> 'P';
            case "tomato" -> 'O';
            case "turnip" -> 'U';
            case "wheat" -> 'W';
            case "thyme" -> 'T';
            default -> Character.toUpperCase(name.charAt(0));
        };
    }

    public List<Stage> cloneStages() {
        List<Stage> copy = new ArrayList<>();

        for (Stage s : this.stages) {
            copy.add(s);
        }

        return copy;
    }

    public String getCropName() {
        Stage stage = getCurrentStage();

        if (stage instanceof LowProductiveStage) {
            return switch (name.toLowerCase()) {
                case "turnip" -> "Turnip Tops";
                case "wheat" -> "Wheat Grain";
                case "potato" -> "Small Potatoes";
                default -> null;
            };
        }

        if (stage instanceof HighProductiveStage || stage instanceof FullyMatureStage) {
            return switch (name.toLowerCase()) {
                case "turnip" -> "Turnip Tuber";
                case "wheat" -> "Wheat Grain";
                case "thyme" -> "Thyme Sprigs";
                case "potato" -> "Large Potatoes";
                case "tomato" -> "Tomato Berries";
                default -> null;
            };
        }

        return null;
    }

    public int getCropPricePerPiece() {
        Stage stage = getCurrentStage();

        if (stage instanceof LowProductiveStage) {
            return switch (name.toLowerCase()) {
                case "turnip" -> 5;
                case "wheat" -> 4;
                case "potato" -> 4;
                default -> 0;
            };
        }

        if (stage instanceof HighProductiveStage || stage instanceof FullyMatureStage) {
            return switch (name.toLowerCase()) {
                case "turnip" -> 6;
                case "wheat" -> 4;
                case "thyme" -> 7;
                case "potato" -> 8;
                case "tomato" -> 5;
                default -> 0;
            };
        }

        return 0;
    }
}
