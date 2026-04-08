package com.verdantsun.gui;

import com.verdantsun.*;
import com.verdantsun.stages.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class FieldPanel extends JPanel {

    private static final int CELL = 54;   // pixel size of each cell
    private static final int MARGIN_TOP  = 28;
    private static final int MARGIN_LEFT = 28;

    private final Field field;
    private final GameGUI gui;

    private boolean highlightMode = false;
    private List<int[]> selectedTiles = new ArrayList<>();

    // ── Soil colours ─────────────────────────────────────────────────────
    private static final Color SOIL_LOAM    = new Color(101, 79, 51);
    private static final Color SOIL_SAND    = new Color(194, 178, 112);
    private static final Color SOIL_GRAVEL  = new Color(100, 100, 100);
    private static final Color METEORITE    = new Color(60, 20, 20);
    private static final Color PERM_FERT    = new Color(40, 80, 40);

    // ── Stage colours ─────────────────────────────────────────────────────
    private static final Color STAGE_SEED   = new Color(100, 200, 100, 200);
    private static final Color STAGE_DORM   = new Color(80, 120, 200, 200);
    private static final Color STAGE_ENRG   = new Color(160, 80, 200, 200);
    private static final Color STAGE_LOWP   = new Color(220, 140, 60, 200);
    private static final Color STAGE_HIGHP  = new Color(220, 70, 70, 200);
    private static final Color STAGE_FULL   = new Color(20, 20, 20, 230);

    // ── Plant symbol colours ──────────────────────────────────────────────
    private static final Color PLT_POTATO   = new Color(230, 170, 60);
    private static final Color PLT_THYME    = new Color(80, 200, 90);
    private static final Color PLT_TOMATO   = new Color(230, 80, 60);
    private static final Color PLT_TURNIP   = new Color(180, 90, 200);
    private static final Color PLT_WHEAT    = new Color(210, 180, 50);
    private static final Color PLT_DEFAULT  = new Color(200, 200, 200);

    public FieldPanel(Field field, GameGUI gui) {
        this.field = field;
        this.gui   = gui;

        setPreferredSize(new Dimension(
                MARGIN_LEFT + 10 * CELL + 4,
                MARGIN_TOP  + 10 * CELL + 4));
        setBackground(GameGUI.BG_DARK);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = (e.getX() - MARGIN_LEFT) / CELL;
                int row = (e.getY() - MARGIN_TOP)  / CELL;
                if (row >= 0 && row < 10 && col >= 0 && col < 10) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        // Right-click confirms action
                        gui.confirmAction();
                    } else {
                        gui.onTileClicked(row, col);
                    }
                }
            }
        });
    }

    public void setHighlightMode(boolean on) {
        this.highlightMode = on;
        repaint();
    }

    public void setSelectedTiles(List<int[]> tiles) {
        this.selectedTiles = tiles;
        repaint();
    }

    // ─────────────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawColumnHeaders(g2);
        drawRowHeaders(g2);

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                drawCell(g2, row, col);
            }
        }

        // Confirm button if in selection mode and tiles selected
        if (highlightMode && !selectedTiles.isEmpty()) {
            drawConfirmHint(g2);
        }
    }

    private void drawColumnHeaders(Graphics2D g2) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(GameGUI.TEXT_DIM);
        for (int c = 0; c < 10; c++) {
            int x = MARGIN_LEFT + c * CELL + CELL / 2;
            g2.drawString(String.valueOf(c + 1), x - 4, MARGIN_TOP - 8);
        }
    }

    private void drawRowHeaders(Graphics2D g2) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(GameGUI.TEXT_DIM);
        for (int r = 0; r < 10; r++) {
            int y = MARGIN_TOP + r * CELL + CELL / 2 + 4;
            g2.drawString(String.valueOf(r + 1), MARGIN_LEFT - 18, y);
        }
    }

    private void drawCell(Graphics2D g2, int row, int col) {
        int x = MARGIN_LEFT + col * CELL;
        int y = MARGIN_TOP  + row * CELL;

        Tile tile = field.getTile(row, col);

        // ── Background fill ──────────────────────────────────────────────
        Color bg;
        if (tile.isMeteoriteAffected()) {
            bg = METEORITE;
        } else if (tile.isPermanentlyFertilized()) {
            bg = PERM_FERT;
        } else {
            bg = soilColor(tile.getSoilType());
        }
        g2.setColor(bg);
        g2.fillRect(x, y, CELL - 1, CELL - 1);

        // ── Stage colour strip on top edge ───────────────────────────────
        if (tile.hasPlant()) {
            Color sc = stageColor(tile.getPlant().getCurrentStage());
            g2.setColor(sc);
            g2.fillRect(x, y, CELL - 1, 6);
        }

        // ── Fertilizer indicator dot ─────────────────────────────────────
        if (tile.getFertilizer() != null && !tile.isPermanentlyFertilized()) {
            g2.setColor(new Color(180, 220, 80, 200));
            g2.fillOval(x + CELL - 12, y + 2, 8, 8);
        }

        // ── Watered indicator ────────────────────────────────────────────
        if (tile.hasPlant() && tile.getPlant().isWatered()) {
            g2.setColor(new Color(80, 160, 230, 180));
            g2.fillOval(x + 2, y + 2, 8, 8);
        }

        // ── Content symbol ───────────────────────────────────────────────
        if (tile.isMeteoriteAffected()) {
            drawCenteredText(g2, "M", x, y, GameGUI.ACCENT_RED,
                    new Font("Courier New", Font.BOLD, 22));
        } else if (tile.hasPlant()) {
            Plant p = tile.getPlant();
            Color plantColor = plantColor(p.getName());
            drawCenteredText(g2, String.valueOf(p.getSymbol()), x, y, plantColor,
                    new Font("Courier New", Font.BOLD, 22));

            // Mini growth bar at bottom
            drawGrowthBar(g2, x, y, p);
        } else {
            String soilSym = soilSymbol(tile.getSoilType());
            drawCenteredText(g2, soilSym, x, y, soilTextColor(tile.getSoilType()),
                    new Font("Courier New", Font.PLAIN, 14));
        }

        // ── Selection highlight ──────────────────────────────────────────
        if (isSelected(row, col)) {
            g2.setColor(new Color(255, 255, 80, 80));
            g2.fillRect(x, y, CELL - 1, CELL - 1);
            g2.setColor(new Color(255, 255, 80, 220));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawRect(x + 1, y + 1, CELL - 3, CELL - 3);
            g2.setStroke(new BasicStroke(1));
        } else if (highlightMode) {
            // Subtle hover-ready tint
            g2.setColor(new Color(255, 255, 255, 12));
            g2.fillRect(x, y, CELL - 1, CELL - 1);
        }

        // ── Cell border ──────────────────────────────────────────────────
        g2.setColor(new Color(0, 0, 0, 80));
        g2.drawRect(x, y, CELL - 1, CELL - 1);
    }

    private void drawGrowthBar(Graphics2D g2, int x, int y, Plant p) {
        int barW = CELL - 6;
        int barH = 4;
        int bx = x + 3;
        int by = y + CELL - barH - 3;

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(bx, by, barW, barH, 3, 3);

        int max = p.getMaxGrowth();
        if (max > 0) {
            float ratio = Math.min(1f, (float) p.getCurrentGrowth() / max);
            int filled = (int) (barW * ratio);
            g2.setColor(new Color(80, 220, 80, 200));
            g2.fillRoundRect(bx, by, filled, barH, 3, 3);
        }
    }

    private void drawConfirmHint(Graphics2D g2) {
        String msg = "✔  " + selectedTiles.size() + " tile(s) selected — right-click or use the Confirm button";
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(msg);
        int px = (getWidth() - tw) / 2;
        int py = MARGIN_TOP + 10 * CELL + 18;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(px - 8, py - 14, tw + 16, 20, 8, 8);
        g2.setColor(GameGUI.ACCENT_GOLD);
        g2.drawString(msg, px, py);
    }

    private void drawCenteredText(Graphics2D g2, String text, int x, int y,
                                  Color color, Font font) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (CELL - fm.stringWidth(text)) / 2;
        int ty = y + (CELL + fm.getAscent() - fm.getDescent()) / 2 - 2;
        g2.setColor(new Color(0, 0, 0, 120));
        g2.drawString(text, tx + 1, ty + 1);
        g2.setColor(color);
        g2.drawString(text, tx, ty);
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private boolean isSelected(int row, int col) {
        for (int[] t : selectedTiles)
            if (t[0] == row && t[1] == col) return true;
        return false;
    }

    private Color soilColor(String soil) {
        return switch (soil.toLowerCase()) {
            case "loam"   -> SOIL_LOAM;
            case "sand"   -> SOIL_SAND;
            case "gravel" -> SOIL_GRAVEL;
            default       -> new Color(80, 80, 80);
        };
    }

    private Color soilTextColor(String soil) {
        return switch (soil.toLowerCase()) {
            case "loam"   -> new Color(200, 170, 120);
            case "sand"   -> new Color(120, 100, 50);
            case "gravel" -> new Color(190, 190, 190);
            default       -> GameGUI.TEXT_DIM;
        };
    }

    private String soilSymbol(String soil) {
        return switch (soil.toLowerCase()) {
            case "loam"   -> "l";
            case "sand"   -> "s";
            case "gravel" -> "g";
            default       -> "?";
        };
    }

    private Color stageColor(com.verdantsun.stages.Stage stage) {
        String name = stage.getStageName().toLowerCase();
        return switch (name) {
            case "seedling"      -> STAGE_SEED;
            case "dormant"       -> STAGE_DORM;
            case "energizing"    -> STAGE_ENRG;
            case "low productive"  -> STAGE_LOWP;
            case "high productive" -> STAGE_HIGHP;
            case "fully mature"  -> STAGE_FULL;
            default              -> new Color(180, 180, 180, 180);
        };
    }

    private Color plantColor(String name) {
        return switch (name.toLowerCase()) {
            case "potato" -> PLT_POTATO;
            case "thyme"  -> PLT_THYME;
            case "tomato" -> PLT_TOMATO;
            case "turnip" -> PLT_TURNIP;
            case "wheat"  -> PLT_WHEAT;
            default       -> PLT_DEFAULT;
        };
    }
}
