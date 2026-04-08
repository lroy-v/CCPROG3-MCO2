package com.verdantsun.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomeDialog extends JDialog {

    private String playerName = null;
    private final JTextField nameField;

    public WelcomeDialog() {
        setTitle("Verdant Sun — Welcome");
        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(GameGUI.BG_DARK);

        JPanel banner = new JPanel(new GridBagLayout());
        banner.setBackground(GameGUI.BG_HEADER);
        banner.setBorder(new MatteBorder(0, 0, 2, 0, GameGUI.BORDER_COLOR));
        banner.setPreferredSize(new Dimension(420, 130));

        JLabel titleLbl = new JLabel("VERDANT SUN", SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLbl.setForeground(GameGUI.ACCENT_GREEN);

        JLabel subLbl = new JLabel("Farming Simulator", SwingConstants.CENTER);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLbl.setForeground(GameGUI.TEXT_DIM);

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.add(titleLbl);
        textStack.add(Box.createVerticalStrut(2));
        textStack.add(subLbl);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 10, 0, 10);
        gc.gridx = 1; banner.add(textStack, gc);
        root.add(banner, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setBackground(GameGUI.BG_DARK);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(24, 36, 20, 36));

        JLabel prompt = new JLabel("Enter your farmer name:");
        prompt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        prompt.setForeground(GameGUI.TEXT_MAIN);
        prompt.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameField = new JTextField(18);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        nameField.setBackground(GameGUI.BG_PANEL);
        nameField.setForeground(GameGUI.TEXT_MAIN);
        nameField.setCaretColor(GameGUI.ACCENT_GREEN);
        nameField.setBorder(new CompoundBorder(
                new LineBorder(GameGUI.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel info = new JLabel("<html><span style='color:#8fa88f'>Season: 20 days &nbsp;|&nbsp; "
                + "Starting savings: 1000g &nbsp;|&nbsp; Daily income: +50g</span></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(prompt);
        body.add(Box.createVerticalStrut(8));
        body.add(nameField);
        body.add(Box.createVerticalStrut(16));
        body.add(info);

        root.add(body, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnRow.setBackground(GameGUI.BG_DARK);
        btnRow.setBorder(new MatteBorder(1, 0, 0, 0, GameGUI.BORDER_COLOR));

        JButton quit = new JButton("Quit");
        styleBtn(quit, GameGUI.TEXT_DIM, false);
        quit.addActionListener(e -> { playerName = null; dispose(); });

        JButton start = new JButton("Start Game");
        styleBtn(start, GameGUI.ACCENT_GREEN, true);
        start.addActionListener(e -> onStart());

        btnRow.add(quit);
        btnRow.add(start);
        root.add(btnRow, BorderLayout.SOUTH);

        // Enter key triggers start
        nameField.addActionListener(e -> onStart());
        getRootPane().setDefaultButton(start);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    private void onStart() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            nameField.setBorder(new CompoundBorder(
                    new LineBorder(GameGUI.ACCENT_RED, 1, true),
                    new EmptyBorder(6, 8, 6, 8)));
            nameField.requestFocus();
            return;
        }
        playerName = name;
        dispose();
    }

    private void styleBtn(JButton btn, Color fg, boolean filled) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(filled ? Color.WHITE : fg);
        btn.setBackground(filled ? new Color(40, 110, 40) : GameGUI.BG_PANEL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setBorder(new CompoundBorder(
                new LineBorder(fg.darker(), 1, true),
                new EmptyBorder(6, 16, 6, 16)));
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public String getPlayerName() {
        return playerName;
    }
}
