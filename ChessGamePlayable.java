import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

class Piece {
    int xp, yp;
    boolean isWhite;
    String name;
    boolean hasMoved = false;
    LinkedList<Piece> pieceList;

    public Piece(int x, int y, boolean isWhite, String name, LinkedList<Piece> pieceList) {
        this.xp = x;
        this.yp = y;
        this.isWhite = isWhite;
        this.name = name;
        this.pieceList = pieceList;
        this.pieceList.add(this);
    }
}

public class ChessGame extends JFrame {
    private LinkedList<Piece> pieces = new LinkedList<>();
    private Image[] imgs = new Image[12];
    private Piece selectedPiece = null;
    private boolean whiteTurn = true;

    public ChessGame() throws IOException {
        loadImages();
        initializePieces();
        setupFrame();
    }

    private void loadImages() throws IOException {
        BufferedImage all = ImageIO.read(new File("C:\\chess.png"));
        int ind = 0;
        for (int y = 0; y < 400; y += 200) {
            for (int x = 0; x < 1200; x += 200) {
                imgs[ind] = all.getSubimage(x, y, 200, 200).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH);
                ind++;
            }
        }
    }

    private void initializePieces() {
        // Initialize black pieces
        for (int i = 0; i < 8; i++) {
            new Piece(i, 1, false, "pawn", pieces);
        }
        new Piece(0, 0, false, "rook", pieces);
        new Piece(7, 0, false, "rook", pieces);
        new Piece(1, 0, false, "knight", pieces);
        new Piece(6, 0, false, "knight", pieces);
        new Piece(2, 0, false, "bishop", pieces);
        new Piece(5, 0, false, "bishop", pieces);
        new Piece(3, 0, false, "queen", pieces);
        new Piece(4, 0, false, "king", pieces);

        // Initialize white pieces
        for (int i = 0; i < 8; i++) {
            new Piece(i, 6, true, "pawn", pieces);
        }
        new Piece(0, 7, true, "rook", pieces);
        new Piece(7, 7, true, "rook", pieces);
        new Piece(1, 7, true, "knight", pieces);
        new Piece(6, 7, true, "knight", pieces);
        new Piece(2, 7, true, "bishop", pieces);
        new Piece(5, 7, true, "bishop", pieces);
        new Piece(3, 7, true, "queen", pieces);
        new Piece(4, 7, true, "king", pieces);
    }

    private void setupFrame() {
        setTitle("Chess Game");
        setSize(512, 512);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);

        JPanel boardPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                boolean white = true;
                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        g.setColor(white ? new Color(235, 235, 208) : new Color(119, 148, 85));
                        g.fillRect(x * 64, y * 64, 64, 64);
                        white = !white;
                    }
                    white = !white;
                }
                for (Piece p : pieces) {
                    if (p != null) {
                        g.drawImage(imgs[getImageIndex(p)], p.xp * 64, p.yp * 64, this);
                    }
                }
            }

            private int getImageIndex(Piece p) {
                int base = p.isWhite ? 0 : 6;
                switch (p.name) {
                    case "king":   return base;
                    case "queen":  return base + 1;
                    case "bishop": return base + 2;
                    case "knight": return base + 3;
                    case "rook":   return base + 4;
                    case "pawn":   return base + 5;
                    default:       return -1;
                }
            }
        };

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / 64;
                int y = e.getY() / 64;
                if (selectedPiece != null && selectedPiece.xp == x && selectedPiece.yp == y) {
                    System.out.println("Deselected " + selectedPiece.name);
                    selectedPiece = null;
                    repaint();
                    return;
                }
                if (selectedPiece == null) {
                    for (Piece p : pieces) {
                        if (p.xp == x && p.yp == y && p.isWhite == whiteTurn) {
                            selectedPiece = p;
                            System.out.println("Selected " + (p.isWhite ? "White" : "Black") + " " + p.name);
                            break;
                        }
                    }
                } else {
                    System.out.println("Attempting to move " + selectedPiece.name + " from (" + selectedPiece.xp + ", " + selectedPiece.yp + ") to (" + x + ", " + y + ")");
                    if (movePiece(selectedPiece, x, y)) {
                        selectedPiece = null;
                    } else {
                        System.out.println("Move failed or invalid. Still " + (whiteTurn ? "White's" : "Black's") + " turn.");
                    }
                    repaint();
                }
            }
        });


        add(boardPanel);
        setVisible(true);
    }

    private boolean movePiece(Piece p, int newX, int newY) {
        if (isValidMove(p, newX, newY)) {
            Piece other = getPieceAt(newX, newY);
            if (other != null) {
                pieces.remove(other); // Capture the piece
                System.out.println(p.name + " captured " + other.name + " at (" + newX + ", " + newY + ")");
            }if (p.name.equals("king") && Math.abs(newX - p.xp) == 2) {
                performCastling(p, newX, newY); // Handle both king and rook movement
            } else {
                p.xp = newX;
                p.yp = newY;
                p.hasMoved = true;
            }
 {
                p.xp = newX;
                p.yp = newY;
                p.hasMoved = true;
                System.out.println(p.name + " moved successfully to (" + newX + ", " + newY + ")");
            }

            // Pawn promotion
            if (p.name.equals("pawn") && (newY == 0 || newY == 7)) {
                promotePawn(p);
            }

            whiteTurn = !whiteTurn; // Toggle turn after a successful move
            System.out.println("Now it is " + (whiteTurn ? "White's" : "Black's") + " turn.");
            return true;
        }
        System.out.println("Move invalid: " + p.name + " cannot move to (" + newX + ", " + newY + ")");
        return false;
    }


    private void promotePawn(Piece pawn) {
        String[] options = new String[]{"Queen", "Rook", "Bishop", "Knight"};
        int response = JOptionPane.showOptionDialog(null, "Choose piece for promotion:", "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        if (response >= 0 && response < options.length) {
            pawn.name = options[response].toLowerCase();
            System.out.println("Pawn promoted to " + pawn.name.toUpperCase());
        }
    }

    private Piece getPieceAt(int x, int y) {
        for (Piece piece : pieces) {
            if (piece.xp == x && piece.yp == y) {
                return piece;
            }
        }
        return null;
    }

    private boolean isValidMove(Piece piece, int newX, int newY) {
        if (getPieceAt(newX, newY) != null && getPieceAt(newX, newY).isWhite == piece.isWhite) {
            return false; // Cannot capture your own piece
        }
        switch (piece.name.toLowerCase()) {
            case "pawn":
                return isValidPawnMove(piece, newX, newY);
            case "rook":
                return isValidRookMove(piece, newX, newY);
            case "knight":
                return isValidKnightMove(piece, newX, newY);
            case "bishop":
                return isValidBishopMove(piece, newX, newY);
            case "queen":
                return isValidQueenMove(piece, newX, newY);
            case "king":
                return isValidKingMove(piece, newX, newY);
            default:
                return false;
        }
    }

    private boolean isValidPawnMove(Piece pawn, int newX, int newY) {
        int direction = pawn.isWhite ? -1 : 1;
        if (newX == pawn.xp && newY == pawn.yp + direction && getPieceAt(newX, newY) == null) {
            return true; // Normal move
        }
        if (newX == pawn.xp && newY == pawn.yp + 2 * direction && pawn.yp == (pawn.isWhite ? 6 : 1) && getPieceAt(newX, newY) == null && getPieceAt(newX, pawn.yp + direction) == null) {
            return true; // Initial double move
        }
        if (Math.abs(newX - pawn.xp) == 1 && newY == pawn.yp + direction && getPieceAt(newX, newY) != null && getPieceAt(newX, newY).isWhite != pawn.isWhite) {
            return true; // Capture move
        }
        return false;
    }

    private boolean isValidRookMove(Piece rook, int newX, int newY) {
        if (rook.xp != newX && rook.yp != newY) {
            return false; // Rooks move in straight lines
        }
        return isPathClear(rook.xp, rook.yp, newX, newY);
    }

    private boolean isValidKnightMove(Piece knight, int newX, int newY) {
        int dx = Math.abs(newX - knight.xp);
        int dy = Math.abs(newY - knight.yp);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2); // Knights move in "L" shapes
    }

    private boolean isValidBishopMove(Piece bishop, int newX, int newY) {
        if (Math.abs(newX - bishop.xp) != Math.abs(newY - bishop.yp)) {
            return false; // Bishops move diagonally
        }
        return isPathClear(bishop.xp, bishop.yp, newX, newY);
    }

    private boolean isValidQueenMove(Piece queen, int newX, int newY) {
        if (queen.xp != newX && queen.yp != newY && Math.abs(newX - queen.xp) != Math.abs(newY - queen.yp)) {
            return false; // Queens move straight or diagonally
        }
        return isPathClear(queen.xp, queen.yp, newX, newY);
    }

    private boolean isValidKingMove(Piece king, int newX, int newY) {
        int dx = Math.abs(newX - king.xp);
        int dy = Math.abs(newY - king.yp);
        if (dx <= 1 && dy <= 1) {
            return true; // Normal king move
        }
        // Castling check
        if (dx == 2 && dy == 0 && !king.hasMoved && !isInCheck(king.isWhite)) {
            return checkCastling(king, newX, newY); // Check if castling is possible
        }
        return false;
    }
    private boolean checkCastling(Piece king, int newX, int newY) {
        int direction = (newX > king.xp) ? 1 : -1;
        int rookX = (direction == 1) ? 7 : 0;
        Piece rook = getPieceAt(rookX, king.yp);
        if (rook != null && !rook.hasMoved && isPathClear(king.xp, king.yp, rookX, king.yp)) {
            return true; // Castling is possible, actual move will be handled elsewhere
        }
        return false;
    }


    private boolean isPathClear(int startX, int startY, int endX, int endY) {
        int dx = Integer.compare(endX, startX);
        int dy = Integer.compare(endY, startY);
        int x = startX + dx;
        int y = startY + dy;
        while (x != endX || y != endY) {
            if (getPieceAt(x, y) != null) {
                return false;
            }
            x += dx;
            y += dy;
        }
        return true;
    }
    private void performCastling(Piece king, int newX, int newY) {
        int direction = (newX > king.xp) ? 1 : -1;
        int rookOrigX = (direction == 1) ? 7 : 0;
        int rookNewX = king.xp + direction;

        Piece rook = getPieceAt(rookOrigX, king.yp);
        if (rook != null && !rook.hasMoved && isPathClear(king.xp, king.yp, rookOrigX, king.yp)) {
            // Execute moves
            king.xp = newX;
            king.hasMoved = true;
            rook.xp = rookNewX;
            rook.hasMoved = true;
        }
    }

    private boolean isInCheck(boolean isWhite) {
        // Placeholder: Implement check detection logic
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ChessGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
