package MainManagement;

import Exceptions.InvalidCommandException;
import Exceptions.InvalidMoveException;
import GameObjects.Piece;
import GameState.Game;
import GameState.Position;
import MainManagement.Main;
import MainManagement.User;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

public class TestImplementation {

    public static void main(String[] args) throws InvalidCommandException, IOException, ParseException {
        System.out.println("----------");
        System.out.println("STARTED AUTOMATATED TESTING FOR CHESS HACKER GAME");
        System.out.println("----------");

        // 1. Citire json
        Main.getInstance().read();

        // 2. Creare cont si login
        testUserAuthentication();

        // Logica jocului (mutari, validari)
        Game game = testGameLogic();

        // Persistenta
        testPersistence(game);

        System.out.println("\n----------");
        System.out.println("TESTING FINISHED SUCCESSFULLY");
        System.out.println("----------");
    }

    private static void testUserAuthentication() {
        System.out.println("\n[TEST 1] User Authentication...");
        Main app = Main.getInstance();

        try {
            // Inregistrare
            app.newAccount("testuser@chess.com", "parola123");
            System.out.println("   -> Create Account: SUCCESS");

            // Login
            User user = app.login("testuser@chess.com", "parola123");
            if (user != null && user.getEmail().equals("testuser@chess.com")) {
                System.out.println("   -> Login: SUCCESS");
            } else {
                System.err.println("   -> Login: FAILED");
            }
        } catch (Exception e) {
            System.err.println("   -> Auth Error: " + e.getMessage());
        }
    }

    private static Game testGameLogic() {
        System.out.println("\n[TEST 2] Game Logic & Rules...");
        Main app = Main.getInstance();

        // Joc nou (fara GUI)
        // Alias si culoare (White)
        app.startNewGameGUI("TestPlayer", "WHITE");
        Game game = app.getCurrentGame();

        if (game == null) {
            System.err.println("   -> Game Creation: FAILED");
            return null;
        }
        System.out.println("   -> Game Creation: SUCCESS");

        try {
            // SCENARIU: Pion alb e2 -> e4
            System.out.print("   -> Move 1 (White Pawn e2-e4): ");
            Position from1 = new Position('E', 2);
            Position to1 = new Position('E', 4);
            game.addMove(game.getCurrentPlayer(), from1, to1, null);
            System.out.println("VALID (Correct)");

            // Verific daca tura s-a schimbat la negru
            if (game.getCurrentPlayerColor().toString().equals("BLACK")) {
                System.out.println("   -> Turn Switch: SUCCESS");
            } else {
                System.err.println("   -> Turn Switch: FAILED");
            }

            // SCENARIU: Mutare invalida (Negru incearca sa mute tura prin pioni)
            // Tura a8 -> a6 (imposibil, pion in a7)
            System.out.print("   -> Move 2 (Invalid Rook a8-a6): ");
            Position fromInv = new Position('A', 8);
            Position toInv = new Position('A', 6);
            try {
                game.addMove(game.getCurrentPlayer(), fromInv, toInv, null);
                System.err.println("FAILED (Should have thrown exception)");
            } catch (InvalidMoveException e) {
                System.out.println("BLOCKED (Correct: " + e.getMessage() + ")");
            }

            // SCENARIU: Mutare Valida Negru (Pion e7 -> e5)
            System.out.print("   -> Move 2 (Black Pawn e7-e5): ");
            game.addMove(game.getCurrentPlayer(), new Position('E', 7), new Position('E', 5), null);
            System.out.println("VALID (Correct)");

            // SCENARIU: Captura (Simulam o situatie ipotetica sau continuam jocul)
            // Continuam spre Scholar's Mate pentru a testa sahul

            // 3. Alb: Nebun f1 -> c4
            game.addMove(game.getCurrentPlayer(), new Position('F', 1), new Position('C', 4), null); // White
            // 4. Negru: Cal b8 -> c6
            game.addMove(game.getCurrentPlayer(), new Position('B', 8), new Position('C', 6), null); // Black
            // 5. Alb: Regina d1 -> h5
            game.addMove(game.getCurrentPlayer(), new Position('D', 1), new Position('H', 5), null); // White
            // 6. Negru: Cal g8 -> f6
            game.addMove(game.getCurrentPlayer(), new Position('G', 8), new Position('F', 6), null); // Black

            System.out.println("   -> Multiple moves executed successfully.");

            // 7. Alb: Regina h5 -> f7 (CAPTURA + SAH MAT)
            System.out.print("   -> Move (Capture + Checkmate attempt Qf7): ");
            Position qFrom = new Position('H', 5);
            Position qTo = new Position('F', 7);

            // Verific ce e la destinatie inainte
            Piece target = game.getBoard().getPieceAt(qTo);
            if(target != null) System.out.print("[Target is " + target.type() + "] ");

            game.addMove(game.getCurrentPlayer(), qFrom, qTo, null);
            System.out.println("EXECUTED");

            // Verificam starea de final
            if (game.isFinished()) {
                System.out.println("   -> Checkmate Detection: SUCCESS");
                Main.getInstance().deleteGame(game);
            } else {
                System.out.println("   -> Checkmate Detection: NOT YET / FAILED");
                if(game.getBoard().isKingInCheck(GameObjects.Colors.BLACK)) {
                    System.out.println("   -> Check Detection: SUCCESS");
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return game;
    }

    private static void testPersistence(Game activeGame) {
        System.out.println("\n[TEST 3] Persistence (Save/Load)...");
        Main app = Main.getInstance();

        app.login("c@g.com", "c");

        // Repornirea aplicatiei (Recitire)
        try {
            app.read();
            System.out.println("   -> Data read from JSON.");

            // Verific daca userul are jocuri salvat
            User user = app.getCurrentUser();
            if (user.getActiveGames().size() > 0) {
                System.out.println("   -> Game found in user list: SUCCESS");

                // Verific ID-ul jocului
                Game loadedGame = user.getActiveGames().get(0);
                System.out.println("   -> Loaded Game ID: " + loadedGame.getGameId());
                System.out.println("   -> Loaded Game Moves Count: " + loadedGame.getMoves().size());

                if (loadedGame.getMoves().size() > 0) {
                    System.out.println("   -> Moves History Preserved: SUCCESS");
                } else {
                    System.err.println("   -> Moves History Lost: FAILED");
                }
            } else {
                System.err.println("   -> No games found after load: FAILED");
            }

        } catch (Exception e) {
            System.err.println("   -> Persistence Error: " + e.getMessage());
        }
    }
}