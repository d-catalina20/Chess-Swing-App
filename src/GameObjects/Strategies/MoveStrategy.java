package GameObjects.Strategies;

import GameState.Board;
import GameState.Position;
import GameObjects.Colors;

import java.awt.*;
import java.util.List;

// Strategy Pattern
public interface MoveStrategy {
    List<Position> getPossibleMoves(Board board, Position currentPos, Colors pieceColor);
}