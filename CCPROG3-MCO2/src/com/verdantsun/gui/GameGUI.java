package com.verdantsun.gui;

import com.verdantsun.*;
import com.verdantsun.stages.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GameGUI extends JFrame {

    private Player player;
    private Field field;
    private WateringCan wateringCan;
    private int currentDay;
    private HighScoreManager highScoreManager;
    private int excavationsToday;
    private HashMap<String, Plant> plantCatalog;
    private HashMap<String, Fertilizer> fertilizerCatalog;

    private FieldPanel fieldPanel;
    private JLabel dayLabel;
    private JLabel savingsLabel;
    private JLabel waterLabel;
    private JTextArea logArea;
    private JPanel actionPanel;
    private JPanel infoPanel;

    static final Color BG_DARK      = new Color(18, 28, 18);
    static final Color BG_PANEL     = new Color(28, 42, 28);
    static final Color BG_HEADER    = new Color(20, 55, 20);
    static final Color ACCENT_GREEN = new Color(80, 200, 80);
    static final Color ACCENT_GOLD  = new Color(220, 185, 60);
    static final Color ACCENT_BLUE  = new Color(80, 160, 220);
    static final Color ACCENT_RED   = new Color(210, 80, 80);
    static final Color TEXT_MAIN    = new Color(230, 240, 230);
    static final Color TEXT_DIM     = new Color(140, 160, 140);
    static final Color BORDER_COLOR = new Color(50, 90, 50);

    static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 15);
    static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font FONT_MONO   = new Font("Courier New", Font.PLAIN, 12);
    static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD, 12);
    static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);

    private String pendingAction = null;
    private Plant  pendingPlant  = null;
    private Fertilizer pendingFertilizer = null;
    private List<int[]> selectedTiles = new ArrayList<>();

    public GameGUI(String playerName) {
        player             = new Player(playerName);
        field              = new Field();
        wateringCan        = new WateringCan(10);
        currentDay         = 1;
        highScoreManager   = new HighScoreManager();
        excavationsToday   = 0;
        plantCatalog       = PlantFactory.createPlants();
        fertilizerCatalog  = FertilizerFactory.createFertilizers();

        highScoreManager.loadScores();

        buildUI();
        refreshAll();
    }

    private void buildUI() {
        setTitle("Verdant Sun Farming Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 860));
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8, 0));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(8, 10, 4, 10));

        fieldPanel = new FieldPanel(field, this);
        JPanel fieldWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        fieldWrapper.setOpaque(false);
        fieldWrapper.add(fieldPanel);
        center.add(fieldWrapper, BorderLayout.CENTER);

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BG_PANEL);
        infoPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 10, 10, 10)));

        JScrollPane infoScroll = new JScrollPane(infoPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        infoScroll.getViewport().setBackground(BG_PANEL);
        infoScroll.setBorder(null);
        infoScroll.setPreferredSize(new Dimension(210, 0));
        center.add(infoScroll, BorderLayout.EAST);

        add(center, BorderLayout.CENTER);

        JPanel southArea = new JPanel(new BorderLayout(0, 6));
        southArea.setOpaque(false);
        southArea.setBorder(new EmptyBorder(0, 10, 10, 10));

        actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBackground(BG_PANEL);
        actionPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 16, 10, 16)));

        JPanel actionWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        actionWrapper.setOpaque(false);
        actionWrapper.add(actionPanel);
        southArea.add(actionWrapper, BorderLayout.NORTH);

        southArea.add(buildLogPanel(), BorderLayout.CENTER);
        add(southArea, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(BG_HEADER);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, BORDER_COLOR),
                new EmptyBorder(10, 16, 10, 16)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 12, 0, 12);

        gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.WEST; gc.weightx = 0;
        JLabel title = new JLabel("VERDANT SUN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ACCENT_GREEN);
        header.add(title, gc);

        gc.gridx = 1;
        dayLabel = makeStatLabel("Day 1 / 20", ACCENT_GOLD);
        header.add(dayLabel, gc);

        gc.gridx = 2;
        savingsLabel = makeStatLabel("Savings: 1000", ACCENT_GREEN);
        header.add(savingsLabel, gc);

        gc.gridx = 3;
        waterLabel = makeStatLabel("Watering Can: 10/10", ACCENT_BLUE);
        header.add(waterLabel, gc);

        gc.gridx = 4; gc.weightx = 1;
        header.add(Box.createHorizontalGlue(), gc);

        gc.gridx = 5; gc.weightx = 0;
        JButton hsBtn = makeSmallButton("High Scores", ACCENT_GOLD);
        hsBtn.addActionListener(e -> showHighScores());
        header.add(hsBtn, gc);

        return header;
    }

    private JPanel buildLogPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
        p.setPreferredSize(new Dimension(0, 110));

        JLabel lbl = new JLabel("Activity Log");
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_DIM);
        p.add(lbl, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(FONT_MONO);
        logArea.setBackground(BG_DARK);
        logArea.setForeground(ACCENT_GREEN);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(new EmptyBorder(4, 6, 4, 6));

        JScrollPane sp = new JScrollPane(logArea);
        sp.setBorder(null);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    void refreshAll() {
        dayLabel.setText("Day " + currentDay + " / 20");
        savingsLabel.setText("Savings: " + player.getSavings());
        waterLabel.setText("Watering Can: " + wateringCan.getCurrentWaterLevel() + "/10");
        fieldPanel.repaint();
        rebuildActionPanel();
        rebuildInfoPanel();
    }

    private void rebuildActionPanel() {
        actionPanel.removeAll();

        if (pendingAction != null) {
            buildSelectionModePanel();
        } else {
            buildNormalActionPanel();
        }

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private void buildNormalActionPanel() {
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (playerCanPlant()) {
            btnRow.add(makeActionBtn("Plant Seed", ACCENT_GREEN, e -> startPlantAction()));
        }
        if (field.hasWaterablePlants() && !wateringCan.isEmpty()) {
            btnRow.add(makeActionBtn("Water Plant", ACCENT_BLUE, e -> startWaterAction()));
        }
        if (player.canAfford(100)) {
            btnRow.add(makeActionBtn("Refill Can (100g)", ACCENT_BLUE, e -> refillCan()));
        }
        btnRow.add(makeActionBtn("Apply Fertilizer", new Color(180, 120, 220), e -> startFertilizeAction()));
        if (field.hasAnyPlant()) {
            btnRow.add(makeActionBtn("Harvest / Remove", ACCENT_GOLD, e -> startHarvestAction()));
        }
        if (currentDay >= 8 && field.hasMeteoriteTiles() && excavationsToday < 5) {
            btnRow.add(makeActionBtn("Excavate Meteorite", ACCENT_RED, e -> startExcavateAction()));
        }

        JButton nextDay = new JButton("Next Day ->");
        nextDay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nextDay.setBackground(new Color(50, 120, 50));
        nextDay.setForeground(Color.WHITE);
        nextDay.setFocusPainted(false);
        nextDay.setBorderPainted(false);
        nextDay.setOpaque(true);
        nextDay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nextDay.setBorder(new EmptyBorder(7, 18, 7, 18));
        nextDay.addActionListener(e -> doNextDay());
        btnRow.add(nextDay);

        actionPanel.add(btnRow);

        actionPanel.add(Box.createVerticalStrut(6));
        JPanel legendRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        legendRow.setOpaque(false);
        legendRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        addLegendChip(legendRow, "l", "Loam", new Color(139, 115, 85));
        addLegendChip(legendRow, "s", "Sand", new Color(210, 195, 130));
        addLegendChip(legendRow, "g", "Gravel", new Color(120, 120, 120));
        addLegendChip(legendRow, "M", "Meteorite", ACCENT_RED);
        addLegendChip(legendRow, "P", "Potato", new Color(210, 160, 60));
        addLegendChip(legendRow, "T", "Thyme", new Color(80, 190, 90));
        addLegendChip(legendRow, "O", "Tomato", new Color(220, 80, 60));
        addLegendChip(legendRow, "U", "Turnip", new Color(160, 90, 180));
        addLegendChip(legendRow, "W", "Wheat", new Color(200, 170, 60));
        actionPanel.add(legendRow);
    }

    private void buildSelectionModePanel() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        String actionLabel = switch (pendingAction) {
            case "plant"     -> "Planting: " + (pendingPlant != null ? pendingPlant.getName() : "");
            case "water"     -> "Watering plants";
            case "fertilize" -> "Fertilizing: " + (pendingFertilizer != null ? pendingFertilizer.getName() : "");
            case "harvest"   -> "Harvest / Remove";
            case "excavate"  -> "Excavating meteorite tiles";
            default          -> pendingAction;
        };

        JLabel statusLbl = new JLabel(actionLabel);
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLbl.setForeground(ACCENT_GOLD);
        row.add(statusLbl);

        JLabel sep = new JLabel("  |  ");
        sep.setForeground(TEXT_DIM);
        row.add(sep);

        JLabel countLbl = new JLabel(selectedTiles.size() + " tile(s) selected");
        countLbl.setFont(FONT_BODY);
        countLbl.setForeground(TEXT_DIM);
        row.add(countLbl);

        JButton confirm = new JButton("Confirm");
        confirm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirm.setBackground(new Color(40, 110, 40));
        confirm.setForeground(Color.WHITE);
        confirm.setFocusPainted(false);
        confirm.setBorderPainted(false);
        confirm.setOpaque(true);
        confirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirm.setBorder(new EmptyBorder(6, 16, 6, 16));
        confirm.setEnabled(!selectedTiles.isEmpty());
        confirm.addActionListener(e -> confirmAction());
        row.add(confirm);

        JButton cancel = new JButton("Cancel");
        cancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancel.setBackground(new Color(110, 30, 30));
        cancel.setForeground(Color.WHITE);
        cancel.setFocusPainted(false);
        cancel.setBorderPainted(false);
        cancel.setOpaque(true);
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.setBorder(new EmptyBorder(6, 16, 6, 16));
        cancel.addActionListener(e -> cancelAction());
        row.add(cancel);

        actionPanel.add(row);
    }

    private void rebuildInfoPanel() {
        infoPanel.removeAll();
        addSectionLabel(infoPanel, "TILE INFO");
        addInfoHint(infoPanel, "Click a tile to see\ndetailed plant info.");
        infoPanel.revalidate();
        infoPanel.repaint();
    }

    void onTileClicked(int row, int col) {
        if (pendingAction != null) {
            toggleTileSelection(row, col);
        } else {
            showTileInfo(row, col);
        }
    }

    private void showTileInfo(int row, int col) {
        Tile tile = field.getTile(row, col);
        infoPanel.removeAll();
        addSectionLabel(infoPanel, "TILE (" + (row+1) + "," + (col+1) + ")");

        addInfoRow(infoPanel, "Soil:", capitalize(tile.getSoilType()));
        addInfoRow(infoPanel, "Meteorite:", tile.isMeteoriteAffected() ? "Yes" : "No");

        boolean permFert = tile.isPermanentlyFertilized() || tile.isMeteoriteAffected();
        addInfoRow(infoPanel, "Perm. Fertilized:", permFert ? "Yes" : "No");

        if (tile.getFertilizer() != null && !tile.isPermanentlyFertilized() && !tile.isMeteoriteAffected()) {
            Fertilizer f = tile.getFertilizer();
            addInfoRow(infoPanel, "Fertilizer:", f.getName());
            addInfoRow(infoPanel, "Effect days:", f.getEffectDays() + "/" + f.getMaxEffectDays());
        }

        if (tile.hasPlant()) {
            Plant p = tile.getPlant();
            infoPanel.add(Box.createVerticalStrut(8));
            addSectionLabel(infoPanel, "PLANT");
            addInfoRow(infoPanel, "Name:", p.getName());
            addInfoRow(infoPanel, "Stage:", p.getCurrentStage().getStageName());
            addInfoRow(infoPanel, "Growth:", p.getCurrentGrowth() + "/" + p.getMaxGrowth());
            addInfoRow(infoPanel, "Watered:", p.isWatered() ? "Yes" : "No");
            addInfoRow(infoPanel, "Pref. Soil:", capitalize(p.getPreferredSoil()));
            addInfoRow(infoPanel, "On Pref. Soil:", p.isInPreferredSoil(tile.getSoilType()) ? "Yes" : "No");

            String cropName = p.getCropName();
            if (cropName != null) {
                addInfoRow(infoPanel, "Crop:", cropName);
                addInfoRow(infoPanel, "Price:", p.getCropPricePerPiece() + "g each");
            }
            addInfoRow(infoPanel, "Can harvest:", p.getCurrentStage().canHarvest() ? "Yes" : "No");
        }

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private void startPlantAction() {
        Plant chosen = choosePlant();
        if (chosen == null) return;
        pendingPlant  = chosen;
        pendingAction = "plant";
        fieldPanel.setHighlightMode(true);
        log("Click tiles to plant " + chosen.getName() + ". Press Confirm when done.");
        rebuildActionPanel();
    }

    private void startWaterAction() {
        pendingAction = "water";
        fieldPanel.setHighlightMode(true);
        log("Click tiles to water. Press Confirm when done.");
        rebuildActionPanel();
    }

    private void startFertilizeAction() {
        Fertilizer chosen = chooseFertilizer();
        if (chosen == null) return;
        pendingFertilizer = chosen;
        pendingAction     = "fertilize";
        fieldPanel.setHighlightMode(true);
        log("Click tiles to fertilize with " + chosen.getName() + ". Press Confirm when done.");
        rebuildActionPanel();
    }

    private void startHarvestAction() {
        pendingAction = "harvest";
        fieldPanel.setHighlightMode(true);
        log("Click tiles to harvest/remove. Press Confirm when done.");
        rebuildActionPanel();
    }

    private void startExcavateAction() {
        if (excavationsToday >= 5) { log("Daily excavation limit reached."); return; }
        pendingAction = "excavate";
        fieldPanel.setHighlightMode(true);
        log("Click meteorite tiles to excavate (500g each). Press Confirm when done.");
        rebuildActionPanel();
    }

    private void toggleTileSelection(int row, int col) {
        for (int i = 0; i < selectedTiles.size(); i++) {
            if (selectedTiles.get(i)[0] == row && selectedTiles.get(i)[1] == col) {
                selectedTiles.remove(i);
                fieldPanel.setSelectedTiles(selectedTiles);
                fieldPanel.repaint();
                rebuildActionPanel();
                return;
            }
        }
        selectedTiles.add(new int[]{row, col});
        fieldPanel.setSelectedTiles(selectedTiles);
        fieldPanel.repaint();
        rebuildActionPanel();
    }

    void confirmAction() {
        if (pendingAction == null || selectedTiles.isEmpty()) {
            cancelAction();
            return;
        }
        switch (pendingAction) {
            case "plant"     -> executePlant();
            case "water"     -> executeWater();
            case "fertilize" -> executeFertilize();
            case "harvest"   -> executeHarvest();
            case "excavate"  -> executeExcavate();
        }
        cancelAction();
    }

    void cancelAction() {
        pendingAction     = null;
        pendingPlant      = null;
        pendingFertilizer = null;
        selectedTiles.clear();
        fieldPanel.setHighlightMode(false);
        fieldPanel.setSelectedTiles(selectedTiles);
        refreshAll();
    }

    private void executePlant() {
        for (int[] coord : selectedTiles) {
            if (!player.canAfford(pendingPlant.getSeedPrice())) {
                log("Not enough savings for more seeds."); break;
            }
            Tile tile = field.getTile(coord[0], coord[1]);
            Plant newPlant = new Plant(
                    pendingPlant.getName(), pendingPlant.getSeedPrice(),
                    pendingPlant.getYield(), pendingPlant.getPreferredSoil(),
                    pendingPlant.cloneStages());
            if (tile.plantSeed(newPlant)) {
                player.deductSavings(pendingPlant.getSeedPrice());
                log("Planted " + pendingPlant.getName() + " at (" + (coord[0]+1) + "," + (coord[1]+1) + ")");
            } else {
                if (tile.isMeteoriteAffected())
                    log("X (" + (coord[0]+1) + "," + (coord[1]+1) + ") is a meteorite tile.");
                else if (!tile.isEmpty())
                    log("X (" + (coord[0]+1) + "," + (coord[1]+1) + ") is not empty.");
                else
                    log("X Wrong soil at (" + (coord[0]+1) + "," + (coord[1]+1) + "). Needs " + capitalize(pendingPlant.getPreferredSoil()) + ".");
            }
        }
    }

    private void executeWater() {
        for (int[] coord : selectedTiles) {
            if (wateringCan.isEmpty()) { log("Watering can empty!"); break; }
            Tile tile = field.getTile(coord[0], coord[1]);
            if (tile.waterPlant()) {
                wateringCan.water();
                log("Watered (" + (coord[0]+1) + "," + (coord[1]+1) + ")");
            } else {
                log("Cannot water (" + (coord[0]+1) + "," + (coord[1]+1) + ")");
            }
        }
    }

    private void executeFertilize() {
        for (int[] coord : selectedTiles) {
            if (!player.canAfford(pendingFertilizer.getPrice())) {
                log("Not enough savings."); break;
            }
            Tile tile = field.getTile(coord[0], coord[1]);
            Fertilizer newF = new Fertilizer(
                    pendingFertilizer.getName(),
                    pendingFertilizer.getPrice(),
                    pendingFertilizer.getEffectDays());
            if (tile.applyFertilizer(newF)) {
                player.deductSavings(pendingFertilizer.getPrice());
                log("Fertilized (" + (coord[0]+1) + "," + (coord[1]+1) + ")");
            } else {
                log("Cannot fertilize (" + (coord[0]+1) + "," + (coord[1]+1) + ")");
            }
        }
    }

    private void executeHarvest() {
        for (int[] coord : selectedTiles) {
            Tile tile = field.getTile(coord[0], coord[1]);
            if (!tile.hasPlant()) { log("No plant at (" + (coord[0]+1) + "," + (coord[1]+1) + ")"); continue; }
            int earned = tile.harvestPlant();
            if (earned > 0) {
                player.addSavings(earned);
                log("Harvested (" + (coord[0]+1) + "," + (coord[1]+1) + ") — +" + earned + "g");
            } else {
                tile.removePlant();
                log("Removed plant at (" + (coord[0]+1) + "," + (coord[1]+1) + ")");
            }
        }
    }

    private void executeExcavate() {
        for (int[] coord : selectedTiles) {
            if (excavationsToday >= 5) { log("Daily limit reached."); break; }
            if (!player.canAfford(500)) { log("Not enough savings."); break; }
            Tile tile = field.getTile(coord[0], coord[1]);
            if (!tile.isMeteoriteAffected()) { log("No meteorite at (" + (coord[0]+1) + "," + (coord[1]+1) + ")"); continue; }
            player.deductSavings(500);
            tile.excavate();
            excavationsToday++;
            log("⛏ Excavated (" + (coord[0]+1) + "," + (coord[1]+1) + ") — soil restored, permanently fertilized.");
        }
    }

    private void refillCan() {
        if (!player.canAfford(100)) { log("Not enough savings to refill."); return; }
        player.deductSavings(100);
        wateringCan.refill();
        log("🪣 Watering can refilled! (-100g)");
        refreshAll();
    }

    private void doNextDay() {
        if (currentDay >= 20) {
            endGame();
            return;
        }
        currentDay++;
        excavationsToday = 0;
        player.addSavings(50);
        field.nextDayUpdate();

        log("──────────────────────────");
        log("Day " + currentDay + " started. (+50g daily income)");

        if (currentDay == 15) {
            field.applyMeteoritePattern(player);
            log("☄ A METEORITE has struck the farm!");
            JOptionPane.showMessageDialog(this,
                    "A meteorite has struck the farm!\nSome tiles are now meteorite-affected.\nYou can excavate them from Day 15 onwards.",
                    "Meteorite Event!", JOptionPane.WARNING_MESSAGE);
        }

        refreshAll();
    }

    private Plant choosePlant() {
        List<Plant> sorted = new ArrayList<>(plantCatalog.values());
        sorted.sort(Comparator.comparing(Plant::getName));

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        ButtonGroup group = new ButtonGroup();
        List<JRadioButton> buttons = new ArrayList<>();

        for (Plant p : sorted) {
            String label = "<html><b>" + p.getName() + "</b>  <span style='color:gray'>"
                    + p.getSeedPrice() + "g</span>  |  Yield: " + p.getYield()
                    + "  |  Max Growth: " + p.getMaxGrowth()
                    + "  |  Soil: " + capitalize(p.getPreferredSoil()) + "</html>";
            JRadioButton rb = new JRadioButton(label);
            rb.setBackground(BG_PANEL);
            rb.setForeground(TEXT_MAIN);
            rb.setFont(FONT_BODY);
            rb.setBorder(new EmptyBorder(4, 6, 4, 6));
            rb.setEnabled(player.canAfford(p.getSeedPrice()));
            group.add(rb);
            buttons.add(rb);
            panel.add(rb);
        }

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Select a Plant to Plant", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        for (int i = 0; i < buttons.size(); i++)
            if (buttons.get(i).isSelected()) return sorted.get(i);
        return null;
    }

    private Fertilizer chooseFertilizer() {
        List<Fertilizer> sorted = new ArrayList<>(fertilizerCatalog.values());
        sorted.sort(Comparator.comparing(Fertilizer::getName));

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        ButtonGroup group = new ButtonGroup();
        List<JRadioButton> buttons = new ArrayList<>();

        for (Fertilizer f : sorted) {
            String label = "<html><b>" + f.getName() + "</b>  <span style='color:gray'>"
                    + f.getPrice() + "g</span>  |  Effect Days: " + f.getEffectDays() + "</html>";
            JRadioButton rb = new JRadioButton(label);
            rb.setBackground(BG_PANEL);
            rb.setForeground(TEXT_MAIN);
            rb.setFont(FONT_BODY);
            rb.setBorder(new EmptyBorder(4, 6, 4, 6));
            rb.setEnabled(player.canAfford(f.getPrice()));
            group.add(rb);
            buttons.add(rb);
            panel.add(rb);
        }

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Select a Fertilizer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        for (int i = 0; i < buttons.size(); i++)
            if (buttons.get(i).isSelected()) return sorted.get(i);
        return null;
    }

    private void showHighScores() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTextArea area = new JTextArea(
                "High Score data stored in data/HighScores.json\n" +
                        "Final scores are recorded when the game ends.\n\n" +
                        "Current savings: " + player.getSavings() + "g");
        area.setEditable(false);
        area.setFont(FONT_MONO);
        area.setBackground(BG_PANEL);
        area.setForeground(ACCENT_GREEN);
        area.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(area, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "High Scores", JOptionPane.PLAIN_MESSAGE);
    }

    private void endGame() {
        highScoreManager.addScore(player.getName(), player.getSavings());
        highScoreManager.saveScores();

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Season Complete!", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ACCENT_GREEN);

        JLabel savings = new JLabel("Final Savings: " + player.getSavings() + "g", SwingConstants.CENTER);
        savings.setFont(new Font("Segoe UI", Font.BOLD, 18));
        savings.setForeground(ACCENT_GOLD);

        JLabel msg = new JLabel("<html><center>Your score has been saved!<br>Thanks for playing Verdant Sun.</center></html>", SwingConstants.CENTER);
        msg.setFont(FONT_BODY);
        msg.setForeground(TEXT_MAIN);

        panel.add(title, BorderLayout.NORTH);
        panel.add(savings, BorderLayout.CENTER);
        panel.add(msg, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, "Game Over", JOptionPane.PLAIN_MESSAGE);
        System.exit(0);
    }

    void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private boolean playerCanPlant() {
        for (Plant p : plantCatalog.values())
            if (player.canAfford(p.getSeedPrice())) return true;
        return false;
    }

    String getPendingAction() { return pendingAction; }
    List<int[]> getSelectedTiles() { return selectedTiles; }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private JLabel makeStatLabel(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(color);
        return lbl;
    }

    private JButton makeSmallButton(String text, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setForeground(fg);
        btn.setBackground(BG_PANEL);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(fg, 1, true));
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeActionBtn(String text, Color color, ActionListener al) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setForeground(color);
        btn.setBackground(BG_DARK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setBorder(new CompoundBorder(
                new LineBorder(color.darker(), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(al);
        return btn;
    }

    private void addSectionLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_DIM);
        lbl.setBorder(new EmptyBorder(2, 0, 4, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
    }

    private void addLegendChip(JPanel panel, String symbol, String desc, Color color) {
        JLabel chip = new JLabel(symbol + " " + desc);
        chip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chip.setForeground(color);
        panel.add(chip);
    }

    private void addInfoRow(JPanel panel, String key, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel k = new JLabel(key);
        k.setFont(new Font("Segoe UI", Font.BOLD, 12));
        k.setForeground(TEXT_DIM);

        JLabel v = new JLabel(value);
        v.setFont(FONT_SMALL);
        v.setForeground(TEXT_MAIN);

        row.add(k); row.add(v);
        panel.add(row);
    }

    private void addInfoHint(JPanel panel, String text) {
        JLabel lbl = new JLabel("<html><center>" + text.replace("\n","<br>") + "</center></html>");
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_DIM);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lbl);
    }
}
