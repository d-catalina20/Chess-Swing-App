package GameState;

import Exceptions.InvalidMoveException;
import GameObjects.Colors;
import GameObjects.Piece;

public class Move {
    private Colors playerColor;
    private Position fromPosition;
    private Position toPosition;
    private Piece capturedPiece;

    public Move(String playerColor, String from, String to) throws InvalidMoveException {
        if (playerColor.equals("WHITE")) {
            this.playerColor = Colors.WHITE;
        }
        else {
            this.playerColor = Colors.BLACK;
        }

        if (from != null && from.length() >= 2) {
            char x = from.charAt(0);
            int y = Character.getNumericValue(from.charAt(1));
            fromPosition = new Position(x, y);
        }
        else {
            throw new InvalidMoveException("Nu e o pozitie valida.");
        }

        if (to != null && to.length() >= 2) {
            char x = to.charAt(0);
            int y = Character.getNumericValue(to.charAt(1));
            toPosition = new Position(x, y);
        }
        else {
            throw new InvalidMoveException("Nu e o pozitie valida.");
        }
    }

    public Move(String playerColor, String from, String to, Piece capturedPiece) throws InvalidMoveException {
        this(playerColor, from, to);
        this.capturedPiece = capturedPiece;
    }


    public Move(Colors playerColor, Position from, Position to, Piece capturedPiece) {
        this.playerColor = playerColor;
        fromPosition = from;
        toPosition = to;
        this.capturedPiece = capturedPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Move)) {
            return false;
        }

        Move move = (Move) o;
        return playerColor == move.playerColor && fromPosition.equals(move.fromPosition) && toPosition.equals(move.toPosition);
    }

    public Colors getPlayerColor() {
        return playerColor;
    }

    public Position getFromPosition() {
        return fromPosition;
    }

    public Position getToPosition() {
        return toPosition;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }
}
