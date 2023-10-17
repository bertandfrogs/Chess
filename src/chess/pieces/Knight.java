package chess.pieces;

import chess.Move;
import chess.Position;
import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

import static chess.interfaces.ChessPiece.PieceType.KNIGHT;

public class Knight extends Piece {
    public Knight(ChessGame.TeamColor c) {
        super(c, KNIGHT);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        super.setBoard(board);
        super.setMyPosition(myPosition);

        Collection<ChessMove> moves = new ArrayList<>();

        addNewMoveIfPossible(moves, 1, 2); // up 2 right 1
        addNewMoveIfPossible(moves, -1, 2); // up 2 left 1
        addNewMoveIfPossible(moves,2,1); // up 1 right 2
        addNewMoveIfPossible(moves,-2,1); // up 1 left 2
        addNewMoveIfPossible(moves,1,-2); // down 2 right 1
        addNewMoveIfPossible(moves,-1,-2); // down 2 left 1
        addNewMoveIfPossible(moves,2,-1); // down 1 right 2
        addNewMoveIfPossible(moves,-2,-1); // down 1 left 2

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
