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

        // right up
        Collection<ChessMove> newMoves = getPieceMoveRecursive(moves, myPosition, 1,1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // up
        newMoves = getPieceMoveRecursive(moves, myPosition, 0,1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // left up
        newMoves = getPieceMoveRecursive(moves, myPosition, -1,1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // left
        newMoves = getPieceMoveRecursive(moves, myPosition, -1,0);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // left down
        newMoves = getPieceMoveRecursive(moves, myPosition, -1,-1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // down
        newMoves = getPieceMoveRecursive(moves, myPosition, 0,-1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // right down
        newMoves = getPieceMoveRecursive(moves, myPosition, 1,-1);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }

        // right
        newMoves = getPieceMoveRecursive(moves, myPosition, 1,0);
        if(newMoves != null) {
            moves.addAll(newMoves);
        }
        
        return moves;
    }

}
