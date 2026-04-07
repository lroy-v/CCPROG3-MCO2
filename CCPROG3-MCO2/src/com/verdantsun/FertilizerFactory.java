package com.verdantsun;

import java.util.*;

public class FertilizerFactory {

    public static HashMap<String, Fertilizer> createFertilizers() {
        HashMap<String, Fertilizer> fertilizers = new HashMap<>();

        fertilizers.put("quick", new Fertilizer("Quick Fertilizer", 100, 2));
        fertilizers.put("lasting", new Fertilizer("Lasting Fertilizer", 150, 3));
        fertilizers.put("premium", new Fertilizer("Premium Fertilizer", 200, 6));

        return fertilizers;
    }
}
