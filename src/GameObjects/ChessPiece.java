package GameObjects;

import GameState.Board;
import GameState.Position;

import java.util.List;

public interface ChessPiece {
    List<Position> getPossibleMoves(Board board);
    boolean checkForCheck(Board board, Position kingPosition);
    char type();
    String toString();
}
