package GUI;

import MainManagement.Main;
import MainManagement.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

public class MainMenuPanel extends JPanel {
    private MainFrame parentFrame;
    private JLabel welcomeLabel;
    private JLabel pointsLabel;
    private JLabel gamesCountLabel;
    private Image backgroundImage;

    public MainMenuPanel(MainFrame frame) {
        this.parentFrame = frame;

        // Box layout pe verticala
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(30, 20, 20, 20));
        // Imaginea de fundal (facuta cu Gemini)
        try {
            URL imgUrl = getClass().getResource("/mainMenuBkg.jpg");
            if (imgUrl != null) {
                backgroundImage = ImageIO.read(imgUrl);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(welcomeLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));

        // Panoul cu informatii (puncte si jocuri active)
        // Folosesc GridLayout aici pentru doua cutii egale una langa alta
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setOpaque(false);
        // Il fortez sa nu fie cat tot ecranul
        statsPanel.setMaximumSize(new Dimension(360, 60));
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        pointsLabel = new JLabel("0");
        gamesCountLabel = new JLabel("0");
        JPanel pointsCard = createStatCard("Total Points", pointsLabel);
        JPanel gamesCountCard = createStatCard("Active Games", gamesCountLabel);

        statsPanel.add(pointsCard);
        statsPanel.add(gamesCountCard);

        add(statsPanel);
        add(Box.createRigidArea(new Dimension(0, 30)));

        // Butoane
        // Buton new game
        JButton newGameBtn = createMenuButton("New Game", new Color(60, 180, 115));
        newGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNewGameDialog();
            }
        });

        add(newGameBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Buton continue game
        JButton continueGameBtn = createMenuButton("Continue Game", new Color(70, 130, 180));
        continueGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showSavedGames();
            }
        });

        add(continueGameBtn);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Buton logout
        JButton logoutBtn = createMenuButton("Log Out", new Color(220, 50, 70));
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showLogin();
            }
        });

        add(logoutBtn);
    }

    public void refreshStats() {
        User user = Main.getInstance().getCurrentUser();

        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getEmail() + "!");
            pointsLabel.setText(String.valueOf(user.getPoints()));
            int activeGames = 0;
            if (user.getGameIds() != null) {
                activeGames = user.getGameIds().size();
            }
            gamesCountLabel.setText(String.valueOf(activeGames));
        }
    }

    private JButton createMenuButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(300, 50));
        btn.setPreferredSize(new Dimension(300, 50));

        return btn;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel();
        // Titlul sus, Valoare jos (layout vertical)
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(40, 40, 40, 200));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        // Eticheta titlu
        JLabel titleLbl = new JLabel(title);
        titleLbl.setForeground(Color.CYAN);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 14));
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Eticheta valoare
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Asamblare cu spatiu intre ele
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(titleLbl);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(valueLabel);

        return card;
    }

    private void showNewGameDialog() {
        JTextField aliasField = new JTextField();
        String[] colors = {"WHITE", "BLACK"};
        JComboBox<String> colorCombo = new JComboBox<>(colors);

        Object[] message = {
                "Alias:", aliasField,
                "Color:", colorCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "New Game Setup", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String alias = aliasField.getText();
            if (alias.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You must choose an alias for the game.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String color = (String) colorCombo.getSelectedItem();

            // Apelez backend-ul pentru a crea jocul
            Main.getInstance().startNewGameGUI(alias, color);
            // Navighez catre ecranul de joc
            parentFrame.showGame(Main.getInstance().getCurrentGame());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}