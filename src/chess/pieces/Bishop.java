package chess.pieces;

import chess.Move;
import chess.Position;
import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

import static chess.interfaces.ChessPiece.PieceType.BISHOP;

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

        // right up
        Collection<ChessMove> newMoves = getPieceMoveRecursive(moves, myPosition, 1,1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // left up
        newMoves = getPieceMoveRecursive(moves, myPosition, -1,1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // left down
        newMoves = getPieceMoveRecursive(moves, myPosition, -1,-1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // right down
        newMoves = getPieceMoveRecursive(moves, myPosition, 1,-1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        return moves;
    }
}
