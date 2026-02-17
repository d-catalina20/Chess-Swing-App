package GameObjects;

import GameObjects.Strategies.RookMoveStrategy;
import GameState.Board;
import GameState.Position;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(Colors color, Position position) {
        super(color, position, new RookMoveStrategy());
    }

    @Override
    public char type() {
        return 'R';
    }
}
