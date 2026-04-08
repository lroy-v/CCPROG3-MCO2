package com.verdantsun;

public class Field {

    private Tile[][] tiles;

    public Field() {
        this.tiles = new Tile[10][10];

        String[][] map = {
                {"l","l","l","l","l","l","l","l","l","l"},
                {"g","s","g","g","s","s","g","g","s","g"},
                {"l","l","l","l","l","l","l","l","l","l"},
                {"s","s","s","g","g","g","g","s","s","s"},
                {"s","l","s","g","g","g","g","s","l","s"},
                {"s","l","s","g","g","g","g","s","l","s"},
                {"s","s","s","g","g","g","g","s","s","s"},
                {"l","l","l","l","l","l","l","l","l","l"},
                {"g","s","g","g","s","s","g","g","s","g"},
                {"l","l","l","l","l","l","l","l","l","l"}
        };

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {

                String soil;

                switch (map[i][j]) {
                    case "l": soil = "loam"; break;
                    case "s": soil = "sand"; break;
                    case "g": soil = "gravel"; break;
                    default: soil = "loam";
                }

                this.tiles[i][j] = new Tile(soil);
            }
        }
    }

    public Tile getTile(int row, int col) {
        return this.tiles[row][col];
    }

    public void nextDayUpdate() {
        for (Tile[] row : this.tiles) {
            for (Tile tile : row) {
                tile.growPlant();
            }
        }
    }

    public void applyMeteoritePattern(Player player) {

        int[][] affectedTiles = {
                {1,1}, {1,4}, {1,5}, {1,8},
                {3,3}, {3,4}, {3,5}, {3,6},
                {4,1}, {4,3}, {4,4}, {4,5}, {4,6}, {4,8},
                {5,1}, {5,3}, {5,4}, {5,5}, {5,6}, {5,8},
                {6,3}, {6,4}, {6,5}, {6,6},
                {8,1}, {8,4}, {8,5}, {8,8}
        };

        for (int[] coord : affectedTiles) {
            int row = coord[0];
            int col = coord[1];

            Tile tile = this.tiles[row][col];

            tile.setMeteoriteAffected(true);

            if (tile.hasPlant()) {
                Plant plant = tile.getPlant();

                if (plant.getCurrentStage().canHarvest()) {
                    int harvestValue = tile.harvestPlant();
                    player.addSavings(harvestValue);

                    System.out.println(
                            plant.getName() +
                                    " at (" + (row + 1) + "," + (col + 1) + ") was harvested by the meteorite! + " +
                                    harvestValue + " savings."
                    );
                } else {
                    System.out.println(
                            plant.getName() +
                                    " at (" + (row + 1) + "," + (col + 1) + ") was destroyed by the meteorite."
                    );
                    tile.removePlant();
                }
            }
        }
    }

    public boolean hasAnyPlant() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j].hasPlant()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasWaterablePlants() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {

                Tile tile = tiles[i][j];

                if (tile.hasPlant() && tile.getPlant().getCurrentStage().allowsWatering()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasMeteoriteTiles() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j].isMeteoriteAffected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void displayField() {

        System.out.println();

        System.out.print("\t");
        for (int c = 1; c <= 10; c++) {
            System.out.print(c + "\t");
        }
        System.out.println();

        for (int i = 0; i < 10; i++) {

            System.out.print((i + 1) + "\t");

            for (int j = 0; j < 10; j++) {

                Tile tile = this.tiles[i][j];

                if (tile.isMeteoriteAffected()) {
                    System.out.print("M\t");
                } else if (tile.hasPlant()) {
                    System.out.print(tile.getPlant().getSymbol() + "\t");
                } else {
                    switch (tile.getSoilType().toLowerCase()) {
                        case "loam": System.out.print("l\t"); break;
                        case "sand":  System.out.print("s\t"); break;
                        case "gravel":  System.out.print("g\t"); break;
                        default: System.out.print("?\t");
                    }
                }
            }
            System.out.println();
        }

        System.out.println("\nLegend:");
        System.out.println("l = Loam | s = Sand | g = Gravel | M = Meteorite");
        System.out.println("P,T,O,U,W = Plants\n");
    }

    public void displayStatuses() {

        boolean plantHeader = false;
        boolean tileHeader = false;

        for (int i = 0; i < this.tiles.length; i++) {
            for (int j = 0; j < this.tiles[i].length; j++) {

                Tile tile = this.tiles[i][j];

                if (tile.hasPlant()) {
                    if (!plantHeader) {
                        System.out.println("\nPlant Status:");
                        plantHeader = true;
                    }

                    Plant p = tile.getPlant();

                    if (tile.isPermanentlyFertilized()) {

                        String cropDisplay = getCropDisplay(p);

                        System.out.println("(" + (i + 1) + "," + (j + 1) + ") "
                                + p.getName()
                                + " | Stage: " + p.getCurrentStage().getStageName()
                                + " | Growth: " + p.getCurrentGrowth() + "/" + p.getMaxGrowth()
                                + " | Watered: " + (p.isWatered() ? "Yes" : "No")
                                + " | Permanently Fertilized Tile"
                                + " | Crop: " + cropDisplay
                                + " | Preferred Soil: "
                                + (p.isInPreferredSoil(tile.getSoilType()) ? "Yes" : "No"));
                    } else {
                        String fertilizerInfo = "0/0";

                        if (tile.getFertilizer() != null) {
                            fertilizerInfo =
                                    tile.getFertilizer().getEffectDays()
                                            + "/" +
                                            tile.getFertilizer().getMaxEffectDays();
                        }

                        String cropDisplay = getCropDisplay(p);

                        System.out.println("(" + (i + 1) + "," + (j + 1) + ") "
                                + p.getName()
                                + " | Stage: " + p.getCurrentStage().getStageName()
                                + " | Growth: " + p.getCurrentGrowth() + "/" + p.getMaxGrowth()
                                + " | Watered: " + (p.isWatered() ? "Yes" : "No")
                                + " | Fertilizer Count: " + fertilizerInfo
                                + " | Crop: " + cropDisplay
                                + " | Preferred Soil: "
                                + (p.isInPreferredSoil(tile.getSoilType()) ? "Yes" : "No"));
                    }
                } else {
                    if (tile.isMeteoriteAffected()
                            || tile.isPermanentlyFertilized()
                            || tile.getFertilizer() != null) {

                        if (!tileHeader) {
                            System.out.println("\nTile Status:");
                            tileHeader = true;
                        }

                        if (tile.isMeteoriteAffected()) {

                            System.out.println("(" + (i + 1) + "," + (j + 1) + ") Meteorite Tile");
                        } else if (tile.isPermanentlyFertilized()) {

                            System.out.println("(" + (i + 1) + "," + (j + 1) + ") Permanently Fertilized Tile");
                        } else if (tile.getFertilizer() != null) {

                            System.out.println("(" + (i + 1) + "," + (j + 1) + ") Tile Fertilized: "
                                    + tile.getFertilizer().getEffectDays()
                                    + "/" + tile.getFertilizer().getMaxEffectDays());
                        }
                    }
                }
            }
        }
    }

    private String getCropDisplay(Plant p) {
        String cropName = p.getCropName();
        int price = p.getCropPricePerPiece();

        return (cropName != null)
                ? cropName + " (" + price + " gold each)"
                : "Not Available";
    }
}
