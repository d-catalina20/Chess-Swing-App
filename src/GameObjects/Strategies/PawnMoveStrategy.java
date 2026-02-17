package GameObjects.Strategies;

import GameObjects.Colors;
import GameState.Board;
import GameState.Position;

import java.util.ArrayList;
import java.util.List;

public class PawnMoveStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from, Colors color) {
        List<Position> moves = new ArrayList<>();
        // Stabilesc directia de mers (+1/-1)
        // Alb merge in sus, negru in jos
        int direction = 1;
        if (color == Colors.BLACK) {
            direction = -1;
        }

        char currentX = from.getX();
        int currentY = from.getY();
        Position currentPos = from;

        // Logica pt mers inainte (fara captura)
        int nextY = currentY + direction;
        Position nextPos = new Position(currentX, nextY);
        // Verific validitatea pozitiei pe tabla
        if (board.isPositionOnBoard(nextPos)) {
            // Patratel gol (pozitie valida)
            if (board.getPieceAt(nextPos) == null) {
                moves.add(nextPos);
                // Daca pionul e pe linia de start poate inainta 2 spatii
                boolean isStartingPos = (color == Colors.WHITE && currentY == 2) || (color == Colors.BLACK && currentY == 7);
                if (isStartingPos) {
                    Position forwardTwo = new Position(currentX, nextY + direction);
                    // Daca al doilea patratel e pe tabla si e gol (pozitie valida)
                    if (board.isPositionOnBoard(forwardTwo) && board.getPieceAt(forwardTwo) == null) {
                        moves.add(forwardTwo);
                    }
                }
            }
        }
        // Logica pt captura (mers pe diagonala)
        int[] captureDx = {-1, 1};
        // Iau ambele combinatii de indici
        for (int i = 0; i < 2; i++) {
            char captureX = (char)(currentX + captureDx[i]);
            // Y-ul de captura este acelasi cu cel de mers inainte
            Position capturePos = new Position(captureX, nextY);
            // Verific validitatea pozitiei pe tabla
            if (board.isPositionOnBoard(capturePos)) {
                // Pozitia e valida doar daca e captura
                if (board.isCollidingWithEnemy(currentPos, capturePos)) {
                    moves.add(capturePos);
                }
            }
        }

        return moves;
    }
}
