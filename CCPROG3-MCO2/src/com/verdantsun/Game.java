package com.verdantsun;

import com.verdantsun.stages.*;
import java.util.*;

public class Game {

    private Player player;
    private Field field;
    private WateringCan wateringCan;
    private int currentDay;
    private HighScoreManager highScoreManager;
    private int excavationsToday;

    private HashMap<String, Plant> plantCatalog;
    private HashMap<String, Fertilizer> fertilizerCatalog;

    private Scanner scanner;

    public Game(String playerName) {
        scanner = new Scanner(System.in);

        this.player = new Player(playerName);
        this.field = new Field();
        this.wateringCan = new WateringCan(10);
        this.currentDay = 1;
        this.highScoreManager = new HighScoreManager();
        this.excavationsToday = 0;

        plantCatalog = PlantFactory.createPlants();
        fertilizerCatalog = FertilizerFactory.createFertilizers();

        this.highScoreManager.loadScores();
    }

    public void startGame() {
        while (this.currentDay <= 20) {
            displayMainMenu();
        }

        endGame();
    }

    public void nextDay() {

        this.currentDay++;
        this.excavationsToday = 0;

        if (this.currentDay <= 20) {
            this.player.addSavings(50);
        }

        this.field.nextDayUpdate();

        if (this.currentDay == 15) {
            this.field.applyMeteoritePattern(this.player);
            System.out.println("A meteorite has struck the farm!");
        }

        System.out.println("Day " + this.currentDay + " started.");
    }

    public void endGame() {
        this.highScoreManager.addScore(this.player.getName(), this.player.getSavings());
        this.highScoreManager.saveScores();
        System.out.println("Game Ended!");
    }

    public void plantSeed() {
        ArrayList<Plant> sortedPlants = new ArrayList<>(this.plantCatalog.values());
        sortedPlants.sort(Comparator.comparing(Plant::getName));

        Set<Character> usedLetters = new HashSet<>();
        Map<Character, Plant> letterMap = new HashMap<>();

        System.out.println("\nAvailable Plants:");
        for (Plant p : sortedPlants) {
            char letter = getUniqueLetter(p.getName(), usedLetters);
            letterMap.put(letter, p);
            System.out.println("[" + letter + "] " + p.getName()
                    + " - Price: " + p.getSeedPrice()
                    + "\t Yield: " + p.getYield()
                    + "\t Max Growth: "  + p.getMaxGrowth()
                    + "\t Preferred Soil: " + capitalize(p.getPreferredSoil()));
        }

        System.out.print("Select a Plant: ");
        String input = scanner.nextLine().trim().toUpperCase();
        if (!letterMap.containsKey(input.charAt(0))) {
            System.out.println("Invalid plant selection.");
            return;
        }
        Plant template = letterMap.get(input.charAt(0));

        List<int[]> tilesToPlant = selectTiles("Plant Seed");
        if (tilesToPlant.isEmpty()) {
            return;
        }

        for (int[] coord : tilesToPlant) {

            if (!this.player.canAfford(template.getSeedPrice())) {
                System.out.println("Not enough savings for more seeds.");
                break;
            }

            Tile tile = this.field.getTile(coord[0], coord[1]);

            Plant newPlant = new Plant(
                    template.getName(),
                    template.getSeedPrice(),
                    template.getYield(),
                    template.getPreferredSoil(),
                    template.cloneStages()
            );

            boolean planted = tile.plantSeed(newPlant);
            if (planted) {
                this.player.deductSavings(template.getSeedPrice());
                System.out.println("\nPlanted at (" + (coord[0]+1) + "," + (coord[1]+1) + ")");
            } else {
                if (!tile.isEmpty()) {
                    System.out.println("\nTile (" + (coord[0]+1) + "," + (coord[1]+1) + ") is not empty.");
                }
                else if (tile.isMeteoriteAffected()) {
                    System.out.println("\nTile (" + (coord[0]+1) + "," + (coord[1]+1) + ") is meteorite affected.");
                }
                else {
                    System.out.println("\nCannot plant at (" + (coord[0]+1) + "," + (coord[1]+1)
                            + ") - Preferred soil is "
                            + capitalize(template.getPreferredSoil()) + ".");
                }
            }
        }
    }

