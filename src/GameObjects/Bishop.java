package GameObjects;

import GameObjects.Strategies.BishopMoveStrategy;
import GameState.Board;
import GameState.Position;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(Colors color, Position position) {
        super(color, position, new BishopMoveStrategy());
    }

    @Override
    public char type() {
        return 'B';
    }
}
