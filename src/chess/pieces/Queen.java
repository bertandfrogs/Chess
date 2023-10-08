package chess.pieces;

import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.Collection;

import static chess.interfaces.ChessPiece.PieceType.QUEEN;

public class Queen extends Piece {
    public Queen(ChessGame.TeamColor c) {
        // uses the parent constructor to set color and type
        super(c, QUEEN);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // specific moveset for queen.

        // able to move as many spaces in all directions if empty space

        return null;
    }

}
