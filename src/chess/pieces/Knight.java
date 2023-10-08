package chess.pieces;

import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.Collection;

import static chess.interfaces.ChessPiece.PieceType.KNIGHT;

public class Knight extends Piece {
    public Knight(ChessGame.TeamColor c) {
        super(c, KNIGHT);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // specific moveset for knights.
        // moves in an l-shape (if there's enough space on the board)
        // able to jump to a specific position if it's empty (no need to check to see if the path is blocked)
        return null;
    }
}
