package common;

/**
 * Represents a single game session, including all game logic and state management.
 */
public class GameSession {
    private Board board;            // The game board
    private final String playerOne; // Identifier for player one
    private final String playerTwo; // Identifier for player two
    private boolean isWhiteTurn = true; // Track whose turn it is, true for White, false for Black

    /**
     * Constructs a new game session with two players.
     * @param playerOne Identifier for player one.
     * @param playerTwo Identifier for player two.
     */
    public GameSession(String playerOne, String playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.board = new Board();  // Initializes the board with pieces in starting positions
    }

    /**
     * Attempts to make a move on the board.
     * @param from Position where the move starts.
     * @param to Position where the move ends.
     * @param player The identifier of the player making the move.
     * @return true if the move was successful, false otherwise.
     */
    public synchronized boolean makeMove(Position from, Position to, String player) {
        if ((isWhiteTurn && player.equals(playerOne)) || (!isWhiteTurn && player.equals(playerTwo))) {
            if (board.isValidMove(from, to, isWhiteTurn)) {
                board.movePiece(from, to);
                isWhiteTurn = !isWhiteTurn; // Switch turns
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the current state of the board.
     * @return the board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Resets the game board to its initial state.
     */
    public void resetGame() {
        this.board = new Board();
        this.isWhiteTurn = true; // White starts
    }

    /**
     * Provides the state of the game as a string (useful for debugging).
     * @return String representation of the game board.
     */
    @Override
    public String toString() {
        return board.toString(); // Ensure Board has a useful toString() implementation
    }
}
