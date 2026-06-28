import java.util.*;

public class ChessGameEngine {
    private char[][] board;
    private boolean isWhiteTurn;
    private boolean[] castlingRights;
    private int[] enPassantSquare;

    public ChessGameEngine() {
        initializeBoard();
        isWhiteTurn = true;
        castlingRights = new boolean[]{true, true, true, true};
        enPassantSquare = null;
    }

    private void initializeBoard() {
        board = new char[8][8];
        board[0] = new char[]{'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'};
        Arrays.fill(board[1], 'p');
        for (int i = 2; i < 6; i++) {
            Arrays.fill(board[i], ' ');
        }
        Arrays.fill(board[6], 'P');
        board[7] = new char[]{'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'};
    }

    public boolean makeMove(int startRow, int startCol, int endRow, int endCol) {
        if (!isValidMove(startRow, startCol, endRow, endCol)) {
            return false;
        }

        char piece = board[startRow][startCol];
        char capturedPiece = board[endRow][endCol];

        boolean isEnPassant = (Character.toLowerCase(piece) == 'p' && startCol != endCol && capturedPiece == ' ');
        char enPassantTargetPiece = isEnPassant ? board[startRow][endCol] : ' ';

        board[endRow][endCol] = piece;
        board[startRow][startCol] = ' ';

        handleSpecialMoves(piece, capturedPiece, startRow, startCol, endRow, endCol);

        if (isInCheck(isWhiteTurn)) {
            board[startRow][startCol] = piece;
            board[endRow][endCol] = capturedPiece;

            if (isEnPassant) {
                board[startRow][endCol] = enPassantTargetPiece;
            }
            return false;
        }

        if (Character.toLowerCase(piece) == 'p' && Math.abs(endRow - startRow) == 2) {
            int direction = isWhiteTurn ? -1 : 1;
            enPassantSquare = new int[]{startRow + direction, startCol};
        } else {
            enPassantSquare = null;
        }

        isWhiteTurn = !isWhiteTurn;
        return true;
    }

    private boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
        if (!isValidPosition(startRow, startCol) || !isValidPosition(endRow, endCol)) {
            return false;
        }

        char piece = board[startRow][startCol];
        boolean isWhitePiece = Character.isUpperCase(piece);

        if (isWhiteTurn != isWhitePiece) {
            return false;
        }

        return validatePieceMove(piece, startRow, startCol, endRow, endCol);
    }

    private boolean canPieceAttack(int startRow, int startCol, int endRow, int endCol) {
        if (!isValidPosition(startRow, startCol) || !isValidPosition(endRow, endCol)) {
            return false;
        }
        char piece = board[startRow][startCol];
        if (piece == ' ') {
            return false;
        }
        return validatePieceMove(piece, startRow, startCol, endRow, endCol);
    }

    private boolean validateCastlingRights(int startRow, int startCol, int endRow, int endCol) {
        if (isInCheck(isWhiteTurn)) return false;

        boolean isKingSide = endCol > startCol;
        int rookCol = isKingSide ? 7 : 0;

        if (!PieceValidation.isPathClear(board, startRow, startCol, endRow, rookCol)) return false;

        boolean isWhite = (startRow == 7);
        if (isWhite) {
            return isKingSide ? castlingRights[0] : castlingRights[1];
        } else {
            return isKingSide ? castlingRights[2] : castlingRights[3];
        }
    }

    public void promotePawn(int row, int col, char newPiece) {
        board[row][col] = newPiece;
    }

    private boolean validatePieceMove(char piece, int startRow, int startCol, int endRow, int endCol) {
        switch (Character.toLowerCase(piece)) {
            case 'p': return PieceValidation.validatePawnMove(board, piece, startRow, startCol, endRow, endCol, enPassantSquare);
            case 'r': return PieceValidation.validateRookMove(board, startRow, startCol, endRow, endCol);
            case 'n': return PieceValidation.validateKnightMove(board, startRow, startCol, endRow, endCol);
            case 'b': return PieceValidation.validateBishopMove(board, startRow, startCol, endRow, endCol);
            case 'q': return PieceValidation.validateQueenMove(board, startRow, startCol, endRow, endCol);
            case 'k':
                if (Math.abs(endCol - startCol) == 2 && startRow == endRow) {
                    return validateCastlingRights(startRow, startCol, endRow, endCol);
                }
                return PieceValidation.validateKingMove(board, startRow, startCol, endRow, endCol);
            default: return false;
        }
    }

    private void handleSpecialMoves(char piece, char capturedPiece, int startRow, int startCol, int endRow, int endCol) {
        if (Character.toLowerCase(piece) == 'k' && Math.abs(endCol - startCol) == 2) {
            handleCastling(startRow, startCol, endRow, endCol);
        }

        if (Character.toLowerCase(piece) == 'p' && startCol != endCol && capturedPiece == ' ') {
            handleEnPassant(startRow, endRow, endCol);
        }
    }

    private boolean isInCheck(boolean isWhiteKing) {
        int[] kingPos = findKing(isWhiteKing);
        if (kingPos == null) return false;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = board[i][j];
                if (piece != ' ' && isWhitePiece(piece) != isWhiteKing) {
                    if (canPieceAttack(i, j, kingPos[0], kingPos[1])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheckForCurrentPlayer() {
        return isInCheck(isWhiteTurn);
    }

    public boolean isCheckmate() {
        if (!isInCheck(isWhiteTurn)) {
            return false;
        }

        for (int startRow = 0; startRow < 8; startRow++) {
            for (int startCol = 0; startCol < 8; startCol++) {
                char piece = board[startRow][startCol];
                if (piece == ' ' || Character.isUpperCase(piece) != isWhiteTurn) {
                    continue;
                }

                for (int endRow = 0; endRow < 8; endRow++) {
                    for (int endCol = 0; endCol < 8; endCol++) {
                        if (!validatePieceMove(piece, startRow, startCol, endRow, endCol)) {
                            continue;
                        }

                        char captured = board[endRow][endCol];
                        if (captured != ' ' && Character.isUpperCase(captured) == isWhiteTurn) {
                            continue;
                        }

                        board[endRow][endCol] = piece;
                        board[startRow][startCol] = ' ';

                        boolean stillInCheck = isInCheck(isWhiteTurn);

                        board[startRow][startCol] = piece;
                        board[endRow][endCol] = captured;

                        if (!stillInCheck) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private boolean isWhitePiece(char piece) {
        return Character.isUpperCase(piece);
    }

    private void handleCastling(int startRow, int startCol, int endRow, int endCol) {
        SpecialMoves.handleCastling(board, startRow, startCol, endRow, endCol);
    }

    private void handleEnPassant(int startRow, int endRow, int endCol) {
        SpecialMoves.handleEnPassant(board, startRow, endRow, endCol);
    }

    private int[] findKing(boolean isWhiteKing) {
        char kingChar = isWhiteKing ? 'K' : 'k';
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == kingChar) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public char[][] getBoard() {
        return board;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }
}