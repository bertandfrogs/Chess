package chess.pieces;

import chess.Move;
import chess.Position;
import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import static chess.interfaces.ChessPiece.PieceType.ROOK;

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

        // right
        Collection<ChessMove> newMoves = getPieceMoveRecursive(moves, myPosition, 1,0);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // left
        newMoves = getPieceMoveRecursive(moves, myPosition, -1,0);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // up
        newMoves = getPieceMoveRecursive(moves, myPosition, 0,1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // down
        newMoves = getPieceMoveRecursive(moves, myPosition, 0,-1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        return moves;
    }
}
