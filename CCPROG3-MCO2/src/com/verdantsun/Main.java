package com.verdantsun;

import com.verdantsun.gui.GameGUI;
import com.verdantsun.gui.WelcomeDialog;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            WelcomeDialog welcome = new WelcomeDialog();
            welcome.setVisible(true);

            String playerName = welcome.getPlayerName();
            if (playerName == null || playerName.isBlank()) {
                System.exit(0);
            }

            GameGUI game = new GameGUI(playerName);
            game.setVisible(true);
        });
    }
}
