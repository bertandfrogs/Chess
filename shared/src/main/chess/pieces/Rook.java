package chess.pieces;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import static chess.ChessPiece.PieceType.ROOK;

public class Rook extends Piece {
    public Rook(ChessGame.TeamColor c) {
        // uses the parent constructor to set color and type
        super(c, ROOK);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        super.setBoard(board);
        super.setMyPosition(myPosition);

        Collection<ChessMove> moves = new ArrayList<>();

        addNewMovesInLineOfSight(moves,1,0);    // right
        addNewMovesInLineOfSight(moves,-1,0);   // left
        addNewMovesInLineOfSight(moves,0,1);    // up
        addNewMovesInLineOfSight(moves,0,-1);   // down

        return moves;
    }
}
