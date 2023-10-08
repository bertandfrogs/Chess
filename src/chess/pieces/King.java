package chess.pieces;

import chess.Move;
import chess.Position;
import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

import static chess.interfaces.ChessPiece.PieceType.KING;

public class King extends Piece {
    public King(ChessGame.TeamColor c) {
        // uses the parent constructor to set color and type
        super(c, KING);
    }

    ChessBoard board;
    ChessPosition myPosition;

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;

        Collection<ChessMove> moves = new ArrayList<>();
        // specific moveset for king.

        // only able to move one space in all directions (if empty)
        // captures enemy piece if occupying the same square
        // DOES NOT CHECK IF KING WILL BE IN DANGER OR NOT


        // right up
        ChessMove newMove = getMove(1,1);
        if(newMove != null) {
            moves.add(newMove);
        }

        // up
        newMove = getMove(0,1);
        if(newMove != null) {
            moves.add(newMove);
        }

        // left up
        newMove = getMove(-1,1);
        if(newMove != null) {
            moves.add(newMove);
        }

        // left
        newMove = getMove(-1,0);
        if(newMove != null) {
            moves.add(newMove);
        }

        // left down
        newMove = getMove(-1,-1);
        if(newMove != null) {
            moves.add(newMove);
        }

        // down
        newMove = getMove(0,-1);
        if(newMove != null) {
            moves.add(newMove);
        }

        // right down
        newMove = getMove(1,-1);
        if(newMove != null) {
            moves.add(newMove);
        }

        // right
        newMove = getMove(1,0);
        if(newMove != null) {
            moves.add(newMove);
        }

        return moves;
    }

    private ChessMove getMove(int directionX, int directionY){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // out of bounds check
        if(row+directionX < 1 || row+directionX > 8 || col+directionY < 1 || col+directionY > 8) {
            return null;
        }

        Position newPosition = new Position(row+directionX, col+directionY);
        Piece targetPiece = (Piece) board.getPiece(newPosition);
        if(targetPiece != null && targetPiece.getTeamColor() == color){
            return null; // can't capture own team
        }
        else {
            return new Move(myPosition, newPosition, null);
        }
    }
}
