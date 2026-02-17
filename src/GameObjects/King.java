package GameObjects;

import GameObjects.Strategies.KingMoveStrategy;
import GameState.Board;
import GameState.Position;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(Colors color, Position position) {
        super(color, position, new KingMoveStrategy());
    }

    @Override
    public char type() {
        return 'K';
    }
}
