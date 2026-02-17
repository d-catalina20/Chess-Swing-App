package MainManagement;

import Exceptions.InvalidCommandException;
import Exceptions.InvalidCredentialsException;
import Exceptions.InvalidMoveException;
import GameObjects.Colors;
import GameObjects.Piece;
import GameState.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;

public class Main {
    // Singleton Pattern
    private static Main instance = null;

    private List<User> users;
    private Map<Integer, Game> games;
    private User currentUser;
    private Game currentGame;
    public static int lastGameId;
    static Scanner scanner;

    // Constructorul privat
    private Main() {
        lastGameId = 0;
        scanner = new Scanner(System.in);
    }

    // Lazy Initialization
    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    public void read() throws IOException, ParseException, InvalidCommandException {
        users = JsonReaderUtil.readAccounts(Path.of("src\\MainManagement\\input\\accounts.json"));
        games = JsonReaderUtil.readGamesAsMap(Path.of("src\\MainManagement\\input\\games.json"));
    }

    public void write() {
        if (users != null) {
            JsonWriterUtil.writeAccounts(Path.of("src\\MainManagement\\input\\accounts.json"), users);
        }
        if (games != null) {
            JsonWriterUtil.writeGames(Path.of("src\\MainManagement\\input\\games.json"), games);
        }
    }

    public User login(String email, String password) {
        for (User user : users) {
            // Caut user-ul cu email-ul dat
            if (user.getEmail().equals(email)) {
                // Si verific daca parola se portiveste
                if (user.getPassword().equals(password)) {
                    this.currentUser = user;
                    // Actualizez lista de jocuri a userului
                    this.currentUser.getGames().clear();
                    for (Map.Entry<Integer, Game> entry : games.entrySet()) {
                        boolean belongsToUser = false;
                        Game game = entry.getValue();
                        int id = entry.getKey();
                        // Caut userul in fiecare joc
                        if (game.getPlayers() != null) {
                            for (Player p : game.getPlayers()) {
                                if (p.getEmail().equals(email)) {
                                    belongsToUser = true;
                                    break;
                                }
                            }
                        }
                        if (belongsToUser) {
                            currentUser.addGame(game);
                        }
                    }
                    write();

                    return user;
                }
            }
        }
        // Daca login-ul esueaza, user-ul current va ramane null (adica se va ramane in meniul de logare)
        return null;
    }

    public User newAccount(String email, String password) throws InvalidCredentialsException {
        // Mai intai verific formatul emailului
        validateEmailFormat(email);
        // Daca e valid caut sa nu fie deja un cont creat cu el
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                throw new InvalidCredentialsException("There is already an account for this address.\nTry another address.");
            }
        }

        User newUser = new User(email, password);
        currentUser = newUser;
        users.add(newUser);
        write();

        return newUser;
    }

    // Valideaza formatul adresei de mail data de user-ul aplicatiei
    public void validateEmailFormat(String email) throws InvalidCredentialsException {
        // Folosesc regex ca sa impun un format valid de adresa de mail
        String regex = "^[A-Za-z0-9_.-]+@[A-Za-z0-9.-]+\\.com$";
        if (!Pattern.matches(regex, email)) {
            throw new InvalidCredentialsException("Invalid Email.\nUsage: <address>@<domain>.com");
        }
    }

    public void deleteGame(Game game) {
        if (game == null) {
            return;
        }
        // Sterg jocul din lista userului
        if (currentUser != null) {
            currentUser.removeGame(game);
        }
        // Sterg jocul si din map-ul global
        if (games != null) {
            games.remove(game.getGameId());
        }

        write();
        System.out.println("Game " + game.getGameId() + " deleted successfully.");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    // Creare nou joc
    public void startNewGameGUI(String alias, String colorString) {
        try {
            if (alias.trim().isEmpty()) {
                throw new InvalidCommandException("Alias cannot be empty.");
            }
            lastGameId++;

            Player player = new Player(currentUser.getEmail(), colorString, alias);
            String computerColor;
            if (player.getColor() == Colors.WHITE) {
                computerColor = "BLACK";
            }
            else {
                computerColor = "WHITE";
            }
            Player computer = new Player("computer", computerColor);

            currentGame = new Game(player, computer, lastGameId);
            if (games == null) {
                games = new HashMap<>();
            }
            games.put(lastGameId, currentGame);
            // Adaug jocul in lista userului
            if (!currentUser.getGameIds().contains(lastGameId)) {
                currentUser.getGameIds().add(lastGameId);
                currentUser.addGame(currentGame);
            }
            //write();
            currentGame.start();
        } catch (InvalidCommandException | InvalidMoveException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException, ParseException, InvalidCommandException {
        Main.getInstance().read();
        //Main.getInstance().run();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI.MainFrame();
            }
        });
    }
}