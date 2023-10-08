package chess.pieces;

import chess.interfaces.ChessGame;

public abstract class Piece implements chess.interfaces.ChessPiece {
    // the Piece class is abstract, the specific Pawn, Rook, Bishop, etc. classes need to be instantiated

    ChessGame.TeamColor color;
    PieceType type;

    // constructor is used by children constructors
    Piece(ChessGame.TeamColor c, PieceType t) {
        color = c;
        type = t;
    }

    @Override
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    // the pieceMoves method from ChessPiece is defined and implemented by its children

    @Override
    public String toString() {
        String result = switch (type) {
            case KING -> " K ";
            case QUEEN -> " Q ";
            case ROOK -> " R ";
            case BISHOP -> " B ";
            case KNIGHT -> " N ";
            case PAWN -> " P ";
        };

        if(color == ChessGame.TeamColor.BLACK) {
            result = result.toLowerCase();
        }

        String ANSI_BLACK_TXT = "\u001B[30m";

        return ANSI_BLACK_TXT + result;
    }

    public String getPieceAsUnicode() {
        String result;
        String ANSI_RED_TXT = "\u001B[31m";
        String ANSI_WHITE_TXT = "\u001B[33m";

        if(color == ChessGame.TeamColor.BLACK) {
            result = ANSI_RED_TXT;
        }
        else {
            result = ANSI_WHITE_TXT;
        }
        result += switch (type) {
            case KING -> " ♚ ";
            case QUEEN -> " ♛ ";
            case ROOK -> " ♜ ";
            case BISHOP -> " ♝ ";
            case KNIGHT -> " ♞ ";
            case PAWN -> " ♟ ";
        };
        return result;
    }
}
