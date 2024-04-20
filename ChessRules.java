import java.util.ArrayList;
import java.util.List;

public class ChessGame {
    private char[][] board;
    private boolean whiteTurn;
    private boolean whiteKingMoved;
    private boolean blackKingMoved;
    private boolean whiteLeftRookMoved;
    private boolean whiteRightRookMoved;
    private boolean blackLeftRookMoved;
    private boolean blackRightRookMoved;
    private int[] enPassantSquare;

    public ChessGame() {
        initializeBoard();
        whiteTurn = true; // White starts first
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteLeftRookMoved = false;
        whiteRightRookMoved = false;
        blackLeftRookMoved = false;
        blackRightRookMoved = false;
        enPassantSquare = new int[]{-1, -1};
    }

    private void initializeBoard() {
        // Initialize the chessboard
        board = new char[][]{
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public boolean isValidMove(int startX, int startY, int endX, int endY) {
        // Check if the start and end positions are within the board boundaries
        if (!isValidPosition(startX, startY) || !isValidPosition(endX, endY)) {
            return false;
        }

        // Check if the start position contains a piece
        char piece = board[startX][startY];
        if (piece == ' ') {
            return false;
        }

        // Check if it's the piece's turn to move
        boolean isWhitePiece = Character.isUpperCase(piece);
        if (isWhitePiece != whiteTurn) {
            return false;
        }

        // Check specific piece movements
        switch (Character.toLowerCase(piece)) {
            case 'p':
                return isValidPawnMove(startX, startY, endX, endY);
            case 'r':
                return isValidRookMove(startX, startY, endX, endY);
            case 'n':
                return isValidKnightMove(startX, startY, endX, endY);
            case 'b':
                return isValidBishopMove(startX, startY, endX, endY);
            case 'q':
                return isValidQueenMove(startX, startY, endX, endY);
            case 'k':
                return isValidKingMove(startX, startY, endX, endY);
            default:
                return false;
        }
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    private boolean isValidPawnMove(int startX, int startY, int endX, int endY) {
        char piece = board[startX][startY];
        int direction = Character.isUpperCase(piece) ? -1 : 1; // White pawns move upwards, black pawns downwards

        // Normal move
        if (startY == endY) {
            if (startX + direction == endX && board[endX][endY] == ' ') {
                return true;
            }
            // Double move from starting position
            if (startX == (Character.isUpperCase(piece) ? 6 : 1) && startX + 2 * direction == endX && board[startX + direction][startY] == ' ' && board[endX][endY] == ' ') {
                return true;
            }
        }
        // Capture move
        else if (Math.abs(startY - endY) == 1 && startX + direction == endX) {
            char targetPiece = board[endX][endY];
            return targetPiece != ' ' && Character.isUpperCase(piece) != Character.isUpperCase(targetPiece);
        }
        // En passant
        else if (startX == enPassantSquare[0] && endX == enPassantSquare[0] + direction && Math.abs(endY - enPassantSquare[1]) == 1) {
            char targetPiece = board[endX][endY];
            return targetPiece == ' ' && board[startX][endY] != ' ';
        }
        return false;
    }

    private boolean isValidRookMove(int startX, int startY, int endX, int endY) {
        // Rook moves horizontally or vertically
        return startX == endX || startY == endY;
    }

    private boolean isValidKnightMove(int startX, int startY, int endX, int endY) {
        int dx = Math.abs(startX - endX);
        int dy = Math.abs(startY - endY);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    private boolean isValidBishopMove(int startX, int startY, int endX, int endY) {
        int dx = Math.abs(startX - endX);
        int dy = Math.abs(startY - endY);
        return dx == dy;
    }

    private boolean isValidQueenMove(int startX, int startY, int endX, int endY) {
        return isValidRookMove(startX, startY, endX, endY) || isValidBishopMove(startX, startY, endX, endY);
    }

    private boolean isValidKingMove(int startX, int startY, int endX, int endY) {
        int dx = Math.abs(startX - endX);
        int dy = Math.abs(startY - endY);
        return (dx <= 1 && dy <= 1);
    }

    public void makeMove(int startX, int startY, int endX, int endY) {
        if (isValidMove(startX, startY, endX, endY)) {
            char piece = board[startX][startY];
            board[endX][endY] = piece;
            board[startX][startY] = ' ';
            whiteTurn = !whiteTurn;

            // Update king and rook moved flags for castling
            if (Character.toLowerCase(piece) == 'k') {
                whiteKingMoved = true;
            } else if (Character.toLowerCase(piece) == 'r') {
                if (startX == 0 && startY == 0) {
                    whiteLeftRookMoved = true;
                } else if (startX == 0 && startY == 7) {
                    whiteRightRookMoved = true;
                } else if (startX == 7 && startY == 0) {
                    blackLeftRookMoved = true;
                } else if (startX == 7 && startY == 7) {
                    blackRightRookMoved = true;
                }
            }

            // Update en passant square
            if (Math.abs(startX - endX) == 2 && Character.toLowerCase(piece) == 'p') {
                enPassantSquare[0] = (startX + endX) / 2;
                enPassantSquare[1] = startY;
            } else {
                enPassantSquare[0] = -1;
                enPassantSquare[1] = -1;
            }
        }
    }

    public boolean isCheckmate() {
        char king = whiteTurn ? 'K' : 'k';

        // Check if the king is in check
        int[] kingPosition = findKingPosition(whiteTurn);
        if (isUnderAttack(kingPosition[0], kingPosition[1], !whiteTurn)) {
            // Check if the king has no legal moves
            if (!hasLegalMoves(kingPosition[0], kingPosition[1])) {
                return true; // Checkmate
            }
        }
        return false; // Not checkmate
    }

    private int[] findKingPosition(boolean isWhite) {
        char king = isWhite ? 'K' : 'k';
        int[] position = new int[2];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == king) {
                    position[0] = i;
                    position[1] = j;
                    return position;
                }
            }
        }
        return null;
    }

    private boolean hasLegalMoves(int x, int y) {
        char piece = board[x][y];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(x, y, i, j)) {
                    char targetPiece = board[i][j];
                    makeMove(x, y, i, j);
                    boolean inCheck = isUnderAttack(findKingPosition(whiteTurn)[0], findKingPosition(whiteTurn)[1], !whiteTurn);
                    undoMove(x, y, i, j, targetPiece);
                    if (!inCheck) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void undoMove(int startX, int startY, int endX, int endY, char capturedPiece) {
        board[startX][startY] = board[endX][endY];
        board[endX][endY] = capturedPiece;
        whiteTurn = !whiteTurn;
    }

    private boolean isUnderAttack(int x, int y, boolean attackerIsWhite) {
        char attackerKing = attackerIsWhite ? 'K' : 'k';
        char attackerQueen = attackerIsWhite ? 'Q' : 'q';
        char attackerRook = attackerIsWhite ? 'R' : 'r';
        char attackerBishop = attackerIsWhite ? 'B' : 'b';
        char attackerKnight = attackerIsWhite ? 'N' : 'n';
        char attackerPawn = attackerIsWhite ? 'P' : 'p';

        // Check for attacks from queens, rooks, and bishops
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    for (int i = 1; i < 8; i++) {
                        int newX = x + i * dx;
                        int newY = y + i * dy;
                        if (!isValidPosition(newX, newY)) {
                            break;
                        }
                        char piece = board[newX][newY];
                        if (piece == ' ') {
                            continue;
                        }
                        if ((dx == 0 || dy == 0) && (piece == attackerQueen || piece == attackerRook)) {
                            return true;
                        }
                        if ((dx != 0 && dy != 0) && (piece == attackerQueen || piece == attackerBishop)) {
                            return true;
                        }
                        break;
                    }
                }
            }
        }

