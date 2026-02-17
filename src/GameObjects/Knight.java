package GameObjects;

import GameObjects.Strategies.KnightMoveStrategy;
import GameState.Board;
import GameState.Position;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(Colors color, Position position) {
        super(color, position, new KnightMoveStrategy());
    }


    @Override
    public char type() {
        return 'N';
    }
}
