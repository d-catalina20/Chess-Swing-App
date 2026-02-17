package GameState;

import Exceptions.InvalidCommandException;
import Exceptions.InvalidMoveException;
import GameObjects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Board {
    private TreeSet<ChessPair<Position, Piece>> piecesPositions;

    public Board() {
        piecesPositions = new TreeSet<>();
    }

    public TreeSet<ChessPair<Position, Piece>> getPiecesPositions() {
        return piecesPositions;
    }

    public void initialize() throws InvalidCommandException {
        for (char col = 'A'; col <= 'H'; col++) {
            addPiece(PieceFactory.createPiece("P", Colors.WHITE, new Position(col, 2)));
            addPiece(PieceFactory.createPiece("P", Colors.BLACK, new Position(col, 7)));
        }

        initMajorPiecesRow(1, Colors.WHITE);
        initMajorPiecesRow(8, Colors.BLACK);
    }

    private void initMajorPiecesRow(int row, Colors color) throws InvalidCommandException {
        addPiece(PieceFactory.createPiece("R", color, new Position('A', row)));
        addPiece(PieceFactory.createPiece("N", color, new Position('B', row)));
        addPiece(PieceFactory.createPiece("B", color, new Position('C', row)));
        addPiece(PieceFactory.createPiece("Q", color, new Position('D', row)));
        addPiece(PieceFactory.createPiece("K", color, new Position('E', row)));
        addPiece(PieceFactory.createPiece("B", color, new Position('F', row)));
        addPiece(PieceFactory.createPiece("N", color, new Position('G', row)));
        addPiece(PieceFactory.createPiece("R", color, new Position('H', row)));
    }

    public void addPiece(Piece piece) {
        piecesPositions.add(new ChessPair<>(piece.getPosition(), piece));
    }

    public void removePiece(Piece piece) {
        piecesPositions.remove(new ChessPair<>(piece.getPosition(), piece));
    }

    public void removeAllPieces() {
        piecesPositions.clear();
    }

    public void movePiece(Position from, Position to, String promotionType) throws InvalidMoveException {
        if (isValidMove(from, to)) {
            Piece pieceToMove = getPieceAt(from);
            Piece capturedPiece = getPieceAt(to);

            // Daca o piesa a fost capturata
            if (capturedPiece != null) {
                // Scot piesa capturata de pe tabla
                removePiece(capturedPiece);
            }
            // Sterg piesa cu pozitia veche din TreeSet
            removePiece(pieceToMove);
            // Setez pozitia noua a piesei
            pieceToMove.setPosition(to);

            // Logica pt promovarea pionului
            if (pieceToMove instanceof Pawn) {
                int y = pieceToMove.getPosition().getY();
                Colors color = pieceToMove.getColor();
                // Daca pionul a ajuns la capat
                if ((color == Colors.WHITE && y == 8) || (color == Colors.BLACK && y == 1)) {
                    // Promovez pionul
                    try {
                        pieceToMove = createPromotedPiece(color, to, promotionType);
                        System.out.println("Pawn promoted to" + pieceToMove + "!");
                    }
                    catch (InvalidCommandException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
            // Pun piesa la pozitia noua inapoi in TreeSet
            addPiece(pieceToMove);
        }
        else {
            throw new InvalidMoveException("Piece can't be moved to that space.");
        }
    }

    private Piece createPromotedPiece(Colors color, Position pos, String type) throws InvalidCommandException {
        if (type == null) {
            throw new InvalidCommandException("Invalid command.");
        }
        Piece p = PieceFactory.createPiece(type, color, pos);
        if (p == null) {
            throw new InvalidCommandException("Invalid command.");
        }

        return p;
    }

    public Piece getPieceAt(Position position) {
        for (ChessPair<Position, Piece> piecePosition : piecesPositions) {
            if (piecePosition.getKey().equals(position)) {
                return piecePosition.getValue();
            }
        }

        return null;
    }

    public boolean isValidMove(Position from, Position to) {
        // Daca pozitia nu e pe tabla
        if (!isPositionOnBoard(to)) {
            return false;
        }

        Piece piece = getPieceAt(from);
        // Daca piesa la pozitia data nu exista
        if (piece == null) {
            return false;
        }
        // Piesa nu poate sa ramana pe loc
        if (from.equals(to)) {
            return false;
        }
        // Pozitia noua trebuie sa fie in lista piesei de mutari posibile
        List<Position> moves = piece.getPossibleMoves(this);
        if (!moves.contains(to)) {
            return false;
        }

        // Simulez verificarea de sah
        // Scot piesa de la pozitia veche din lista
        removePiece(piece);
        Piece capturedPiece = getPieceAt(to);
        // Daca a fost capturata o piesa
        if (capturedPiece != null) {
            // O scot din lista
            removePiece(capturedPiece);
        }
        // Adaug piesa la pozitia noua
        piece.setPosition(to);
        addPiece(piece);

        // Verific daca mutarea a pus regele propriu in sah
        boolean moveGetsCheck = isKingInCheck(piece.getColor());
        // Revin la pozitia dinaintea simularii mutarii
        removePiece(piece);
        piece.setPosition(from);
        addPiece(piece);
        // Daca a fost facuta o captura in simulare, pun inapoi piesa capturata
        if (capturedPiece != null) {
            addPiece(capturedPiece);
        }

        // Mutarea e valida daca nu pune regele propriu in sah
        return !moveGetsCheck;
    }

    // Returneaza true daca jucatorul nu are nicio mutare valida si e in sah
    public boolean isCheckMate(Colors playerColor) {
        return checkOrStaleMate(playerColor) && isKingInCheck(playerColor);
    }

    // Returneaza true daca jucatorul nu are nicio mutare valida, dar nu e în sah
    public boolean isStaleMate(Colors playerColor) {
        return checkOrStaleMate(playerColor) && !isKingInCheck(playerColor);
    }

    private boolean checkOrStaleMate(Colors playerColor) {
        // Fac o copie a pieselor jucatorului curent pentru a evita erori (deoarece isValidMove modifica temporar TreeSet-ul)
        List<Piece> playerPieces = new ArrayList<>();
        for (ChessPair<Position, Piece> pair : piecesPositions) {
            if (pair.getValue().getColor() == playerColor) {
                playerPieces.add(pair.getValue());
            }
        }
        // Caut oricare mutare care salveaza regele
        for (Piece p : playerPieces) {
            List<Position> potentialMoves = p.getPossibleMoves(this);
            for (Position dest : potentialMoves) {
                if (isValidMove(p.getPosition(), dest)) {
                    // Am gasit o mutare, deci jocul poate continua
                    return false;
                }
            }
        }
        // Nu am gasit nicio mutare valida
        return true;
    }

    public boolean isKingInCheck(Colors kingColor) {
        Position kingPos = findKingPosition(kingColor);
        if (kingPos == null) return false;

        for (ChessPair<Position, Piece> pair : piecesPositions) {
            Piece p = pair.getValue();

            // Dacă piesa e inamica
            if (p.getColor() != kingColor) {
                if (p.checkForCheck(this, kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Position findKingPosition(Colors color) {
        for (ChessPair<Position, Piece> pair : piecesPositions) {
            Piece p = pair.getValue();
            if (p instanceof King && p.getColor() == color) {
                return p.getPosition();
            }
        }

        return null;
    }

    public boolean isPositionOnBoard(Position to) {
        return (to.getX() >= 'A' && to.getX() <= 'H') && (to.getY() >= 1 && to.getY() <= 8);
    }

    public boolean isCollidingWithAlly(Position from, Position to) {
        Piece pieceToMove = getPieceAt(from);
        Piece pieceAtTargetPos = getPieceAt(to);

        if (pieceAtTargetPos == null) {
            return false;
        }
        else {
            return pieceAtTargetPos.getColor() == pieceToMove.getColor();
        }
    }

    public boolean isCollidingWithEnemy(Position from, Position to) {
        Piece pieceToMove = getPieceAt(from);
        Piece pieceAtTargetPos = getPieceAt(to);

        if (pieceAtTargetPos == null) {
            return false;
        }
        else {
            return pieceAtTargetPos.getColor() != pieceToMove.getColor();
        }
    }

    public void printBoard(Colors playerColor) {
        // Matrice temporara pentru afisare
        String[][] displayMatrix = new String[8][8];
        // Pun pe fiecare pozitie din matrice stringul care reprezinta un patratel de pe tabla
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                displayMatrix[i][j] = " ...";
            }
        }

        // Pun piesele din TreeSet in matrice
        for (ChessPair<Position, Piece> piecePosition : piecesPositions) {
            Position pos = piecePosition.getKey();
            Piece piece = piecePosition.getValue();
            // A...H pe coloane
            int col = pos.getX() - 'A';
            // 8...1 pe linii
            int row = 8 - pos.getY();
            // Inlocuiesc patratelele cu piese pe pozitiile adecvate
            displayMatrix[row][col] = piece.toString();
        }

        // Organizez afisarea ca sa reflecte perspectiva jucatorului
        System.out.println("   ___________________________________");
        // Daca jucatorul si-a ales piesele albe
        if (playerColor == Colors.WHITE) {
            for (int i = 0; i < 8; i++) {
                System.out.print(" " + (8 - i) + " |");
                for (int j = 0; j < 8; j++) {
                    System.out.print(displayMatrix[i][j]);
                }
                System.out.println(" |");
            }
            System.out.println("   ___________________________________");
            System.out.println("      A   B   C   D   E   F   G   H");
        }
        // Daca jucatorul si-a ales piesele negre
        else {
            for (int i = 7; i >= 0; i--) {
                System.out.print(" " + (8 - i) + " |");
                for (int j = 7; j >= 0; j--) {
                    System.out.print(displayMatrix[i][j]);
                }
                System.out.println(" |");
            }
            System.out.println("   ___________________________________");
            System.out.println("      H   G   F   E   D   C   B   A");
        }
    }
}
