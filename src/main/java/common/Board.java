package common;
import java.util.HashMap;
import java.util.Map;

public class Board {
    private Map<Position, Piece> board;  // A map to track pieces by their positions

    public Board() {
        this.board = new HashMap<>();
        setupInitialPosition();
    }

    /**
     * Sets up the initial positions of all pieces on the board.
     */
    public void setupInitialPosition() {
        // Place Black pieces
        for (int i = 0; i < 8; i++) {  // Pawns
            board.put(new Position(i, 1), new Piece(PieceType.PAWN, false));
        }
        // Rooks
        board.put(new Position(0, 0), new Piece(PieceType.ROOK, false));
        board.put(new Position(7, 0), new Piece(PieceType.ROOK, false));
        // Knights
        board.put(new Position(1, 0), new Piece(PieceType.KNIGHT, false));
        board.put(new Position(6, 0), new Piece(PieceType.KNIGHT, false));
        // Bishops
        board.put(new Position(2, 0), new Piece(PieceType.BISHOP, false));
        board.put(new Position(5, 0), new Piece(PieceType.BISHOP, false));
        // Queen and King
        board.put(new Position(3, 0), new Piece(PieceType.QUEEN, false));
        board.put(new Position(4, 0), new Piece(PieceType.KING, false));

        // Place White pieces
        for (int i = 0; i < 8; i++) {  // Pawns
            board.put(new Position(i, 6), new Piece(PieceType.PAWN, true));
        }
        // Rooks
        board.put(new Position(0, 7), new Piece(PieceType.ROOK, true));
        board.put(new Position(7, 7), new Piece(PieceType.ROOK, true));
        // Knights
        board.put(new Position(1, 7), new Piece(PieceType.KNIGHT, true));
        board.put(new Position(6, 7), new Piece(PieceType.KNIGHT, true));
        // Bishops
        board.put(new Position(2, 7), new Piece(PieceType.BISHOP, true));
        board.put(new Position(5, 7), new Piece(PieceType.BISHOP, true));
        // Queen and King
        board.put(new Position(3, 7), new Piece(PieceType.QUEEN, true));
        board.put(new Position(4, 7), new Piece(PieceType.KING, true));
    }

    /**
     * Validates whether a move from one position to another is legal.
     * @param from Start position of the move.
     * @param to End position of the move.
     * @param isWhiteTurn Indicates if it's White's turn.
     * @return true if the move is legal; false otherwise.
     */
    public boolean isValidMove(Position from, Position to, boolean isWhiteTurn) {
        Piece piece = board.get(from);
        if (piece == null || piece.isWhite() != isWhiteTurn) {
            return false; // No piece at the starting position or wrong turn
        }

        return piece.isValidMove(from, to, this);
    }

    /**
     * Moves a piece from one position to another.
     * @param from Start position of the move.
     * @param to End position of the move.
     */
    public void movePiece(Position from, Position to) {
        Piece piece = board.get(from);
        if (piece != null) {
            board.remove(from);
            board.put(to, piece);
        }
    }

    @Override
    public String toString() {
        // Optional: Implement a way to convert the board into a string for easier debugging/printing
        return "";  // Implement based on your needs
    }
}
