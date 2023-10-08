package chess.pieces;

import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.Collection;
import static chess.interfaces.ChessPiece.PieceType.ROOK;

public class Rook extends Piece {
    public Rook(ChessGame.TeamColor c) {
        // uses the parent constructor to set color and type
        super(c, ROOK);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // specific moveset for rooks --
        // able to move right or left (as long as there is empty space)
        // able to move up or down (as long as there is empty space)
        // captures enemy pieces if in the space
        return null;
    }
}
