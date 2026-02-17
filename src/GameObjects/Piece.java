package GameObjects;

import GameObjects.Strategies.MoveStrategy;
import GameState.Board;
import GameState.Position;

import java.util.List;

public abstract class Piece implements ChessPiece {
    private Colors color;
    private Position position;
    // Strategy Pattern
    protected MoveStrategy moveStrategy;

    Piece(Colors color, Position position, MoveStrategy moveStrategy) {
        this.color = color;
        this.position = position;
        this.moveStrategy = moveStrategy;
    }

    public Colors getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        String pieceColor;
        if (color == Colors.WHITE) {
            pieceColor = "W";
        }
        else {
            pieceColor = "B";
        }

        return " " + type() + "-" + pieceColor;
    }

    @Override
    public boolean checkForCheck(Board board, Position kingPosition) {
        List<Position> possibleMoves = getPossibleMoves(board);
        // E sah daca regele se gaseste in lista de pozitii ale piesei
        return possibleMoves.contains(kingPosition);
    }

    public List<Position> getPossibleMoves(Board board) {
        return moveStrategy.getPossibleMoves(board, this.position, this.color);
    }
}
