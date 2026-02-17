package GameObjects.Strategies;

import GameObjects.Piece;
import GameState.GameEndReason;

public class StandardScoreStrategy implements ScoreStrategy {
    @Override
    public int getScore(Piece capturedPiece) {
        if (capturedPiece.type() == 'Q') return 90;
        if (capturedPiece.type() == 'R') return 50;
        if (capturedPiece.type() == 'B') return 30;
        if (capturedPiece.type() == 'N') return 30;
        if (capturedPiece.type() == 'P') return 10;
        return 0;
    }

    @Override
    public int getGameEndScore(GameEndReason reason) {
        switch (reason) {
            case WIN_CHECKMATE:       return 300;
            case LOSE_CHECKMATE:      return -300;
            case WIN_OPPONENT_RESIGN: return 150;
            case LOSE_RESIGN:         return -150;
            case DRAW_STALEMATE:      return 0;
            default:                  return 0;
        }
    }
}
