package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.BISHOP;

public class Bishop extends Piece {
    public Bishop(ChessGame.TeamColor c) {
        // uses the parent constructor to set color and type
        super(c, BISHOP);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        super.setBoard(board);
        super.setMyPosition(myPosition);

        Collection<ChessMove> moves = new ArrayList<>();

        addNewMovesInLineOfSight(moves, 1,1);   // right up
        addNewMovesInLineOfSight(moves, -1,1);  // left up
        addNewMovesInLineOfSight(moves, -1,-1); // left down
        addNewMovesInLineOfSight(moves, 1,-1);  // right down

        return moves;
    }
}
