package GameState;

import Exceptions.InvalidCommandException;
import Exceptions.InvalidMoveException;
import GameObjects.Colors;
import GameObjects.Piece;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private int gameId;
    private Board gameBoard;
    private List<Player> players;
    private List<Move> moves;
    // ID = 0 pt WHITE player; ID = 1 pt BLACK player
    private int currentPlayerId;
    private Colors currentPlayerColor;
    private int humanPlayerId;
    private boolean isActive;
    private List<GameObserver> observers = new ArrayList<>();

    public Game() {
        gameBoard = new Board();
        moves = new ArrayList<>();
        players = new ArrayList<>(2);
        isActive = true;
    }

    public Game(Player player, Player computer, int gameId) {
        this.gameId = gameId;
        currentPlayerColor = Colors.WHITE;
        currentPlayerId = 0;
        moves = new ArrayList<>();
        gameBoard = new Board();
        isActive = true;

        // Ordonez jucatorii ca sa respecte indexul asociat fiecarei culori
        List<Player> players = new ArrayList<>(2);
        if (player.getColor() == Colors.WHITE) {
            players.add(player);
            players.add(computer);
            humanPlayerId = 0;
        }
        else {
            players.add(computer);
            players.add(player);
            humanPlayerId = 1;
        }
        this.players = players;
        // Setez punctajele amandurora la 0 ca sa nu mai verific separat care e computer-ul
        players.get(0).setPoints(0);
        players.get(1).setPoints(0);
        // Apoi pun punctele user-ului din cont
        players.get(humanPlayerId).setPoints(player.getPoints());
    }

    public void showPlayerStats() {
        System.out.println("_______ Players _______");

        System.out.println("Player1 name: " + players.get(0).getName());
        System.out.println("Player1 email: " + players.get(0).getEmail());
        System.out.println("Player1 color: " + players.get(0).getColor());
        System.out.println();
        System.out.println("Player2 name: " + players.get(1).getName());
        System.out.println("Player2 email: " + players.get(1).getEmail());
        System.out.println("Player2 color: " + players.get(1).getColor());
    }

    public void start() throws InvalidMoveException, InvalidCommandException {
        gameBoard.initialize();
        isActive = true;
        moves.clear();
        players.get(0).setOwnedPieces(gameBoard);
        players.get(1).setOwnedPieces(gameBoard);
    }

    public void resume() {
        this.isActive = true;

        // Recalculez cine este user-ul si cine e computerul pe baza email-ului
        if (players.get(0).getEmail().equalsIgnoreCase("computer")) {
            humanPlayerId = 1;
        } else {
            humanPlayerId = 0;
        }
        // Recalculez indexul jucatorului curent pe baza culorii salvate
        if (players.get(0).getColor() == currentPlayerColor) {
            currentPlayerId = 0;
        } else {
            currentPlayerId = 1;
        }
        // Setez lista de piese ale jucatorilor
        if (players != null && !players.isEmpty() && gameBoard != null) {
            players.get(0).setOwnedPieces(gameBoard);
            players.get(1).setOwnedPieces(gameBoard);
        }
        System.out.println("Game " + gameId + " resumed successfully.");
    }

    public void switchPlayer() {
        if (currentPlayerId == 0) {
            currentPlayerId = 1;
            currentPlayerColor = Colors.BLACK;
        }
        else {
            currentPlayerId = 0;
            currentPlayerColor = Colors.WHITE;
        }
    }

    // Am ales sa fac verificarea direct in board
    public boolean checkForCheckMate() {
        return gameBoard.isCheckMate(currentPlayerColor);
    }

    public boolean checkForStaleMate() {
        return gameBoard.isStaleMate(currentPlayerColor);
    }

    public void addMove(Player p, Position from, Position to, String promotionType) throws InvalidMoveException {
        // Iau piesa potential capturata
        Piece capturedPiece = gameBoard.getPieceAt(to);
        // Fac mutarea la nivel de player
        p.makeMove(from, to, gameBoard, promotionType);
        // Daca nu s-a aruncat nicio exceptie, se adauga in lista de mutari
        Move move = new Move(p.getColor(), from, to, capturedPiece);
        moves.add(move);
        // Actualizez listele de piese ale jucatorilor
        players.get(0).setOwnedPieces(gameBoard);
        players.get(1).setOwnedPieces(gameBoard);
        // Schimb tura
        switchPlayer();
        // Notificari
        notifyMoveMade(move);
        notifyPlayerSwitch(getCurrentPlayer());
    }

    // Overload pt mutarile computerului -> promotie automata la regina
    public void addMove(Player p, Position from, Position to) throws InvalidMoveException {
        addMove(p, from, to, "Q");
    }

    // Mutarea computerului
    public void makeRandomMove() {
        Player currentPlayer = getCurrentPlayer();
        // Verificari de siguranta, am avut un bug in care computerul muta si piesele userului
        if (!isActive) return;
        if (currentPlayer == getHumanPlayer()) {
            return;
        }
        Player computer = currentPlayer;
        List<ChessPair<Position, Piece>> listOwnedPieces = new ArrayList<>(computer.getOwnedPieces());
        List<Position> possibleMoves;
        // Amestec piesele ca sa nu mute mereu aceeasi piesa
        java.util.Collections.shuffle(listOwnedPieces);

        for (ChessPair<Position, Piece> pair : listOwnedPieces) {
            Piece piece = pair.getValue();
            Position from = pair.getKey();
            possibleMoves = piece.getPossibleMoves(gameBoard);

            // Daca piesa n-are mutari, trec peste
            if (possibleMoves.isEmpty()) {
                continue;
            }
            // Amestec si lista de mutari
            java.util.Collections.shuffle(possibleMoves);

            for (Position to : possibleMoves) {
                try {
                    addMove(computer, from, to);
                    System.out.println("Computer moved: " + piece.type() + " from " + from + " to " + to);
                    return;
                } catch (InvalidMoveException e) {
                    // Daca mutarea e invalida, trec la alta piesa
                    // Nu mai afisez mesajul, ca sa nu se faca clutter
                }
            }
        }
        // Mesaj final, cand esueaza computerul
        System.out.println("Computer has no valid moves!");
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int id) {
        gameId = id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players.add(players.get(0));
        this.players.add(players.get(1));
    }

    public Board getBoard() {
        return gameBoard;
    }

    public void setBoard(List<Piece> board) {
        gameBoard.removeAllPieces();
        for (Piece piece : board) {
            gameBoard.addPiece(piece);
        }
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves.clear();
        this.moves.addAll(moves);
    }

    public Colors getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public void setCurrentPlayerColor(String currentPlayerColor) throws InvalidCommandException {
        if (currentPlayerColor.equals("WHITE")) {
            this.currentPlayerColor = Colors.WHITE;
        }
        else {
            this.currentPlayerColor = Colors.BLACK;
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerId);
    }

    public Player getHumanPlayer() {
        if (players.get(0).getEmail().equalsIgnoreCase("computer")) {
            humanPlayerId = 1;
        } else {
            humanPlayerId = 0;
        }
        return players.get(humanPlayerId);
    }

    public boolean isHumanTurn() {
        return currentPlayerId == humanPlayerId;
    }

    public boolean checkThreeRepetitions() {
        int n = moves.size();
        // Trebuie sa fie cel putin 10 mutari in istoric
        if (n < 10) {
            return false;
        }

        // Mutarile jucatorului curent
        Move currentMove = moves.get(n - 1);
        Move currentMove1 = moves.get(n - 5);
        Move currentMove2 = moves.get(n - 9);
        // Mutarile adversarului curent
        Move previousMove = moves.get(n - 2);
        Move previousMove1 = moves.get(n - 6);
        Move previousMove2 = moves.get(n - 10);

        // Verific daca jucatorul curent a repetat mutarile
        boolean playerRepeated = currentMove.equals(currentMove1) && currentMove.equals(currentMove2);
        // Verificăm daca adversarul a repetat mutarile
        boolean opponentRepeated = previousMove.equals(previousMove1) && previousMove.equals(previousMove2);

        // Daca amandoi au repetat, e egalitate -> Computerul renunta
        return playerRepeated && opponentRepeated;
    }

    public boolean isFinished() {
        Player humanPlayer = players.get(humanPlayerId);
        // Calculez si pt winner si pt loser pt ca la momentul curent nu stiu care e user-ul si care e computerul
        Player loser = players.get(currentPlayerId);
        Player winner;
        if (currentPlayerId == 0) {
            winner = players.get(1);
        }
        else {
            winner = players.get(0);
        }

        // Daca jocul a fost oprit manual (quit/resign)
        if (!isActive) {
            return true;
        }
        // Fac verificarile de PAT, MAT si remiza prin repetitie
        if (checkForCheckMate()) {
            String msg = "CHECKMATE! " + winner.getColor() + " wins!";
            if (humanPlayer.getColor() == winner.getColor()) {
                msg += " Congratulations! You won!";
            }
            else {
                msg += " The computer won!";
            }
            System.out.println(msg);
            winner.addGameEndPoints(GameEndReason.WIN_CHECKMATE);
            loser.addGameEndPoints(GameEndReason.LOSE_CHECKMATE);
            if (humanPlayer.getColor() == winner.getColor()) {
                System.out.println("Gained " + humanPlayer.getGameEndScore(GameEndReason.WIN_CHECKMATE) + " points for winning!");
            }
            else {
                System.out.println("Lost " + Math.abs(humanPlayer.getGameEndScore(GameEndReason.LOSE_CHECKMATE)) + " points for losing!");
            }
            isActive = false;
            notifyGameFinished(msg);

            return true;
        }

        if (checkForStaleMate()) {
            String msg = "STALEMATE! The game is a draw.";
            System.out.println(msg);
            notifyGameFinished(msg);
            isActive = false;

            return true;
        }

        if (checkThreeRepetitions()) {
            String msg = "DRAW by Repetition! The Computer resigns.";
            System.out.println(msg);
            System.out.println(humanPlayer.getColor() + " wins!");
            humanPlayer.addGameEndPoints(GameEndReason.WIN_OPPONENT_RESIGN);
            System.out.println("Gained " + humanPlayer.getGameEndScore(GameEndReason.WIN_OPPONENT_RESIGN) + " points for enemy resignation!");
            isActive = false;
            notifyGameFinished(msg);

            return true;
        }

        return false;
    }

    public boolean isActive() {
        return isActive;
    }

    // Metoda pentru renuntare a userului
    public void resignGame() {
        Player loser = players.get(currentPlayerId);
        Player winner;
        if (currentPlayerId == 0) {
            winner = players.get(1);
        }
        else {
            winner = players.get(0);
        }
        winner.addGameEndPoints(GameEndReason.WIN_OPPONENT_RESIGN);
        loser.addGameEndPoints(GameEndReason.LOSE_RESIGN);
        System.out.println("Lost " + Math.abs(players.get(currentPlayerId).getGameEndScore(GameEndReason.LOSE_RESIGN)) + " points for resigning!");
        notifyGameFinished("You have resigned.");
        this.isActive = false;
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    private void notifyMoveMade(Move move) {
        for (GameObserver obs : observers) obs.onMoveMade(move);
    }

    private void notifyGameFinished(String message) {
        for (GameObserver obs : observers) obs.onGameFinished(message);
    }

    private void notifyPlayerSwitch(Player currentPlayer) {
        for (GameObserver obs : observers) obs.onPlayerSwitch(currentPlayer);
    }
}
