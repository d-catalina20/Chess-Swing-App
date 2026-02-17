package GameObjects;

import GameObjects.Strategies.PawnMoveStrategy;
import GameState.Board;
import GameState.Position;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(Colors color, Position position) {
        super(color, position, new PawnMoveStrategy());
    }

    @Override
    public char type() {
        return 'P';
    }

}