    public void waterPlant() {
        if (this.wateringCan.isEmpty()) {
            System.out.println("Water can is empty.");
            return;
        }

        List<int[]> tiles = selectTiles("Water Plant");
        if (tiles.isEmpty()) {
            return;
        }

        for (int[] coord : tiles) {
            if (this.wateringCan.isEmpty()) break;

            Tile tile = this.field.getTile(coord[0], coord[1]);
            boolean watered = tile.waterPlant();

            if (watered) {
                this.wateringCan.water();
                System.out.println("Watered (" + (coord[0] + 1) + "," + (coord[1] + 1) + ")");
            } else {
                System.out.println("Cannot water at (" + (coord[0] + 1) + "," + (coord[1] + 1) + ")");
            }
        }
    }

    public void applyFertilizer() {

        ArrayList<Fertilizer> sortedFertilizers = new ArrayList<>(this.fertilizerCatalog.values());
        sortedFertilizers.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        Set<Character> usedLetters = new HashSet<>();
        Map<Character, Fertilizer> letterMap = new HashMap<>();

        System.out.println("\nAvailable Fertilizers:");
        for (Fertilizer f : sortedFertilizers) {
            char letter = getUniqueLetter(f.getName(), usedLetters);
            letterMap.put(letter, f);
            System.out.println("[" + letter + "] " + f.getName()
                    + " - Price: " + f.getPrice()
                    + "\t Effect Days: " + f.getEffectDays());
        }

        System.out.print("Select a Fertilizer: ");
        String input = scanner.nextLine().trim().toUpperCase();
        if (!letterMap.containsKey(input.charAt(0))) {
            System.out.println("Invalid fertilizer selection.");
            return;
        }

        Fertilizer template = letterMap.get(input.charAt(0));

        List<int[]> tilesToFertilize = selectTiles("Apply Fertilizer");
        if (tilesToFertilize.isEmpty()) {
            return;
        }

        for (int[] coord : tilesToFertilize) {
            if (!this.player.canAfford(template.getPrice())) {
                System.out.println("Not enough savings for more fertilizer.");
                break;
            }

            Tile tile = this.field.getTile(coord[0], coord[1]);

            Fertilizer newFertilizer = new Fertilizer(
                    template.getName(),
                    template.getPrice(),
                    template.getEffectDays()
            );

            if (tile.applyFertilizer(newFertilizer)) {
                this.player.deductSavings(template.getPrice());
                System.out.println("Fertilizer applied at (" + (coord[0] + 1) + "," + (coord[1] + 1) + ")");
            } else {
                System.out.println("Cannot apply fertilizer at (" + (coord[0] + 1) + "," + (coord[1] + 1) + ")");
            }
        }
    }

    public void removeOrHarvestPlant() {

        List<int[]> tilesToAct = selectTiles("Remove/Harvest Plant");
        if (tilesToAct.isEmpty()) {
            return;
        }

        for (int[] coord : tilesToAct) {
            Tile tile = this.field.getTile(coord[0], coord[1]);
            if (!tile.hasPlant()) {
                System.out.println("No plant at (" + (coord[0] + 1) + "," + (coord[1] + 1) + ")");
                continue;
            }

            int earned = tile.harvestPlant();
            if (earned > 0) {
                this.player.addSavings(earned);
                System.out.println("Harvested (" + (coord[0] + 1) + "," + (coord[1] + 1) + ") - Earned: " + earned);
            } else {
                tile.removePlant();
                System.out.println("Plant removed (" +  (coord[0] + 1) + "," + (coord[1] + 1) + ")");
            }
        }
    }

    public void excavateMeteorite() {

        if (this.currentDay < 8) {
            System.out.println("Meteorite excavation not available yet.");
            return;
        }

        if (this.excavationsToday >= 5) {
            System.out.println("Maximum excavations reached today (5).");
            return;
        }

        int excavationCost = 500;

        List<int[]> tiles = selectTiles("Excavate Meteorite");
        if (tiles.isEmpty()) {
            return;
        }

        for (int[] coord : tiles) {
            if (this.excavationsToday >= 5) {
                System.out.println("Daily excavation limit reached.");
                break;
            }

            if (!this.player.canAfford(excavationCost)) {
                System.out.println("Not enough savings.");
                break;
            }

            Tile tile = this.field.getTile(coord[0], coord[1]);

            if (!tile.isMeteoriteAffected()) {
                System.out.println("No meteorite at (" + (coord[0] + 1) + "," + (coord[1] + 1) + ")");
                continue;
            }

            this.player.deductSavings(excavationCost);
            tile.excavate();
            excavationsToday ++;

            String soil = capitalize(tile.getSoilType());

            System.out.println("Excavated (" +  (coord[0] + 1) + "," + (coord[1] + 1) +
                    ") - Soil restored to " + soil + ". Tile permanently fertilized.");
        }
    }

