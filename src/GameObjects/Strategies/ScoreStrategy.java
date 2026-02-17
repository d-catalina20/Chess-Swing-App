package GameObjects.Strategies;

import GameObjects.Piece;
import GameState.GameEndReason;

// Strategy Pattern
public interface ScoreStrategy {
    int getScore(Piece capturedPiece);
    int getGameEndScore(GameEndReason reason);
}
