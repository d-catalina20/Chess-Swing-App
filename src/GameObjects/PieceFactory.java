package GameObjects;

import Exceptions.InvalidCommandException;
import GameState.Position;

// Factory Pattern
public class PieceFactory {

    public static Piece createPiece(String type, Colors color, Position position) throws InvalidCommandException {
        if (type == null) {
            return null;
        }

        type = type.toUpperCase();

        switch (type) {
            case "P": return new Pawn(color, position);
            case "R": return new Rook(color, position);
            case "N": return new Knight(color, position);
            case "B": return new Bishop(color, position);
            case "Q": return new Queen(color, position);
            case "K": return new King(color, position);
            default:
                throw new InvalidCommandException("Unknown piece type: " + type);
        }
    }
}