        // Check for attacks from knights
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] move : knightMoves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (isValidPosition(newX, newY) && (board[newX][newY] == attackerKnight)) {
                return true;
            }
        }

        // Check for attacks from pawns
        int pawnDirection = attackerIsWhite ? -1 : 1;
        int[][] pawnMoves = {{pawnDirection, -1}, {pawnDirection, 1}};
        for (int[] move : pawnMoves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (isValidPosition(newX, newY) && (board[newX][newY] == attackerPawn)) {
                return true;
            }
        }

        // Check for attacks from opposing king
        int[][] kingMoves = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        for (int[] move : kingMoves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (isValidPosition(newX, newY) && (board[newX][newY] == attackerKing)) {
                return true;
            }
        }

        return false;
    }

    public boolean canCastle(boolean isWhite, boolean kingSide) {
        if (isWhite) {
            if (kingSide) {
                return !whiteKingMoved && !whiteRightRookMoved && board[0][5] == ' ' && board[0][6] == ' ';
            } else {
                return !whiteKingMoved && !whiteLeftRookMoved && board[0][1] == ' ' && board[0][2] == ' ' && board[0][3] == ' ';
            }
        } else {
            if (kingSide) {
                return !blackKingMoved && !blackRightRookMoved && board[7][5] == ' ' && board[7][6] == ' ';
            } else {
                return !blackKingMoved && !blackLeftRookMoved && board[7][1] == ' ' && board[7][2] == ' ' && board[7][3] == ' ';
            }
        }
    }

    public void castle(boolean isWhite, boolean kingSide) {
        if (canCastle(isWhite, kingSide)) {
            if (isWhite) {
                if (kingSide) {
                    board[0][6] = 'K';
                    board[0][4] = ' ';
                    board[0][5] = 'R';
                    board[0][7] = ' ';
                    whiteKingMoved = true;
                    whiteRightRookMoved = true;
                } else {
                    board[0][2] = 'K';
                    board[0][4] = ' ';
                    board[0][3] = 'R';
                    board[0][0] = ' ';
                    whiteKingMoved = true;
                    whiteLeftRookMoved = true;
                }
            } else {
                if (kingSide) {
                    board[7][6] = 'k';
                    board[7][4] = ' ';
                    board[7][5] = 'r';
                    board[7][7] = ' ';
                    blackKingMoved = true;
                    blackRightRookMoved = true;
                } else {
                    board[7][2] = 'k';
                    board[7][4] = ' ';
                    board[7][3] = 'r';
                    board[7][0] = ' ';
                    blackKingMoved = true;
                    blackLeftRookMoved = true;
                }
            }
            whiteTurn = !whiteTurn;
            enPassantSquare[0] = -1;
            enPassantSquare[1] = -1;
        }
    }

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        game.printBoard();

        // Example move
        int startX = 6;
        int startY = 4;
        int endX = 4;
        int endY = 4;

        if (game.isValidMove(startX, startY, endX, endY)) {
            game.makeMove(startX, startY, endX, endY);
            System.out.println("Move successful!");
        } else {
            System.out.println("Invalid move!");
        }

        game.printBoard();
    }
}
