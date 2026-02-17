package GameState;

import Exceptions.InvalidCommandException;
import Exceptions.InvalidMoveException;
import GameObjects.Colors;
import GameObjects.Piece;
import GameObjects.Strategies.ScoreStrategy;
import GameObjects.Strategies.StandardScoreStrategy;
import MainManagement.User;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Player {
    private String playerName, playerEmail;
    public Colors playerColor;
    private List<Piece> capturedPieces;
    private TreeSet<ChessPair<Position, Piece>> ownedPieces;
    private int playerPoints;
    private ScoreStrategy scoreStrategy;

    public Player(String email, String color) throws InvalidCommandException {
        playerEmail = email;
        setColor(color);
        capturedPieces = new ArrayList<>();
        ownedPieces = new TreeSet<>();
        // Cand numele nu e dat la instantierea player-ului, il extrag din email
        // Daca emailul contine '@', atunci player-ul e user-ul
        int idxAtSymbol = email.indexOf('@');
        if (idxAtSymbol != -1) {
            playerName = email.substring(0, idxAtSymbol);
        }
        // Altfel player-ul e computerul
        else {
            playerName = "computer";
        }
        scoreStrategy = new StandardScoreStrategy();
    }

    public Player(String email, String color, String name) throws InvalidCommandException {
        this(email, color);
        playerName = name;
    }

    public void makeMove(Position from, Position to, Board board, String promotionType) throws InvalidMoveException {
        Piece piece = board.getPieceAt(from);
        // Inainte sa apelez movePiece din board, verific daca piesa apartine player-ului
        // Board verifica doar corectitudinea la nivel de mutare, nu si de apartenenta
        if (piece == null) {
            throw new InvalidMoveException("There is no piece at that position.");
        }
        if (piece.getColor() != playerColor) {
            throw new InvalidMoveException("You can move pieces only of your color.");
        }
        // Acum verific validitatea la nivel de tabla
        if (board.isValidMove(from, to)) {
            Piece capturedPiece = board.getPieceAt(to);
            board.movePiece(from, to, promotionType);
            // Daca s-a facut o captura, se adauga in lista jucatorului de piese capturate si se actualizeaza punctajul
            if (capturedPiece != null) {
                capturedPieces.add(capturedPiece);
                System.out.println("Captured piece: " + capturedPiece.type());
                // Punctaj pt captura
                int value = scoreStrategy.getScore(capturedPiece);
                playerPoints += value;
                System.out.println("Points gained: " + value + "\nTotal current points: " + playerPoints);
            }
        }
        else {
            throw new InvalidMoveException("Invalid move.");
        }
    }

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public TreeSet<ChessPair<Position, Piece>> getOwnedPieces() {
        return ownedPieces;
    }

    public void setOwnedPieces(Board board) {
        ownedPieces.clear();

        for (ChessPair<Position, Piece> pair : board.getPiecesPositions()) {
            if (pair.getValue().getColor() == this.playerColor) {
                ownedPieces.add(pair);
            }
        }
    }

    public int getPoints() {
        return playerPoints;
    }

    public void setPoints(int points) {
        playerPoints = points;
    }

    public String getName() {
        return playerName;
    }

    public void setName(String name) {
        playerName = name;
    }

    public Colors getColor() {
        return playerColor;
    }

    public void setColor(String color) throws InvalidCommandException {
        if (color.toLowerCase().equals("w") || color.toLowerCase().equals("white")) {
            playerColor = Colors.WHITE;
        }
        else if (color.toLowerCase().equals("b") || color.toLowerCase().equals("black")) {
            playerColor = Colors.BLACK;
        }
        else {
            throw new InvalidCommandException("Invalid command.\nUsage: <w/white> for white or <b/black> for black.");
        }
    }

    public String getColorToString() {
        if (playerColor == Colors.WHITE) {
            return "WHITE";
        }
        else {
            return "BLACK";
        }
    }

    public String getEmail() {
        return playerEmail;
    }

    public void setEmail(User currentUser) {
        if (currentUser != null)
            playerEmail = currentUser.getEmail();
    }

    public void recalculatePoints() {
        this.playerPoints = 0;
        this.scoreStrategy = new GameObjects.Strategies.StandardScoreStrategy();

        for (Piece p : capturedPieces) {
            this.playerPoints += scoreStrategy.getScore(p);
        }
    }

    public void addGameEndPoints(GameEndReason reason) {
        this.playerPoints += scoreStrategy.getGameEndScore(reason);
    }

    public int getGameEndScore(GameEndReason reason) {
        return scoreStrategy.getGameEndScore(reason);
    }
}
