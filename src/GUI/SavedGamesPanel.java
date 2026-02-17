package GUI;

import MainManagement.Main;
import MainManagement.User;
import GameState.Game;
import GameState.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SavedGamesPanel extends JPanel {
    private MainFrame parentFrame;
    private JPanel gamesContainer; // Aici voi pune lista de jocuri

    public SavedGamesPanel(MainFrame frame) {
        this.parentFrame = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 50));

        // Titlul in nord
        JLabel titleLabel = new JLabel("Your Active Games");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        gamesContainer = new JPanel();
        gamesContainer.setLayout(new BoxLayout(gamesContainer, BoxLayout.Y_AXIS));
        gamesContainer.setBackground(new Color(60, 60, 65));

        // Pun containerul intr-un ScrollPane
        JScrollPane scrollPane = new JScrollPane(gamesContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Panou pentru butonul de revenire la meniu in sud
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(45, 45, 50));
        footerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JButton backBtn = new JButton("Back to Menu");
        backBtn.setPreferredSize(new Dimension(200, 40));
        backBtn.setBackground(Color.BLUE);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showMainMenu();
            }
        });
        footerPanel.add(backBtn);

        add(footerPanel, BorderLayout.SOUTH);
    }


    public void refreshGamesList() {
        gamesContainer.removeAll(); // Curat lista veche
        // Iau lista noua
        User currentUser = Main.getInstance().getCurrentUser();
        List<Game> activeGames = currentUser.getActiveGames();

        gamesContainer.add(Box.createVerticalStrut(15));
        // Daca e goala, afisez un mesaj
        if (activeGames.isEmpty()) {
            JLabel noGamesLabel = new JLabel("No active games found.");
            noGamesLabel.setForeground(Color.LIGHT_GRAY);
            noGamesLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noGamesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            gamesContainer.add(noGamesLabel);
        }
        // Altfel, pentru fiecare joc, fac un panou separat
        else {
            for (Game game : activeGames) {
                JPanel card = createGameCard(game);
                card.setAlignmentX(Component.CENTER_ALIGNMENT);
                gamesContainer.add(card);
                gamesContainer.add(Box.createVerticalStrut(15));
            }
        }

        gamesContainer.repaint();
    }

    private JPanel createGameCard(Game game) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(800, 100));
        card.setPreferredSize(new Dimension(600, 100));
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Player humanPlayer = game.getHumanPlayer();

        String infoText = String.format("<html><b>Game ID: %d</b><br/>Alias: %s (%s)<br/>Points in match: %d</html>",
                game.getGameId(),
                humanPlayer.getName(),
                humanPlayer.getColor(),
                humanPlayer.getPoints()
        );

        // Label cu informatii sumare despre jocul din lista
        JLabel infoLabel = new JLabel(infoText);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        card.add(infoLabel, BorderLayout.CENTER);

        // Panou pentru butoanele cu optiuni pt joc
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        // Buton detalii
        JButton infoBtn = new JButton("Details");
        infoBtn.setBackground(new Color(108, 117, 125));
        infoBtn.setForeground(Color.WHITE);
        infoBtn.setFocusPainted(false);
        infoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GameDetailsDialog(parentFrame, game).setVisible(true);
            }
        });

        // Buton resume
        JButton resumeBtn = new JButton("Resume");
        resumeBtn.setBackground(new Color(70, 130, 180));
        resumeBtn.setForeground(Color.WHITE);
        resumeBtn.setFocusPainted(false);
        resumeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.resume();
                parentFrame.showGame(game);
            }
        });

        // Buton stergere
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(220, 53, 69));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(SavedGamesPanel.this, "Delete this game permanently?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Main.getInstance().deleteGame(game);
                    refreshGamesList();
                }
            }
        });

        btnPanel.add(infoBtn);
        btnPanel.add(resumeBtn);
        btnPanel.add(deleteBtn);

        card.add(btnPanel, BorderLayout.EAST);

        return card;
    }
}