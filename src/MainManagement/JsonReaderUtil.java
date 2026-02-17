package MainManagement;

import Exceptions.InvalidCommandException;
import Exceptions.InvalidMoveException;
import GameObjects.*;
import GameState.Game;
import GameState.Move;
import GameState.Player;
import GameState.Position;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading JSON documents using JSON.simple ("simple-json").
 *
 * ###### IMPORTANT: This is just an example of how to read JSON documents using the library.
 * Your classes might differ slightly, so don’t hesitate to update this class as needed.
 *
 * Expected structures:
 * - accounts.json: an array of objects with fields: email (String), password (String), points (Number), games (array of numbers)
 * - games.json: an array of objects with fields matching the JSON provided:
 *   id (Number), players (array of {email, color}), currentPlayerColor (String),
 *   board (array of {type, color, position}), moves (array of {playerColor, from, to})
 */
public final class JsonReaderUtil {

    JsonReaderUtil() {
    }

    /**
     * Reads the accounts from the given JSON file path.
     *
     * @param path path to accounts.json
     * @return list of Account objects (empty list if file not found or array empty)
     * @throws IOException    if I/O fails
     * @throws ParseException if JSON is invalid
     */
    public static List<User> readAccounts(Path path) throws IOException, ParseException {
        if (path == null || !Files.exists(path)) {
            return new ArrayList<>();
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            JSONArray arr = asArray(root);
            List<User> result = new ArrayList<>();

            if (arr == null) {
                return result;
            }

            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) {
                    continue;
                }

                User acc = new User();
                acc.setEmail(asString(obj.get("email")));
                acc.setPassword(asString(obj.get("password")));
                acc.setPoints(asInt(obj.get("points"), 0));
                List<Integer> gameIds = new ArrayList<>();
                JSONArray games = asArray(obj.get("games"));
                if (games != null) {
                    for (Object gid : games) {
                        gameIds.add(asInt(gid, 0));
                    }
                }
                acc.setGameIds(gameIds);
                result.add(acc);
            }
            return result;
        }
    }

    /**
     * Reads the games from the given JSON file path and returns them as a map by id.
     * The structure strictly follows games.json as provided (no title/genre).
     *
     * @param path path to games.json
     * @return map id -> Game (empty if file missing or array empty)
     * @throws IOException    if I/O fails
     * @throws ParseException if JSON is invalid
     */
    public static Map<Integer, Game> readGamesAsMap(Path path) throws IOException, ParseException {
        Map<Integer, Game> map = new HashMap<>();
        if (path == null || !Files.exists(path)) {
            return map;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            JSONArray arr = asArray(root);
            if (arr == null) return map;
            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) {
                    continue;
                }
                int id = asInt(obj.get("id"), -1);
                if (id < 0) {
                    continue;
                }// skip invalid
                Game g = new Game();
                g.setGameId(id);
                if (id > Main.lastGameId) {
                    Main.lastGameId = id;
                }
                g.setCurrentPlayerColor((String) obj.get("currentPlayerColor"));

                // Map ajutator pentru a gasi rapid jucatorul dupa culoare (pt a-i da punctele)
                Map<String, Player> playerByColorMap = new HashMap<>();

                // Players array
                JSONArray playersArr = asArray(obj.get("players"));
                if (playersArr != null) {
                    List<Player> players = new ArrayList<>();
                    for (Object pItem : playersArr) {
                        JSONObject pObj = asObject(pItem);
                        if (pObj == null) {
                            continue;
                        }
                        String email = asString(pObj.get("email"));
                        String color = asString(pObj.get("color"));
                        String name = asString(pObj.get("name"));
                        if (name == null) name = "NaN";
                        Player p = new Player(email, color, name);
                        players.add(p);
                        playerByColorMap.put(color, p);
                    }
                    g.setPlayers(players);
                }

                g.setCurrentPlayerColor(asString(obj.get("currentPlayerColor")));

                // Board array
                JSONArray boardArr = asArray(obj.get("board"));
                if (boardArr != null) {
                    for (Object bItem : boardArr) {
                        JSONObject bObj = asObject(bItem);
                        if (bObj == null) {
                            continue;
                        }

                        String type = asString(bObj.get("type"));
                        String color = asString(bObj.get("color"));
                        String position = asString(bObj.get("position"));
                        char col = position.charAt(0);
                        int row = Character.getNumericValue(position.charAt(1));
                        Position pos = new Position(col, row);
                        Colors pieceColor = color.equals("WHITE") ? Colors.WHITE : Colors.BLACK;
                        Piece piece = PieceFactory.createPiece(type, pieceColor, pos);

                        if (piece != null) {
                            g.getBoard().addPiece(piece);
                        }
                    }
                }

                JSONArray movesArr = asArray(obj.get("moves"));
                if (movesArr != null) {
                    List<Move> moves = new ArrayList<>();
                    for (Object mItem : movesArr) {
                        JSONObject mObj = asObject(mItem);
                        if (mObj == null) {
                            continue;
                        }

                        String playerColor = asString(mObj.get("playerColor"));
                        String from = asString(mObj.get("from"));
                        String to = asString(mObj.get("to"));

                        JSONObject capturedObj = asObject(mObj.get("captured"));
                        Piece capturedPiece = null;

                        if (capturedObj != null) {
                            String cType = asString(capturedObj.get("type"));
                            String cColorStr = asString(capturedObj.get("color"));
                            Colors cColor = cColorStr.equals("WHITE") ? Colors.WHITE : Colors.BLACK;

                            // Pozitia nu conteaza pentru piese capturate (sunt scoase de pe tabla), pun una oarecare
                            capturedPiece = PieceFactory.createPiece(cType, cColor, new Position('A', 1));

                            // Adaug piesa la jucatorul care a facut captura
                            Player p = playerByColorMap.get(playerColor);
                            if (p != null) {
                                p.getCapturedPieces().add(capturedPiece);
                            }
                        }

                        // Adaug mutarea in istoric
                        moves.add(new Move(playerColor, from, to, capturedPiece));
                    }
                    g.setMoves(moves);
                }
                // Dupa ce am parcurs tot istoricul si am umplut listele de capturi, recalculez scorul
                if (g.getPlayers() != null) {
                    for (Player p : g.getPlayers()) {
                        p.recalculatePoints();
                    }
                }

                map.put(id, g);
            }
        }
        catch (InvalidCommandException | InvalidMoveException e) {
            System.out.println(e.getMessage());
        }
        return map;
    }

    private static JSONArray asArray(Object o) {
        return (o instanceof JSONArray) ? (JSONArray) o : null;
    }

    private static JSONObject asObject(Object o) {
        return (o instanceof JSONObject) ? (JSONObject) o : null;
    }

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static int asInt(Object o, int def) {
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return o != null ? Integer.parseInt(String.valueOf(o)) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
