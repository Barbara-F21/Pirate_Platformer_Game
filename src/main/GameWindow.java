package main;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.PrintWriter;

public class GameWindow {

    private JFrame jframe;

    public GameWindow(GamePanel gamePanel) {
        jframe = new JFrame();

        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.add(gamePanel);

        jframe.setResizable(false);
        createMenuBar();
        jframe.pack(); // Igazítsa mérethez az ablakot
        jframe.setLocationRelativeTo(null); // Azért itt, hogy az ablak középen legyen
        jframe.setVisible(true);

        jframe.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                System.out.println("Gained");
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                gamePanel.getGame().windowFocusLost();
            }
        });

        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Help");

        JMenuItem saveItem = new JMenuItem("Save Score");
        saveItem.addActionListener(e -> savePlayerScore()); // A mentési funkció meghívása
        fileMenu.add(saveItem);

        JMenuItem howToPlayItem = new JMenuItem("How to play?");
        howToPlayItem.addActionListener(e -> showHowToPlay());
        fileMenu.add(howToPlayItem);

        menuBar.add(fileMenu);

        jframe.setJMenuBar(menuBar);
    }

    private void showHowToPlay() {
        try {
            File file = new File("./HowToPlay.txt");

            if (!file.exists()) {
                return;
            }

            java.util.List<String> lines = java.nio.file.Files.readAllLines(file.toPath());// Beolvassuk a fájl tartalmát

            JTextArea textArea = new JTextArea(20, 50);
            for (String line : lines) {
                textArea.append(line + "\n");
            }
            textArea.setEditable(false); // Nem szerkesztheto

            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(jframe, scrollPane, "How To Play?", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(jframe, "Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Játékos neve és pontszáma mentése
    private void savePlayerScore() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(jframe) == JFileChooser.APPROVE_OPTION) {
            try {
                String playerName = JOptionPane.showInputDialog(jframe, "Name:");
                int score = (int) (Math.random() * 100); // Példa pontszám
                if (playerName != null && !playerName.trim().isEmpty()) {
                    File file = fileChooser.getSelectedFile();
                    try (PrintWriter writer = new PrintWriter(file)) {
                        writer.println("Username: " + playerName);
                        writer.println("Score: " + score);
                    }
                    JOptionPane.showMessageDialog(jframe, "Progress saved!");
                } else {
                    JOptionPane.showMessageDialog(jframe, "Type in your username, please!", "Error!", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(jframe, "Error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
