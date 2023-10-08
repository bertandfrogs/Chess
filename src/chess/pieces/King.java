package chess.pieces;

import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.Collection;

import static chess.interfaces.ChessPiece.PieceType.KING;

public class King extends Piece {
    public King(ChessGame.TeamColor c) {
        // uses the parent constructor to set color and type
        super(c, KING);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // specific moveset for king.

        // only able to move one space in all directions (if empty)
        // captures enemy piece if occupying the same square
        // DOES NOT CHECK IF KING WILL BE IN DANGER OR NOT

        return null;
    }
}
