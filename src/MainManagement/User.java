package MainManagement;

import GameState.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {
    private String email, password;
    private List<Game> games;
    private List<Integer> gameIds;
    private int points;

    public User() {
        email = "";
        password = "";
        points = 0;
        games = new ArrayList<>();
        gameIds = new ArrayList<>();
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        points = 0;
        games = new ArrayList<>();
        gameIds = new ArrayList<>();
    }

    public void addGame(Game game) {
        // Evit duplicatele
        if (!this.games.contains(game)) {
            this.games.add(game);
            // Sincronizez id-ul cu jocul
            if (!this.gameIds.contains(game.getGameId())) {
                this.gameIds.add(game.getGameId());
            }
        }
    }

    public void removeGame(Game game) {
        games.remove(game);
        // Cast la Integer ca sa stearga obiectul, nu indexul
        this.gameIds.remove((Integer) game.getGameId());
    }

    public List<Game> getActiveGames() {
        List<Integer> activeGameIds = new ArrayList<>();
        List<Game> activeGames = new ArrayList<>();
        if (games != null) {
            for (Game game : games) {
                if (game.isActive()) {
                    activeGames.add(game);
                    activeGameIds.add(game.getGameId());
                }
            }
        }
        setGames(activeGames);
        setGameIds(activeGameIds);
        return activeGames;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        // Daca email-ul n-a fost setat
        if (this.email.equals("")) {
            this.email = email;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        // Daca parola n-a fost setata
        if (this.password.equals("")) {
            this.password = password;
        }
    }

    public List<Integer> getGameIds() {
        return gameIds;
    }

    public void setGameIds(List<Integer> gameIds) {
        this.gameIds = gameIds;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
}