    public void refillWateringCan() {

        int refillCost = 100;

        if (!this.player.canAfford(refillCost)) {
            System.out.println("Not enough savings to refill watering can.");
            return;
        }

        this.player.deductSavings(refillCost);
        this.wateringCan.refill();

        System.out.println("Watering can refilled. (-100 savings)");
    }

    private boolean playerCanPlant() {
        for (Plant p : this.plantCatalog.values()) {
            if (this.player.canAfford(p.getSeedPrice()))
                return true;
        }
        return false;
    }

    public void displayMainMenu() {
        int option = 1;
        HashMap<Integer, Runnable> actions = new HashMap<>();

        System.out.println("\n=== VERDANT SUN SIMULATOR ===");
        System.out.println("Day: " + this.currentDay);
        System.out.println("Savings: " + this.player.getSavings());
        System.out.println("Watering Can: " + this.wateringCan.getCurrentWaterLevel() + "/10");

        this.field.displayField();
        this.field.displayStatuses();

        if (playerCanPlant()) {
            System.out.println("[" + option + "] Plant Seed");
            actions.put(option++, this::plantSeed);
        }

        if (this.field.hasWaterablePlants()) {
            System.out.println("[" + option + "] Water Plant");
            actions.put(option++, this::waterPlant);
        }

        if (this.player.canAfford(100)) {
            System.out.println("[" + option + "] Refill Watering Can");
            actions.put(option++, this::refillWateringCan);
        }

        System.out.println("[" + option + "] Apply Fertilizer");
        actions.put(option++, this::applyFertilizer);

        if (this.field.hasAnyPlant()) {
            System.out.println("[" + option + "] Remove/Harvest Plant");
            actions.put(option++, this::removeOrHarvestPlant);
        }

        if (this.currentDay >= 8 && this.field.hasMeteoriteTiles()) {
            System.out.println("[" + option + "] Excavate Meteorite");
            actions.put(option++, this::excavateMeteorite);
        }

        System.out.println("[" + option + "] Next Day");
        int nextDayOption = option;

        int choice = getIntInput("Choose option: ");

        if (actions.containsKey(choice)) {
            actions.get(choice).run();
        } else if (choice == nextDayOption) {
            nextDay();
        } else {
            System.out.println("Invalid choice");
        }
    }


    private List<int[]> selectTiles(String actionName) {
        List<int[]> tiles = new ArrayList<>();
        System.out.println("\n" + actionName);
        System.out.println("[1] Single Tile");
        System.out.println("[2] Multiple Tiles");
        System.out.println("[0] Cancel");
        int mode = getIntInput("Enter Choose Option: ");

        if (mode == 0) {
            return tiles;
        }

        if (mode == 1) {
            int row = getValidCoordinate("row");
            int col = getValidCoordinate("column");
            tiles.add(new int[]{row, col});
        } else if (mode == 2) {
            System.out.print("Enter tiles (e.g., 1,2 3,4 5,6): ");
            String[] inputs = scanner.nextLine().split(" ");
            for (String t : inputs) {
                String[] parts = t.split(",");
                if (parts.length != 2) continue;
                try {
                    int r = Integer.parseInt(parts[0]) - 1;
                    int c = Integer.parseInt(parts[1]) - 1;
                    if (r >= 0 && r < 10 && c >= 0 && c < 10) {
                        tiles.add(new int[]{r, c});
                    }
                } catch (NumberFormatException ignored) {}
            }
        } else {
            System.out.println("Invalid option.");
        }
        return tiles;
    }

    private int getValidCoordinate(String label) {
        while (true) {
            int value = getIntInput("Enter " + label + " (1-10): ");
            if (value >= 1 && value <= 10) {
                return value - 1;
            }
            System.out.println("Coordinate must be 1-10.");
        }
    }

    private int getIntInput(String message) {
        while (true) {
            System.out.print(message);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            }
            System.out.println("Invalid input! Enter a number.");
            scanner.nextLine();
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private char getUniqueLetter(String name, Set<Character> used) {
        name = name.toUpperCase();
        for (char c : name.toCharArray()) {
            if (Character.isLetter(c) && !used.contains(c)) {
                used.add(c);
                return c;
            }
        }

        for (char c = 'A'; c <= 'Z'; c++) {
            if (!used.contains(c)) {
                used.add(c);
                return c;
            }
        }
        return '?';
    }
}
