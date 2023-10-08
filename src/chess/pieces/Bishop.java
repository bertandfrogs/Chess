package chess.pieces;

import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.Collection;

import static chess.interfaces.ChessPiece.PieceType.BISHOP;

public class Bishop extends Piece {
    public Bishop(ChessGame.TeamColor c) {
        // uses the parent constructor to set color and type
        super(c, BISHOP);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // specific moveset for bishops --
        // able to move up left or up right along diagonal (as long as there is empty space and a clear path)
        // able to move down left or down right along diagonal (as long as there is empty space and a clear path)
        // captures enemy piece if in the target space
        return null;
    }
}
