package GameObjects.Strategies;

import GameObjects.Colors;
import GameState.Board;
import GameState.Position;

import java.util.ArrayList;
import java.util.List;

public class BishopMoveStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from, Colors color) {
        List<Position> moves = new ArrayList<>();
        // Sus-Dreapta, Sus-Stanga, Jos-Dreapta, Jos-Stanga
        int[] dx = {1, -1, 1, -1};
        int[] dy = {1, 1, -1, -1};

        char currentX = from.getX();
        int currentY = from.getY();
        // Iau fiecare combinatie de indici
        for (int i = 0; i < 4; i++) {
            Position currentPos = new Position(currentX, currentY);
            // Calculez pozitiile urmatoare
            int nextY = currentY;
            char nextX = currentX;
            // Cat timp indicii raman valizi
            while (true) {
                nextY += dy[i];
                nextX += (char) dx[i];
                // Verific validitatea pozitiei pe tabla
                Position nextPos = new Position(nextX, nextY);
                if (board.isPositionOnBoard(nextPos)) {
                    // Patratel piesa de aceeasi culoare
                    if (board.isCollidingWithAlly(currentPos, nextPos)) {
                        break;
                    }
                    // Patratel captura (pozitie valida)
                    if (board.isCollidingWithEnemy(currentPos, nextPos)) {
                        moves.add(nextPos);
                        break;
                    }
                    // Patratel gol (pozitie valida)
                    moves.add(nextPos);
                }
                // Daca nu e pe tabla, ma opresc
                else {
                    break;
                }
            }
        }

        return moves;
    }
}
