package common;

public class Piece {
    private PieceType type;
    private boolean isWhite;  // true for white pieces, false for black pieces

    public Piece(PieceType type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public PieceType getType() {
        return type;
    }

    /**
     * Validates if a move is valid for this piece based on its type and the game rules.
     * @param from Starting position of the piece.
     * @param to Target position of the piece.
     * @param board The current board state.
     * @return true if the move is valid.
     */
    public boolean isValidMove(Position from, Position to, Board board) {
        // Basic bounds checking
        if (!isPositionValid(to)) {
            return false;
        }

        switch (type) {
            case PAWN:
                return isValidPawnMove(from, to, board, isWhite);
            case KNIGHT:
                return isValidKnightMove(from, to, board);
            case BISHOP:
                return isValidBishopMove(from, to, board);
            case ROOK:
                return isValidRookMove(from, to, board);
            case QUEEN:
                return isValidQueenMove(from, to, board);
            case KING:
                return isValidKingMove(from, to, board);
            default:
                return false;
        }
    }

    private boolean isPositionValid(Position position) {
        return position.getX() >= 0 && position.getX() < 8 && position.getY() >= 0 && position.getY() < 8;
    }

    // Implement specific validation logic for each type of piece:
    private boolean isValidPawnMove(Position from, Position to, Board board, boolean isWhite) {
        // Implement pawn movement logic (including capturing, en passant, first move double step)
        return true;  // Placeholder
    }

    private boolean isValidKnightMove(Position from, Position to, Board board) {
        // Knights move in an L shape: two squares in one direction, then one square perpendicular
        return true;  // Placeholder
    }

    private boolean isValidBishopMove(Position from, Position to, Board board) {
        // Bishops move diagonally any number of squares
        return true;  // Placeholder
    }

    private boolean isValidRookMove(Position from, Position to, Board board) {
        // Rooks move horizontally or vertically any number of squares
        return true;  // Placeholder
    }

    private boolean isValidQueenMove(Position from, Position to, Board board) {
        // Queens move diagonally, horizontally, or vertically any number of squares
        return true;  // Placeholder
    }

    private boolean isValidKingMove(Position from, Position to, Board board) {
        // Kings move one square in any direction
        return true;  // Placeholder
    }
}
