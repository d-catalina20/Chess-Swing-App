package GameState;

import GameObjects.Colors;
import GameObjects.Piece;

// Observer Pattern
public interface GameObserver {
    void onMoveMade(Move move);
    void onPlayerSwitch(Player currentPlayer);
    void onGameFinished(String message);
}
