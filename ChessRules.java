import java.util.ArrayList;
import java.util.List;

public class ChessRules {
    private char[][] board;
    private static boolean whiteTurn;
    private boolean whiteKingMoved;
    private boolean blackKingMoved;
    private boolean whiteLeftRookMoved;
    private boolean whiteRightRookMoved;
    private boolean blackLeftRookMoved;
    private boolean blackRightRookMoved;
    private int[] enPassantSquare;
    private boolean kingInCheck;
    public String name;

    public ChessRules(boolean startingTurn) {
        initializeBoard();
        whiteTurn = startingTurn; // White starts first
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteLeftRookMoved = false;
        whiteRightRookMoved = false;
        blackLeftRookMoved = false;
        blackRightRookMoved = false;
        kingInCheck = false;
        enPassantSquare = new int[]{-1, -1};
    }
    public ChessRules(String name) {
        initializeBoard();
        whiteTurn = false; // White starts first
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteLeftRookMoved = false;
        whiteRightRookMoved = false;
        blackLeftRookMoved = false;
        blackRightRookMoved = false;
        kingInCheck = false;
        enPassantSquare = new int[]{-1, -1};
    }


    private void initializeBoard() {
        // Initialize the chessboard
        board = new char[][]{
                {'0', '1', '2', '3', '4', '5', '6', '7', '8'},
                {'1', 'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
                {'2', 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {'3', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'4', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'5', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'6', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'7', 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'8', 'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };
    }

    public void printBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
    public void setName(String newName){
        this.name = newName;
    }    
    public static  void setWhiteTurn(boolean turn){
        whiteTurn =turn;
    }
    public String returnBoard(){
        String gameBoard ="";
        for (int i = 0; i < 9; i++) {
            if(i>0){
                gameBoard = gameBoard+"\n";
            }
            for (int j = 0; j < 9; j++) {
                gameBoard = gameBoard+(board[i][j] + " ");
            }
            
        }
        return gameBoard;
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
        return x > 0 && x < 9 && y > 0 && y < 9;
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
            if (startX == (Character.isUpperCase(piece) ? 7 : 2) && startX + 2 * direction == endX && board[startX + direction][startY] == ' ' && board[endX][endY] == ' ') {
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
                if (startX == 1 && startY == 1) {
                    whiteLeftRookMoved = true;
                } else if (startX == 1 && startY == 8) {
                    whiteRightRookMoved = true;
                } else if (startX == 8 && startY == 1) {
                    blackLeftRookMoved = true;
                } else if (startX == 8 && startY == 8) {
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
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
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
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 1; j++) {
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
                    for (int i = 1; i < 9; i++) {
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
                return !whiteKingMoved && !whiteRightRookMoved && board[1][6] == ' ' && board[1][7] == ' ';
            } else {
                return !whiteKingMoved && !whiteLeftRookMoved && board[1][2] == ' ' && board[1][7] == ' ' && board[1][4] == ' ';
            }
        } else {
            if (kingSide) {
                return !blackKingMoved && !blackRightRookMoved && board[8][6] == ' ' && board[8][7] == ' ';
            } else {
                return !blackKingMoved && !blackLeftRookMoved && board[8][2] == ' ' && board[8][3] == ' ' && board[8][4] == ' ';
            }
        }
    }

    public void castle(boolean isWhite, boolean kingSide) {
        if (canCastle(isWhite, kingSide)) {
            if (isWhite) {
                if (kingSide) {
                    board[1][7] = 'K';
                    board[1][5] = ' ';
                    board[1][6] = 'R';
                    board[1][8] = ' ';
                    whiteKingMoved = true;
                    whiteRightRookMoved = true;
                } else {
                    board[1][3] = 'K';
                    board[1][5] = ' ';
                    board[1][4] = 'R';
                    board[1][1] = ' ';
                    whiteKingMoved = true;
                    whiteLeftRookMoved = true;
                }
            } else {
                if (kingSide) {
                    board[8][7] = 'k';
                    board[8][5] = ' ';
                    board[8][6] = 'r';
                    board[8][8] = ' ';
                    blackKingMoved = true;
                    blackRightRookMoved = true;
                } else {
                    board[8][3] = 'k';
                    board[8][5] = ' ';
                    board[8][4] = 'r';
                    board[8][1] = ' ';
                    blackKingMoved = true;
                    blackLeftRookMoved = true;
                }
            }
            whiteTurn = !whiteTurn;
            enPassantSquare[0] = -1;
            enPassantSquare[1] = -1;
        }
    }
    public String onWhiteTurn(ChessRules game,int sX,int sY, int eX, int eY){
        System.out.println("/n");
        int startX = sY;
        int startY = sX;
        int endX = eY;
        int endY = eX;
        String moveDetail = "";
        if (game.isValidMove(startX, startY, endX, endY)) {
            game.makeMove(startX, startY, endX, endY);
            whiteTurn = false;
            kingInCheck = game.isCheckmate();
            if(isCheckmate()==true){
                moveDetail ="Game Over white is victorious\n" + game.returnBoard();
            }else if(isCheckmate()==false){
                moveDetail ="\n"+"Move successful!"+"\n"+game.returnBoard();
            }
        }
        else{
            moveDetail ="\n Invalid move!\n"+game.returnBoard();
        }
        return moveDetail;
    }

    public String onBlackTurn(ChessRules game,int sX,int sY, int eX, int eY){
        System.out.println("\n");
        int startX = sY;
        int startY = sX;
        int endX = eY;
        int endY = eX;
        String moveDetail = "";
        if (game.isValidMove(startX, startY, endX, endY)) {
            game.makeMove(startX, startY, endX, endY);
            whiteTurn = true;//update whos turn it is
            kingInCheck = game.isCheckmate();//update if its checkmate
            if(isCheckmate()==true){
                moveDetail ="Game Over black is victorious\n" + game.returnBoard();
            }else if(isCheckmate()==false){
                moveDetail ="\n"+"Move successful!"+"\n"+game.returnBoard();
            }
        }
        else{
            moveDetail ="\n"+"Invalid move!\n"+game.returnBoard();
        }
        return moveDetail;
    }

    public boolean matchUpdate(){
       boolean update = whiteTurn;
        return update;
    }

    public static void main(String[] args) {
        //ChessRules game = new ChessRules(true);
       /* game.printBoard();
        System.out.println(game.onWhiteTurn(game,3,7,3,5));
        System.out.println(game.onBlackTurn(game,3,2,3,3));
        System.out.println(game.onWhiteTurn(game,4,7,4,6));
        System.out.println(game.onBlackTurn(game,4,1,1,4));
        System.out.println(game.onWhiteTurn(game,5,8,4,7));
       // System.out.println(game.onBlackTurn(game,1,4,4,7));*/
       

    }
}