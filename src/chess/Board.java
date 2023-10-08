package chess;

import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;
import chess.interfaces.ChessPiece;
import chess.pieces.*;

import java.util.HashMap;
import java.util.Map;

public class Board implements chess.interfaces.ChessBoard {
    private Map<Integer, Piece> pieces = new HashMap<>(); // Stores pieces according to the hashCode of the position on the board

    @Override
    public void addPiece(ChessPosition pos, ChessPiece p) {
        Position position = (Position) pos;
        Piece piece = (Piece) p;
        pieces.put(position.hashCode(), piece);
    }
    public void addPiece(int hashCode, ChessPiece p) {
        Piece piece = (Piece) p;
        pieces.put(hashCode, piece);
    }

    @Override
    public Piece getPiece(ChessPosition pos) {
        Position position = (Position) pos;
        return pieces.get(position.hashCode());
    }
    public Piece getPiece(int hashCode) {
        return pieces.get(hashCode);
    }

    @Override
    public void resetBoard() {
        // clear pieces maps
        pieces.clear();

        // set row 1 (white)
        addPiece(getHashFromRowAndCol(1, 1), new Rook(ChessGame.TeamColor.WHITE));
        addPiece(getHashFromRowAndCol(1, 2), new Knight(ChessGame.TeamColor.WHITE));
        addPiece(getHashFromRowAndCol(1, 3), new Bishop(ChessGame.TeamColor.WHITE));
        addPiece(getHashFromRowAndCol(1, 4), new Queen(ChessGame.TeamColor.WHITE));
        addPiece(getHashFromRowAndCol(1, 5), new King(ChessGame.TeamColor.WHITE));
        addPiece(getHashFromRowAndCol(1, 6), new Bishop(ChessGame.TeamColor.WHITE));
        addPiece(getHashFromRowAndCol(1, 7), new Knight(ChessGame.TeamColor.WHITE));
        addPiece(getHashFromRowAndCol(1, 8), new Rook(ChessGame.TeamColor.WHITE));

        // set row 2 (white pawns)
        for(int i = 1; i <= 8; i++) {
            addPiece(getHashFromRowAndCol(2, i), new Pawn(ChessGame.TeamColor.WHITE));
        }

        // set row 7 (black pawns)
        for(int i = 1; i <= 8; i++) {
            addPiece(getHashFromRowAndCol(7, i), new Pawn(ChessGame.TeamColor.BLACK));
        }

        // set row 8 (black)
        addPiece(getHashFromRowAndCol(8, 1), new Rook(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 2), new Knight(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 3), new Bishop(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 4), new Queen(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 5), new King(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 6), new Bishop(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 7), new Knight(ChessGame.TeamColor.BLACK));
        addPiece(getHashFromRowAndCol(8, 8), new Rook(ChessGame.TeamColor.BLACK));
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        // variables that format the color of the console output (for tiles)
        final String ANSI_RESET = "\u001B[0m"; // to reset console output
        final String ANSI_BLACK_BG = "\u001B[40m";

        // starting from row 8 and going down (following board notation)
        for(int row = 8; row >= 1; row--) {
            output.append(ANSI_RESET);
            for(int col = 1; col <= 8; col++) {
                if(row % 2 == 0 && col % 2 == 0 || row % 2 == 1 && col % 2 == 1) {
                    output.append(ANSI_RESET); // light colored tile
                }
                else {
                    output.append(ANSI_BLACK_BG); // dark colored tile
                }

                Piece current = getPiece(getHashFromRowAndCol(row, col));
                if(current != null){
                    output.append(current.getPieceAsUnicode());
                }
                else {
                    output.append(" \u2003 "); // whitespace
                }

                output.append(ANSI_RESET);
            }
            output.append("\n");
        }
        return output.toString();
    }

    public void movePiece(ChessMove move) {
        // this method doesn't check any logic, just does the physical movement of pieces
        // (assumes that Game has already checked logic)
        Position startPos = (Position) move.getStartPosition();
        Position endPos = (Position) move.getEndPosition();
        Piece movingPiece = getPiece(startPos);
        Piece pieceAtEnd = getPiece(endPos);
        ChessPiece.PieceType promotionType = move.getPromotionPiece();

        if(promotionType != null){
            if(movingPiece.getPieceType() != promotionType){
                movingPiece = switch(promotionType) {
                    case KING -> new King(movingPiece.getTeamColor());
                    case QUEEN -> new Queen(movingPiece.getTeamColor());
                    case BISHOP -> new Bishop(movingPiece.getTeamColor());
                    case KNIGHT -> new Knight(movingPiece.getTeamColor());
                    case ROOK -> new Rook(movingPiece.getTeamColor());
                    case PAWN -> new Pawn(movingPiece.getTeamColor());
                };
            }
        }

        if(pieceAtEnd == null) {
            pieces.remove(startPos.hashCode());
            pieces.put(endPos.hashCode(), movingPiece);
        }
        else {
            pieces.remove(startPos.hashCode());
            pieces.replace(endPos.hashCode(), movingPiece);
        }
    }

    public Map<Integer, Piece> getPieces() {
        return pieces;
    }

    private int getHashFromRowAndCol(int row, int col) {
        return (row*10) + col;
    }
}