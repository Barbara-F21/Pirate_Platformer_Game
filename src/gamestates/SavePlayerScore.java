package gamestates;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class SavePlayerScore extends JFrame {

    private JTextField playerNameField;
    private JTextField scoreField;

    public SavePlayerScore() {
        setTitle("Játékos mentése");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel nameLabel = new JLabel("Játékos neve:");
        nameLabel.setBounds(20, 20, 100, 30);
        add(nameLabel);

        playerNameField = new JTextField();
        playerNameField.setBounds(120, 20, 150, 30);
        add(playerNameField);

        JLabel scoreLabel = new JLabel("Pontszám:");
        scoreLabel.setBounds(20, 60, 100, 30);
        add(scoreLabel);

        scoreField = new JTextField();
        scoreField.setBounds(120, 60, 150, 30);
        add(scoreField);

        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Fájl");

        JMenuItem saveItem = new JMenuItem("Mentés");
        saveItem.addActionListener(e -> savePlayerScore()); // Mentési funkció meghívása
        fileMenu.add(saveItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar); // Menü sáv hozzáadása az ablakhoz
    }

    private void savePlayerScore() {
        String playerName = playerNameField.getText();
        String scoreText = scoreField.getText();

        if (playerName.isEmpty() || scoreText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A név és a pontszám kitöltése kötelező!", "Figyelmeztetés", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Játékos neve: " + playerName);
                writer.println("Pontszám: " + scoreText);
                JOptionPane.showMessageDialog(this, "Sikeresen elmentve: " + file.getAbsolutePath(), "Siker", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Hiba történt a mentés során.", "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
