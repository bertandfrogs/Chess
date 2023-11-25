package chess.pieces;

import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

import static chess.interfaces.ChessPiece.PieceType.QUEEN;

public class Queen extends Piece {
    public Queen(ChessGame.TeamColor c) {
        // uses the parent constructor to set color and type
        super(c, QUEEN);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        super.setBoard(board);
        super.setMyPosition(myPosition);

        Collection<ChessMove> moves = new ArrayList<>();

        addNewMovesInLineOfSight(moves,1,1);    // right up
        addNewMovesInLineOfSight(moves,0,1);    // up
        addNewMovesInLineOfSight(moves,-1,1);   // left up
        addNewMovesInLineOfSight(moves,-1,0);   // left
        addNewMovesInLineOfSight(moves,-1,-1);  // left down
        addNewMovesInLineOfSight(moves,0,-1);   // down
        addNewMovesInLineOfSight(moves,1,-1);   // right down
        addNewMovesInLineOfSight(moves,1,0);    // right

        return moves;
    }
}
