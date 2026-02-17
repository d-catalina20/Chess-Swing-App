package GUI;

import Exceptions.InvalidMoveException;
import GameObjects.Colors;
import GameObjects.Piece;
import GameState.Game;
import GameState.GameObserver;
import GameState.Move;
import GameState.Player;
import GameState.Position;
import MainManagement.Main;
import MainManagement.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePanel extends JPanel implements GameObserver {
    private MainFrame parentFrame;
    private Game game;
    private JButton[][] squares = new JButton[8][8];
    private JPanel boardPanel; // Tabla 8x8
    private Map<String, ImageIcon> pieceIcons = new HashMap<>(); // Mapa pt imagini piese

    // Etichete status
    private JLabel statusLabel;
    private JLabel checkStatusLabel;
    private JTextArea moveHistoryArea;

    // Statistici Jucatori
    private JLabel player1ScoreLabel;
    private JPanel player1CapturesPanel;
    private JLabel player2ScoreLabel;
    private JPanel player2CapturesPanel;

    // Starea interactiunii
    private Position sourcePosition = null;
    private JButton sourceButton = null;

    public GamePanel(MainFrame frame, Game game) {
        this.parentFrame = frame;
        this.game = game;
        // Acest panou va fi observer pt actiunile din back-end
        game.addObserver(this);

        // Incarc imaginile pieselor
        loadPieceImages();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Avertisment sah in nord
        checkStatusLabel = new JLabel("");
        checkStatusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        checkStatusLabel.setForeground(Color.RED);
        checkStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(checkStatusLabel, BorderLayout.NORTH);

        // Tabla de sah cu coordonate in centru
        boardPanel = new JPanel(new GridLayout(8, 8));
        initializeBoardButtons();
        boardPanel.setBorder(BorderFactory.createLineBorder(new Color(50, 70, 35), 5));
        // Cream containerul care include numerele si literele
        JPanel boardWithLabels = createBoardWithLabels();
        add(boardWithLabels, BorderLayout.CENTER);

        // Info si controale in est
        JPanel sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.EAST);

        // Randez starea initiala
        refreshBoard();
        updateStats();
        populateMoveHistory();

        // Daca userul si-a ales piesele negre, computerul incepe
        if (!game.isHumanTurn()) {
            triggerComputerMove();
        }
    }

    private JPanel createBoardWithLabels() {
        JPanel container = new JPanel(new BorderLayout());

        // Verific perspectiva
        boolean isUserWhite = game.getHumanPlayer().getColor() == Colors.WHITE;

        // Panoul pentru numere
        JPanel numPanel = new JPanel(new GridLayout(8, 1));
        numPanel.setPreferredSize(new Dimension(30, 0));

        for (int i = 0; i < 8; i++) {
            // Alb: 8..1, Negru: 1..8
            String num;
            if (isUserWhite == true) {
                num = String.valueOf(8 - i);
            }
            else {
                num = String.valueOf(i + 1);
            }
            JLabel label = new JLabel(num, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            numPanel.add(label);
        }

        // Panoul pt litere (si alinierea cu numerele)
        JPanel lettersPanel = new JPanel(new BorderLayout());

        // Patratel gol in coltul stanga-jos (pentru aliniere cu numerele)
        JPanel cornerPlaceholder = new JPanel();
        cornerPlaceholder.setPreferredSize(new Dimension(30, 30));
        lettersPanel.add(cornerPlaceholder, BorderLayout.WEST);

        // Panoul pt litere
        JPanel lettersPanel1 = new JPanel(new GridLayout(1, 8));
        lettersPanel1.setPreferredSize(new Dimension(0, 30));

        for (int i = 0; i < 8; i++) {
            // Alb: A..H, Negru: H..A
            char letter;
            if (isUserWhite == true) {
                letter = (char)('A' + i);
            }
            else {
                letter = (char)('H' - i);
            }
            JLabel label = new JLabel(String.valueOf(letter), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            lettersPanel1.add(label);
        }
        lettersPanel.add(lettersPanel1, BorderLayout.CENTER);

        // Asamblarea
        container.add(numPanel, BorderLayout.WEST);
        container.add(boardPanel, BorderLayout.CENTER);
        container.add(lettersPanel, BorderLayout.SOUTH);

        return container;
    }

    private void initializeBoardButtons() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int visualRow = i;
                int visualCol = j;

                JButton btn = new JButton();
                btn.setFocusPainted(false);
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleSquareClick(visualRow, visualCol, btn);
                    }
                });

                // Colorare tabla
                if ((i + j) % 2 == 0) btn.setBackground(new Color(230,230,210));
                else btn.setBackground(new Color(110, 160, 50));

                squares[i][j] = btn;
                boardPanel.add(btn);
            }
        }
    }

    private JPanel createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Status Tura
        statusLabel = new JLabel("Turn: " + game.getCurrentPlayerColor());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Status Player 1
        Player p1 = game.getPlayers().get(0);
        panel.add(createPlayerStatsPanel(p1, true));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Status Player 2
        Player p2 = game.getPlayers().get(1);
        panel.add(createPlayerStatsPanel(p2, false));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Istoric Mutari
        JLabel historyLabel = new JLabel("Move History");
        historyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(historyLabel);

        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        // Pun intr-un scrollPane istoricul
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setPreferredSize(new Dimension(280, 150));
        panel.add(scrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Butoane
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        btnPanel.setMaximumSize(new Dimension(300, 40));

        JButton saveBtn = new JButton("Save & Exit");
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.getInstance().write();
                game.removeObserver(GamePanel.this);
                parentFrame.showMainMenu();
            }
        });

        JButton resignBtn = new JButton("Resign");
        resignBtn.setBackground(new Color(220, 50, 70));
        resignBtn.setForeground(Color.WHITE);
        resignBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!game.isActive()) {
                    return;
                }
                // Pun un prompt secundar ca sa ma asigur ca userul n-a apasat din greseala
                int confirm = JOptionPane.showConfirmDialog(
                        GamePanel.this,
                        "Are you sure you want to resign?",
                        "Confirm Resignation",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    game.resignGame();
                }
            }
        });

        btnPanel.add(saveBtn);
        btnPanel.add(resignBtn);
        panel.add(btnPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Pun separat sub butonul de intoarcere la meniu
        JButton backToMenuBtn = new JButton("Back to main menu");
        backToMenuBtn.setBackground(Color.BLUE);
        backToMenuBtn.setForeground(Color.WHITE);
        backToMenuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToMenuBtn.setMaximumSize(new Dimension(300, 40));
        backToMenuBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Salvez temporar credentialele userului curent
                    User current = Main.getInstance().getCurrentUser();
                    String email = current.getEmail();
                    String pass = current.getPassword();

                    // Reincarc datele din fisierul JSON (resetez datele curente la cele de pe disk)
                    Main.getInstance().read();
                    // Reloghez utilizatorul automat (pentru că read() a recreat obiectele User)
                    Main.getInstance().login(email, pass);

                    // Afisez meniul si scot observerul
                    game.removeObserver(GamePanel.this);
                    parentFrame.showMainMenu();
                } catch (Exception ex) {
                    // In caz de eroare la citire, merg oricum la meniu
                    System.out.println(ex.getMessage());
                    parentFrame.showMainMenu();
                }
            }
        });

        panel.add(backToMenuBtn);
        return panel;
    }

    private JPanel createPlayerStatsPanel(Player p, boolean isPlayer1) {
        JPanel pPanel = new JPanel();
        pPanel.setLayout(new BorderLayout());

        // Fac o bordura speciala ca sa apara pe ea numele jucatorului si culoarea
        pPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                p.getName() + " (" + p.getColor() + ")",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12)));

        pPanel.setMaximumSize(new Dimension(300, 90));
        pPanel.setBackground(Color.WHITE);

        // Label cu informatii despre scorul curent
        JLabel scoreLbl = new JLabel("Points: " + p.getPoints());
        scoreLbl.setBorder(new EmptyBorder(5, 5, 5, 5));
        pPanel.add(scoreLbl, BorderLayout.NORTH);

        // Pun in interiorul panoului, un alt panou cu piesele capturate
        JPanel capturesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        capturesPanel.setBackground(Color.WHITE);
        capturesPanel.setPreferredSize(new Dimension(250, 40));
        // Pun in interiorul unui scrollPane orizontal
        JScrollPane scrollPane = new JScrollPane(capturesPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(260, 50));

        pPanel.add(scrollPane, BorderLayout.CENTER);

        // Afisez sus panoul player-ului care joaca cu albele si sub celui care joaca cu negrele
        if (isPlayer1) {
            player1ScoreLabel = scoreLbl;
            player1CapturesPanel = capturesPanel;
        } else {
            player2ScoreLabel = scoreLbl;
            player2CapturesPanel = capturesPanel;
        }

        return pPanel;
    }

    private void updateStats() {
        Player p1 = game.getPlayers().get(0);
        Player p2 = game.getPlayers().get(1);

        player1ScoreLabel.setText("Points: " + p1.getPoints());
        player2ScoreLabel.setText("Points: " + p2.getPoints());

        refreshCapturesPanel(player1CapturesPanel, p1.getCapturedPieces());
        refreshCapturesPanel(player2CapturesPanel, p2.getCapturedPieces());

        updateCheckStatus();
    }

    public void updateCheckStatus() {
        checkStatusLabel.setText(" ");

        if (game.getBoard().isKingInCheck(game.getCurrentPlayerColor())) {
            if (game.isHumanTurn()) {
                // Folosesc invokeLater si aici pentru ca piesa sa apuce sa apara mutata
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(GamePanel.this, "You are in CHECK!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                });
                checkStatusLabel.setText("CHECK! Your King is under attack!");
            } else {
                checkStatusLabel.setText("CHECK! Computer's King is under attack!");
            }
        }
    }

    private void refreshCapturesPanel(JPanel panel, List<Piece> pieces) {
        // Golesc capturile din panou
        panel.removeAll();
        // Si le readaug, incluzand noua captura
        for (Piece captured : pieces) {
            ImageIcon originalIcon = (ImageIcon) getPieceIcon(captured);

            if (originalIcon != null) {
                // micsorez icon-urile pentru panoul lateral
                Image img = originalIcon.getImage();
                Image smallImg = img.getScaledInstance(35, 35, Image.SCALE_SMOOTH);

                JLabel pLabel = new JLabel(new ImageIcon(smallImg));
                panel.add(pLabel);
            }
        }

        int itemWidth = 40;
        int totalItems = pieces.size();
        int requiredWidth = totalItems * itemWidth + 20;

        if (requiredWidth < 250) requiredWidth = 250;
        panel.setPreferredSize(new Dimension(requiredWidth, 40));

        panel.repaint();
    }

    // Am inlocuit unicode-ul cu imaginii desenate de mine
    /*
    private String getUnicodePiece(Piece piece) {
        if (piece.getColor() == Colors.WHITE) {
            switch (piece.type()) {
                case 'K': return "\u2654";
                case 'Q': return "\u2655";
                case 'R': return "\u2656";
                case 'B': return "\u2657";
                case 'N': return "\u2658";
                case 'P': return "\u2659";
            }
        } else {
            switch (piece.type()) {
                case 'K': return "\u265A";
                case 'Q': return "\u265B";
                case 'R': return "\u265C";
                case 'B': return "\u265D";
                case 'N': return "\u265E";
                case 'P': return "\u265F";
            }
        }
        return "";
    }
    */

    private Icon getPieceIcon(Piece piece) {
        if (piece == null) return null;

        // Construiesc cheia pe baza piesei (ex: "WHITE" + "_" + "K")
        String color;
        if (piece.getColor() == Colors.WHITE) {
            color = "WHITE";
        }
        else {
            color = "BLACK";
        }
        String type = String.valueOf(piece.type());
        String key = color + "_" + type;

        return pieceIcons.get(key);
    }

    // Metoda care ia din lista de mutari posibile, doar pe cele valide
    private List<Position> getSafeMoves(Piece piece) {
        List<Position> safeMoves = new ArrayList<>();
        List<Position> possibleMoves = piece.getPossibleMoves(game.getBoard());
        Position currentPos = piece.getPosition();

        for (Position targetPos : possibleMoves) {
            if (game.getBoard().isValidMove(currentPos, targetPos)) {
                safeMoves.add(targetPos);
            }
        }
        return safeMoves;
    }

    private void highlightPossibleMoves(Piece piece) {
        // Evidentiez doar mutarile valide
        List<Position> moves = getSafeMoves(piece);
        boolean isUserWhite = game.getHumanPlayer().getColor() == Colors.WHITE;

        for (Position pos : moves) {
            int col = pos.getX() - 'A';
            int row;
            if (isUserWhite == true) {
                row = 8 - pos.getY();
            } else {
                row = pos.getY() - 1;
            }

            if (game.getBoard().getPieceAt(pos) != null) {
                squares[row][col].setBackground(Color.RED); // Rosu pt captura
            } else {
                squares[row][col].setBackground(Color.GREEN); // Verde pt mutare
            }
        }
    }

    private void handleSquareClick(int visualRow, int visualCol, JButton clickedButton) {
        if (!game.isActive()) return;
        if (!game.isHumanTurn()) return;

        Position clickedPos = getLogicalPosition(visualRow, visualCol);
        // Daca nimic nu era selectat, marchez selectia facuta
        if (sourcePosition == null) {
            Piece piece = game.getBoard().getPieceAt(clickedPos);
            if (piece != null && piece.getColor() == game.getCurrentPlayerColor()) {
                sourcePosition = clickedPos;
                sourceButton = clickedButton;
                sourceButton.setBackground(Color.YELLOW);
                highlightPossibleMoves(piece);
            }
        }
        else {
            // Daca userul a dat click tot pe piesa selectata, o deselectez
            if (clickedPos.equals(sourcePosition)) {
                resetBoardColors();
                sourcePosition = null;
                return;
            }

            // Verific daca userul a dat click pe o alta piesa de-a lui
            Piece clickedPiece = game.getBoard().getPieceAt(clickedPos);
            if (clickedPiece != null && clickedPiece.getColor() == game.getCurrentPlayerColor()) {
                // Schimb selectia pe noua piesa
                sourcePosition = clickedPos;
                sourceButton = clickedButton;

                // Resetez culorile si evidentiez noua selectie
                resetBoardColors();
                sourceButton.setBackground(Color.YELLOW);
                highlightPossibleMoves(clickedPiece);
                return; // Ies din metoda, nu incerc sa mut piesa
            }

            // Incerc executarea mutarii
            try {
                Piece sourcePiece = game.getBoard().getPieceAt(sourcePosition);
                List<Position> validMoves = getSafeMoves(sourcePiece);

                if (!validMoves.contains(clickedPos)) {
                    throw new InvalidMoveException("Invalid move");
                }

                String promotionType = null;
                if (sourcePiece instanceof GameObjects.Pawn) {
                    int targetRank = clickedPos.getY();
                    if ((sourcePiece.getColor() == Colors.WHITE && targetRank == 8) ||
                            (sourcePiece.getColor() == Colors.BLACK && targetRank == 1)) {

                        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                        int choice = JOptionPane.showOptionDialog(this, "Promote Pawn to:", "Promotion",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                        if (choice == -1) {
                            resetBoardColors();
                            sourcePosition = null;
                            return;
                        }

                        String selected = options[choice];
                        if (selected.equals("Knight")) promotionType = "N";
                        else promotionType = selected.substring(0, 1);
                    }
                }

                game.addMove(game.getCurrentPlayer(), sourcePosition, clickedPos, promotionType);
                sourcePosition = null;
                resetBoardColors();

            } catch (InvalidMoveException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Invalid Move", JOptionPane.WARNING_MESSAGE);
                resetBoardColors();
                sourcePosition = null;
            }
        }
    }

    private Position getLogicalPosition(int row, int col) {
        boolean isUserWhite = game.getHumanPlayer().getColor() == Colors.WHITE;
        char posCol = (char) ('A' + col);
        int posRow;
        if (isUserWhite == true) {
            posRow = (8 - row);
        }
        else {
          posRow = (row + 1);
        }
        return new Position(posCol, posRow);
    }

    private void populateMoveHistory() {
        if (game.getMoves() != null) {
            int moveCount = 1;

            for (Move move : game.getMoves()) {
                moveHistoryArea.append(formatMoveLog(move, moveCount));
                moveCount++;
            }

            // Pun cursorul la capatul istoricului
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        moveHistoryArea.setCaretPosition(moveHistoryArea.getDocument().getLength());
                    } catch (Exception e) {
                    }
                }
            });
        }
    }

    public static String formatMoveLog(Move move, int moveNumber) {
        // Aleg simbolul: ○ (White), ● (Black)
        String bullet;
        if (move.getPlayerColor() == Colors.WHITE) {
            bullet = "\u25CB";
        }
        else {
            bullet = "\u25CF";
        }

        // Text pentru captura (daca exista)
        String captureText = "";
        if (move.getCapturedPiece() != null) {
            captureText = " (captured " + move.getCapturedPiece().type() + ")";
        }

        return String.format("%d. %s %s -> %s%s\n",
                moveNumber,
                bullet,
                move.getFromPosition(),
                move.getToPosition(),
                captureText);
    }

    private void refreshBoard() {
        // Repun piesele pe tabla la noile pozitii
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position pos = getLogicalPosition(i, j);
                Piece piece = game.getBoard().getPieceAt(pos);

                squares[i][j].setText("");
                squares[i][j].setIcon(null);
                squares[i][j].setDisabledIcon(null);
                if (piece != null) {
                    squares[i][j].setIcon(getPieceIcon(piece));
                    squares[i][j].setDisabledIcon(getPieceIcon(piece));
                }
            }
        }
    }

    private void resetBoardColors() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) squares[i][j].setBackground(new Color(230,230,210));
                else squares[i][j].setBackground(new Color(110, 160, 50));
            }
        }
    }

    private void triggerComputerMove() {
        // Dupa un delay scurt, computerul face mutarea
        Timer computerTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (game.isActive() && !game.isHumanTurn()) {
                    game.makeRandomMove();
                }
                // O masura de sigurata pentru oprirea cronometrului
                ((Timer)e.getSource()).stop();
            }
        });
        computerTimer.setRepeats(false);
        computerTimer.start();
    }

    @Override
    public void onPlayerSwitch(Player currentPlayer) {
        statusLabel.setText("Turn: " + currentPlayer.getColor());
    }

    @Override
    public void onMoveMade(Move move) {
        // Daca panoul nu mai e afisat (ex. ->back to main menu)
        if (!this.isDisplayable()) {
            game.removeObserver(this);
            return;
        }

        refreshBoard();
        updateStats();

        // Adaug in istoric
        int moveIndex = game.getMoves().size();
        moveHistoryArea.append(formatMoveLog(move, moveIndex));

        if (game.isFinished()) {
            return;
        }

        if (!game.isHumanTurn() && game.isActive()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    triggerComputerMove();
                }
            });
        }
    }

    @Override
    public void onGameFinished(String message) {
        refreshBoard();
        updateStats();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
                // Calculez punctele si le salvez
                Main mainApp = Main.getInstance();
                User currentUser = mainApp.getCurrentUser();
                int sessionPoints = game.getHumanPlayer().getPoints();
                int newTotal = Math.max(0, currentUser.getPoints() + sessionPoints);

                currentUser.setPoints(newTotal);
                mainApp.deleteGame(game);
                mainApp.write();

                String title;
                if (message.toLowerCase().contains("draw") || message.toLowerCase().contains("stalemate")) {
                    title = "Draw";
                } else if (message.contains(game.getHumanPlayer().getColor().toString()) && message.contains("wins")) {
                    title = "VICTORY!";
                } else if (message.contains("Resign")) {
                    title = "DEFEAT (Resigned)";
                } else {
                    if (game.getBoard().isCheckMate(game.getHumanPlayer().getColor())) {
                        title = "DEFEAT";
                    } else {
                        title = "VICTORY!";
                    }
                }

                GameResultDialog resultDialog = new GameResultDialog(
                        parentFrame,
                        title,
                        message,
                        sessionPoints,
                        newTotal
                );

                resultDialog.setVisible(true);
            }
        });
    }

    private void loadPieceImages() {
        String[] colors = {"WHITE", "BLACK"};
        String[] types = {"K", "Q", "R", "B", "N", "P"};

        for (String color : colors) {
            for (String type : types) {
                String fileName = "/pieces/" + color + "_" + type + ".png";
                try {
                    // Incarc imaginea
                    URL imgUrl = getClass().getResource(fileName);
                    if (imgUrl != null) {
                        Image img = ImageIO.read(imgUrl);
                        // Redimensionez imaginea ca sa incapa frumos in buton
                        Image scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                        pieceIcons.put(color + "_" + type, new ImageIcon(scaledImg));
                    } else {
                        System.err.println("Imagine lipsă: " + fileName);
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}