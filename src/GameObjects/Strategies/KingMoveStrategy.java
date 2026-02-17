package GameObjects.Strategies;

import GameObjects.Colors;
import GameState.Board;
import GameState.Position;

import java.util.ArrayList;
import java.util.List;

public class KingMoveStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from, Colors color) {
        List<Position> moves = new ArrayList<>();
        // Sus, Jos, Dreapta, Stanga, Sus-Dreapta, Sus-Stanga, Jos-Dreapta, Jos-Stanga
        int[] dx = {0, 0, 1, -1, 1, -1, 1, -1};
        int[] dy = {1, -1, 0, 0, 1, 1, -1, -1};

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
