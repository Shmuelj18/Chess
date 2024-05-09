package common;

/**
 * Represents a position on the chessboard.
 */
public class Position {
    private int x; // Column on the board, 0-7 corresponding to 'a'-'h'
    private int y; // Row on the board, 0-7 corresponding to '1'-'8'

    /**
     * Constructs a Position based on algebraic chess notation.
     * @param pos String notation of the position (e.g., "e2").
     */
    public Position(String pos) {
        if (pos == null || pos.length() != 2) {
            throw new IllegalArgumentException("Invalid position format");
        }
        this.x = pos.charAt(0) - 'a'; // Convert 'a' to 'h' into 0 to 7
        this.y = Character.getNumericValue(pos.charAt(1)) - 1; // Convert '1' to '8' into 0 to 7
    }

    /**
     * Constructs a Position with x and y coordinates.
     * @param x the horizontal position (0-7, corresponding to columns 'a'-'h')
     * @param y the vertical position (0-7, corresponding to rows '1'-'8')
     */
    public Position(int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) {
            throw new IllegalArgumentException("Coordinates out of bounds");
        }
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Checks if the position is within the bounds of a standard chessboard.
     * @return true if the position is within the valid chessboard bounds.
     */
    public boolean isInBounds() {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y; // Simple hash code implementation based on coordinates
    }

    @Override
    public String toString() {
        return "" + (char) ('a' + x) + (y + 1);
    }
}
