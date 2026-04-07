package com.verdantsun;

import com.verdantsun.stages.*;
import java.util.*;

public class PlantFactory {

    public static HashMap<String, Plant> createPlants() {
        HashMap<String, Plant> plants = new HashMap<>();

        plants.put("turnip", new Plant("Turnip", 10, 2, "loam", createStages("turnip")));
        plants.put("potato", new Plant("Potato", 20, 3, "sand", createStages("potato")));
        plants.put("wheat", new Plant("Wheat", 15, 4, "loam", createStages("wheat")));
        plants.put("thyme", new Plant("Thyme", 25, 6, "gravel", createStages("thyme")));
        plants.put("tomato", new Plant("Tomato", 10, 9, "sand", createStages("tomato")));

        return plants;
    }

    private static List<Stage> createStages(String name) {
        List<Stage> stages = new ArrayList<>();

        switch (name) {
            case "turnip":
                for (int i = 0; i < 3; i++) stages.add(new SeedlingStage());
                stages.add(new DormantStage());
                for (int i = 0; i < 4; i++) stages.add(new LowProductiveStage());
                stages.add(new HighProductiveStage());
                stages.add(new FullyMatureStage());
                break;

            case "wheat":
                stages.add(new SeedlingStage());
                for (int i = 0; i < 6; i++) stages.add(new LowProductiveStage());
                for (int i = 0; i < 3; i++) stages.add(new EnergizingStage());
                for (int i = 0; i < 2; i++) stages.add(new HighProductiveStage());
                for (int i = 0; i < 3; i++) stages.add(new EnergizingStage());
                for (int i = 0; i < 2; i++) stages.add(new HighProductiveStage());
                for (int i = 0; i < 3; i++) stages.add(new EnergizingStage());
                stages.add(new FullyMatureStage());
                break;

            case "thyme":
                for (int i = 0; i < 12; i++) stages.add(new SeedlingStage());
                stages.add(new FullyMatureStage());
                break;

            case "potato":
                for (int i = 0; i < 5; i++) stages.add(new SeedlingStage());
                stages.add(new EnergizingStage());
                stages.add(new DormantStage());
                stages.add(new LowProductiveStage());
                stages.add(new EnergizingStage());
                stages.add(new DormantStage());
                stages.add(new LowProductiveStage());
                stages.add(new EnergizingStage());
                stages.add(new DormantStage());
                stages.add(new LowProductiveStage());
                stages.add(new EnergizingStage());
                stages.add(new DormantStage());
                stages.add(new LowProductiveStage());
                stages.add(new EnergizingStage());
                stages.add(new DormantStage());
                stages.add(new HighProductiveStage());
                stages.add(new EnergizingStage());
                stages.add(new DormantStage());
                stages.add(new HighProductiveStage());
                stages.add(new EnergizingStage());
                stages.add(new DormantStage());
                stages.add(new HighProductiveStage());
                stages.add(new EnergizingStage());
                stages.add(new DormantStage());
                stages.add(new HighProductiveStage());
                stages.add(new FullyMatureStage());
                break;

            case "tomato":
                stages.add(new SeedlingStage());
                for (int i = 0; i < 12; i++) stages.add(new DormantStage());
                for (int i = 0; i < 10; i++) stages.add(new EnergizingStage());
                stages.add(new FullyMatureStage());
                break;
        }

        return stages;
    }
}
