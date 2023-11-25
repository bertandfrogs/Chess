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

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        super.setBoard(board);
        super.setMyPosition(myPosition);

        Collection<ChessMove> moves = new ArrayList<>();

        addNewMoveIfPossible(moves,1,1);    // right up
        addNewMoveIfPossible(moves,0,1);    // up
        addNewMoveIfPossible(moves,-1,1);   // left up
        addNewMoveIfPossible(moves,-1,0);   // left
        addNewMoveIfPossible(moves,-1,-1);  // left down
        addNewMoveIfPossible(moves,0,-1);   // down
        addNewMoveIfPossible(moves,1,-1);   // right down
        addNewMoveIfPossible(moves,1,0);    // right

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

    private void addNewMoveIfPossible(Collection<ChessMove> moves, int directionX, int directionY) {
        ChessMove newMove = getMove(directionX,directionY);
        if(newMove != null) {
            moves.add(newMove);
        }
    }
}
