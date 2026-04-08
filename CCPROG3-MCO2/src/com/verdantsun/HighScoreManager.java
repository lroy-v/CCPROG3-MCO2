package com.verdantsun;
import java.io.*;
import java.util.*;

public class HighScoreManager {

    private ArrayList<HighScoreEntry> highScores;

    public HighScoreManager() {
        highScores = new ArrayList<>();
    }

    public void loadScores() {
        highScores.clear();
        try {
            File file = new File("data/HighScores.json");
            if (!file.exists()) return;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) json.append(line);
            reader.close();

            String content = json.toString().trim();
            if (content.isEmpty() || content.equals("{}")) return;

            content = content.substring(1, content.length() - 1); // remove { }
            String[] entries = content.split("},");
            for (int i = 0; i < entries.length; i++) {
                if (!entries[i].endsWith("}")) entries[i] += "}";
                String[] keySplit = entries[i].split(":\\{");
                if (keySplit.length < 2) continue;

                String values = keySplit[1].replace("}", "");
                String[] attributes = values.split(",");
                String name = "";
                int savings = 0;

                for (String attr : attributes) {
                    String[] pair = attr.split(":");
                    if (pair.length < 2) continue;
                    String key = pair[0].replace("\"", "").trim();
                    String value = pair[1].replace("\"", "").trim();
                    if (key.equals("name")) name = value;
                    if (key.equals("savings")) savings = Integer.parseInt(value);
                }
                highScores.add(new HighScoreEntry(name, savings));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveScores() {
        try {
            File folder = new File("data");
            if (!folder.exists()) folder.mkdirs(); // create folder if missing

            BufferedWriter writer = new BufferedWriter(new FileWriter("data/HighScores.json"));
            writer.write("{");
            for (int i = 0; i < highScores.size(); i++) {
                HighScoreEntry entry = highScores.get(i);
                writer.write("\"" + (i+1) + "\":{");
                writer.write("\"name\":\"" + entry.getPlayerName() + "\",");
                writer.write("\"savings\":" + entry.getSavings() + "}");
                if (i < highScores.size() - 1) writer.write(",");
            }
            writer.write("}");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addScore(String name, int savings) {

        highScores.add(new HighScoreEntry(name, savings));

        highScores.sort((a, b) -> b.getSavings() - a.getSavings());

        if (highScores.size() > 10) {
            highScores.remove(highScores.size() - 1);
        }
    }
}
