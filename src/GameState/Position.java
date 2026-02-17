package GameState;

import Exceptions.InvalidMoveException;

public class Position implements Comparable<Position> {
    private char x;
    private int y;

    public Position(char x, int y) {
        this.x = x;
        this.y = y;
    }

    public char getX() {
        return x;
    }

    public void setX(char x) throws InvalidMoveException {
        if (x >= 'A' && x <= 'H') {
            this.x = x;
        }
        else {
            throw new InvalidMoveException("Invalid position.");
        }
    }

    public int getY() {
        return y;
    }

    public void setY(int y) throws InvalidMoveException {
        if (y >= 1 && y <= 8) {
            this.y = y;
        }
        else {
            throw new InvalidMoveException("Invalid position.");
        }
    }

    public static void validatePosition(Position pos) throws InvalidMoveException{
        if (pos == null) {
            throw new InvalidMoveException("Position can't be null.");
        }

        char col = pos.getX();
        int row = pos.getY();
        // Coloana trebuie sa fie intre 'A' si 'H'
        // Linia trebuie sa fie intre 1 si 8
        if (col < 'A' || col > 'H' || row < 1 || row > 8) {
            throw new InvalidMoveException("Invalid position on board.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Position))
            return false;

        Position pos = (Position) o;
        return x == pos.x && y == pos.y;
    }

    // Returneaza negativ daca this < o, 0 daca sunt egale, pozitiv daca this > o
    @Override
    public int compareTo(Position o) {
        if (this.equals(o)) {
            return 0;
        }
        // Mai intai crescator dupa coordonata y, apoi dupa coordonata x
        else if (this.y != o.y) {
            return this.y - o.y;
        }
        else {
            return this.x - o.x;
        }
    }

    @Override
    public String toString() {
        return x + "" + y;
    }
}
