public class SpecialMoves {
    public static void handleCastling(char[][] board, int startRow, int startCol, int endRow, int endCol) {
        boolean isKingSide = endCol > startCol;

        int rookStartCol = isKingSide ? 7 : 0;
        int rookEndCol = isKingSide ? endCol - 1 : endCol + 1;
        char rook = board[startRow][rookStartCol];

        board[startRow][rookStartCol] = ' ';
        board[startRow][rookEndCol] = rook;
    }

    public static void handleEnPassant(char[][] board, int startRow, int endRow, int endCol) {
        board[startRow][endCol] = ' ';
    }
}