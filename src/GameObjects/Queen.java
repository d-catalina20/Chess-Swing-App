package GameObjects;

import GameObjects.Strategies.QueenMoveStrategy;
import GameState.Board;
import GameState.Position;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(Colors color, Position position) {
        super(color, position, new QueenMoveStrategy());
    }

    @Override
    public char type() {
        return 'Q';
    }
}
