import javax.swing.*;
import java.awt.*;

public class ChessGameUI extends JFrame {
    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_SIZE = 80;
    private JPanel boardPanel;
    private JButton[][] squares;
    private ChessGameEngine gameEngine;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private JLabel statusLabel;
    public static final Color very_light_yellow = new Color(255, 255, 204);
    public static final Color light_brown = new Color(153, 102, 0);

    public ChessGameUI() {
        gameEngine = new ChessGameEngine();
        initializeUI();
    }

    private String getChessSymbol(char piece) {
        switch (piece) {
            case 'K': return "\u2654";
            case 'Q': return "\u2655";
            case 'R': return "\u2656";
            case 'B': return "\u2657";
            case 'N': return "\u2658";
            case 'P': return "\u2659";
            case 'k': return "\u265A";
            case 'q': return "\u265B";
            case 'r': return "\u265C";
            case 'b': return "\u265D";
            case 'n': return "\u265E";
            case 'p': return "\u265F";
            default: return "";
        }
    }

    private void initializeUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        squares = new JButton[BOARD_SIZE][BOARD_SIZE];

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                squares[row][col] = new JButton();
                squares[row][col].setPreferredSize(new Dimension(SQUARE_SIZE, SQUARE_SIZE));
                squares[row][col].setBackground((row + col) % 2 == 0 ? very_light_yellow : light_brown);
                final int finalRow = row;
                final int finalCol = col;
                squares[row][col].addActionListener(e -> handleSquareClick(finalRow, finalCol));
                boardPanel.add(squares[row][col]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("White's turn");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        updateBoardDisplay();

        pack();
        setLocationRelativeTo(null);
    }

    private void handleSquareClick(int row, int col) {
        if (selectedRow == -1) {
            char piece = gameEngine.getBoard()[row][col];
            if (piece != ' ' && (Character.isUpperCase(piece) == gameEngine.isWhiteTurn())) {
                selectedRow = row;
                selectedCol = col;
                squares[row][col].setBackground(Color.YELLOW);
            }
        } else {
            boolean moveSuccess = gameEngine.makeMove(selectedRow, selectedCol, row, col);

            if (moveSuccess) {
                char pieceMoved = gameEngine.getBoard()[row][col];
                if (Character.toLowerCase(pieceMoved) == 'p' && (row == 0 || row == 7)) {
                    String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                    int choice = JOptionPane.showOptionDialog(this,
                        "Choose piece for promotion:", "Pawn Promotion",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);

                    boolean isWhite = (row == 0);
                    char promotedPiece = isWhite ? 'Q' : 'q';

                    if (choice == 1) promotedPiece = isWhite ? 'R' : 'r';
                    else if (choice == 2) promotedPiece = isWhite ? 'B' : 'b';
                    else if (choice == 3) promotedPiece = isWhite ? 'N' : 'n';

                    gameEngine.promotePawn(row, col, promotedPiece);
                }
            }

            squares[selectedRow][selectedCol].setBackground(
                (selectedRow + selectedCol) % 2 == 0 ? very_light_yellow : light_brown
            );
            selectedRow = -1;
            selectedCol = -1;

            if (moveSuccess) {
                updateBoardDisplay();
                updateStatus();
            }
        }
    }

    private void updateStatus() {
        String turnText = gameEngine.isWhiteTurn() ? "White" : "Black";

        if (gameEngine.isCheckmate()) {
            String winner = gameEngine.isWhiteTurn() ? "Black" : "White";
            statusLabel.setText("Checkmate! " + winner + " wins!");
        } else if (gameEngine.isInCheckForCurrentPlayer()) {
            statusLabel.setText(turnText + "'s turn - CHECK!");
        } else {
            statusLabel.setText(turnText + "'s turn");
        }
    }

    private void updateBoardDisplay() {
        char[][] board = gameEngine.getBoard();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                char piece = board[row][col];
                squares[row][col].setText(getChessSymbol(piece));

                if (Character.isUpperCase(piece)) {
                    squares[row][col].setForeground(Color.BLACK);
                } else {
                    squares[row][col].setForeground(new Color(50, 50, 50));
                }
                squares[row][col].setFont(new Font("SansSerif", Font.PLAIN, 40));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGameUI ui = new ChessGameUI();
            ui.setVisible(true);
        });
    }
}