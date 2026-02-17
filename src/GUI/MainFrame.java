package GUI;

import GameState.Game;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Numele ecranelor
    public static final String LOGIN_VIEW = "LOGIN";
    public static final String MAIN_MENU_VIEW = "MAIN_MENU";
    public static final String GAME_VIEW = "GAME";
    public static final String SAVED_GAMES_VIEW = "SAVED_GAMES";

    // Pastrez referinte la panel-uri pentru a le putea da refresh
    private MainMenuPanel mainMenuPanel;
    private SavedGamesPanel savedGamesPanel;

    public MainFrame() {
        setTitle("Chess-Swing-App");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Folosesc card layout ca sa stack-ez paginile meniului
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        LoginPanel loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, LOGIN_VIEW);

        mainMenuPanel = new MainMenuPanel(this);
        mainPanel.add(mainMenuPanel, MAIN_MENU_VIEW);

        savedGamesPanel = new SavedGamesPanel(this);
        mainPanel.add(savedGamesPanel, SAVED_GAMES_VIEW);

        add(mainPanel);
        // Prima data apare meniul de login
        showLogin();
        setVisible(true);
    }

    public void showLogin() {
        cardLayout.show(mainPanel, LOGIN_VIEW);
    }

    public void showMainMenu() {
        // Cand intru in meniu, actualizez punctajul si numarul de jocuri afisate
        mainMenuPanel.refreshStats();
        cardLayout.show(mainPanel, MAIN_MENU_VIEW);
    }

    public void showGame(Game game) {
        // Pentru fiecare joc regenerez un nou panou de joc
        GamePanel gamePanel = new GamePanel(this, game);

        // Adaug panel-ul in layout (sau este inlocuit cel vechi cu acelasi nume)
        mainPanel.add(gamePanel, GAME_VIEW);
        cardLayout.show(mainPanel, GAME_VIEW);
    }

    public void showSavedGames() {
        // Ca in cazul meniului principal, mai intai actualizez datele afisate
        savedGamesPanel.refreshGamesList();
        cardLayout.show(mainPanel, SAVED_GAMES_VIEW);
    }
}