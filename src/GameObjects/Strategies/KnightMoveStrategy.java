package GameObjects.Strategies;

import GameObjects.Colors;
import GameState.Board;
import GameState.Position;

import java.util.ArrayList;
import java.util.List;

public class KnightMoveStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from, Colors color) {
        List<Position> moves = new ArrayList<>();
        // Pozitiile in forma de L ale calului
        int[] dx = {1, -1, 1, -1, 2, 2, -2, -2};
        int[] dy = {2, 2, -2, -2, 1, -1, 1, -1};

        char currentX = from.getX();
        int currentY = from.getY();
        Position currentPos = new Position(currentX, currentY);
        // Iau fiecare combinatie de indici
        for (int i = 0; i < 8; i++) {
            int nextY = currentY + dy[i];
            char nextX = (char) (currentX + (char)dx[i]);
            // Calculez pozitiile urmatoare
            Position nextPos = new Position(nextX, nextY);
            if (board.isPositionOnBoard(nextPos)) {
                // Patratel gol sau captura (pozitie valida)
                if (!board.isCollidingWithAlly(currentPos, nextPos)) {
                    moves.add(nextPos);
                }
            }
        }

        return moves;
    }
}
