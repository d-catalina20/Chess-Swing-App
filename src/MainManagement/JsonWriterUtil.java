package MainManagement;

import GameObjects.Piece;
import GameState.*;
import org.json.simple.JSONArray;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for writing JSON documents using JSON.simple.
 * This class mirrors the functionality of JsonReaderUtil but for output.
 *
 * It handles the serialization of:
 * - accounts.json (User objects)
 * - games.json (Game state, including Board, Players and Moves)
 */
// Clasa facuta "in mirror" fata de cea pt citire
public final class JsonWriterUtil {

    private JsonWriterUtil() {
    }

    /**
     * Writes the list of User objects to the specified JSON file path.
     *
     * @param path  path to accounts.json
     * @param users list of User objects to serialize
     */
    public static void writeAccounts(Path path, List<User> users) {
        if (path == null || users == null) return;

        JSONArray rootArray = new JSONArray();

        for (User user : users) {
            // Folosesc LinkedHashMap pentru a forta ordinea specifica a campurilor in JSON
            // (HashMap simplu nu garanteaza ordinea, iar LinkedHashMap o pastreaza pe cea de inserare)
            Map<String, Object> userObj = new LinkedHashMap<>();

            // Ordinea de aici va fi si ordinea vizuala din fisier
            userObj.put("email", user.getEmail());
            userObj.put("password", user.getPassword());
            userObj.put("points", user.getPoints());

            // Convertesc lista de ID-uri intr-un JSONArray
            JSONArray gamesArr = new JSONArray();
            if (user.getGameIds() != null) {
                for (Integer gameId : user.getGameIds()) {
                    gamesArr.add(gameId);
                }
            }
            userObj.put("games", gamesArr);

            rootArray.add(userObj);
        }

        writeToFile(path, rootArray);
    }

    /**
     * Writes the map of Game objects to the specified JSON file path.
     * serialization includes detailed structures for players, board state, and move history.
     *
     * @param path  path to games.json
     * @param games map of ID -> Game objects to serialize
     */
    public static void writeGames(Path path, Map<Integer, Game> games) {
        if (path == null || games == null) return;

        JSONArray rootArray = new JSONArray();

        for (Game game : games.values()) {
            // LinkedHashMap si aici
            Map<String, Object> gameObj = new LinkedHashMap<>();

            gameObj.put("id", game.getGameId());
            gameObj.put("currentPlayerColor", game.getCurrentPlayerColor().toString()); // Convertesc Enum la String

            // Lista pt playeri
            JSONArray playersArr = new JSONArray();
            if (game.getPlayers() != null) {
                for (Player p : game.getPlayers()) {
                    Map<String, Object> pObj = new LinkedHashMap<>();
                    pObj.put("email", p.getEmail());
                    pObj.put("color", p.getColor().toString());
                    pObj.put("name", p.getName());
                    playersArr.add(pObj);
                }
            }
            gameObj.put("players", playersArr);

            // Lista pt board
            JSONArray boardArr = new JSONArray();
            // Verific daca tabla exista si nu e null
            if (game.getBoard() != null) {
                // Iterez prin piesele active de pe tabla
                for (ChessPair<Position, Piece> pair : game.getBoard().getPiecesPositions()) {
                    Piece piece = pair.getValue();
                    Map<String, Object> bObj = new LinkedHashMap<>();

                    // Salvez proprietatile piesei exact cum cere formatul din games.json
                    bObj.put("type", String.valueOf(piece.type()));
                    bObj.put("color", piece.getColor().toString());
                    bObj.put("position", piece.getPosition().toString());

                    boardArr.add(bObj);
                }
            }
            gameObj.put("board", boardArr);

            // Lista pt mutari
            JSONArray movesArr = new JSONArray();
            if (game.getMoves() != null) {
                for (Move move : game.getMoves()) {
                    Map<String, Object> mObj = new LinkedHashMap<>();

                    mObj.put("playerColor", move.getPlayerColor().toString());
                    mObj.put("from", move.getFromPosition().toString());
                    mObj.put("to", move.getToPosition().toString());

                    // Adaug campul 'captured' doar daca o piesa a fost luata la acea mutare
                    if (move.getCapturedPiece() != null) {
                        Map<String, Object> capObj = new LinkedHashMap<>();
                        capObj.put("type", String.valueOf(move.getCapturedPiece().type()));
                        capObj.put("color", move.getCapturedPiece().getColor().toString());
                        mObj.put("captured", capObj);
                    }

                    movesArr.add(mObj);
                }
            }
            gameObj.put("moves", movesArr);

            rootArray.add(gameObj);
        }

        writeToFile(path, rootArray);
    }

    /**
     * Helper method to write the JSONArray to disk.
     * Uses a custom prettyPrint method to format the JSON string for readability.
     */
    private static void writeToFile(Path path, JSONArray data) {
        try (FileWriter file = new FileWriter(path.toString())) {
            // Convertesc JSONArray la String si apoi il formatez frumos (indentare)
            String niceJson = prettyPrint(data.toJSONString());
            file.write(niceJson);
            // Asigur scrierea completa in fisier
            file.flush();
        } catch (IOException e) {
            System.out.println("Writing error: " + e.getMessage());
        }
    }

    /**
     * Manually formats a raw JSON string with indentation.
     * Simple-json does not provide a native pretty-print feature.
     */
    private static String prettyPrint(String jsonString) {
        StringBuilder prettyJSON = new StringBuilder();
        int indentLevel = 0;
        boolean inQuote = false;
        // 4 spatii pentru indentare
        String indentString = "    ";

        for (char charFromJson : jsonString.toCharArray()) {
            switch (charFromJson) {
                case '"':
                    inQuote = !inQuote;
                    prettyJSON.append(charFromJson);
                    break;
                case ' ':
                    // Ignor spatiile din afara ghilimelelor pentru a controla eu formatarea
                    if (inQuote) {
                        prettyJSON.append(charFromJson);
                    }
                    break;
                case '{':
                case '[':
                    prettyJSON.append(charFromJson);
                    if (!inQuote) {
                        indentLevel++;
                        appendIndentedNewLine(prettyJSON, indentLevel, indentString);
                    }
                    break;
                case '}':
                case ']':
                    if (!inQuote) {
                        indentLevel--;
                        appendIndentedNewLine(prettyJSON, indentLevel, indentString);
                    }
                    prettyJSON.append(charFromJson);
                    break;
                case ',':
                    prettyJSON.append(charFromJson);
                    if (!inQuote) {
                        appendIndentedNewLine(prettyJSON, indentLevel, indentString);
                    }
                    break;
                case ':':
                    prettyJSON.append(charFromJson);
                    // Adaug un spatiu dupa doua puncte
                    if (!inQuote) {
                        prettyJSON.append(" ");
                    }
                    break;
                default:
                    prettyJSON.append(charFromJson);
            }
        }
        return prettyJSON.toString();
    }

    private static void appendIndentedNewLine(StringBuilder sb, int indentLevel, String indentString) {
        sb.append("\n");
        for (int i = 0; i < indentLevel; i++) {
            sb.append(indentString);
        }
    }
}