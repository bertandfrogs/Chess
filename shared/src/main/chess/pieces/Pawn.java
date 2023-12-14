package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.*;

public class Pawn extends Piece {
    public Pawn(ChessGame.TeamColor c) {
        // uses the parent constructor to set color and type
        super(c, PAWN);
    }
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard chessBoard, ChessPosition myChessPosition) {
        super.setBoard(chessBoard);
        super.setMyPosition(myChessPosition);

        int myRow = myPosition.getRow();
        Collection<ChessMove> moves = new ArrayList<>();

        int moveDirection = 0;
        int startingRow = 0;
        int promotionRow = 0;
        switch(color) {
            case BLACK -> {
                // can only move down
                moveDirection = -1;
                startingRow = 7;
                promotionRow = 1;
            }
            case WHITE -> {
                // can only move up
                moveDirection = 1;
                startingRow = 2;
                promotionRow = 8;
            }
        }

        if(myRow == startingRow){
            // can move two spaces forward
            ChessMove move = getDoubleForwardMovement(board, myPosition, moveDirection);
            if (move != null){
                moves.add(move);
            }
        }

        // move one space forward, if possible
        Collection<ChessMove> moveForward = getForwardMovement(board, myPosition, moveDirection, promotionRow);
        if (!moveForward.isEmpty()){
            moves.addAll(moveForward);
        }

        // move diagonally to the right, if possible
        Collection<ChessMove> moveRight = getCaptureMovement(board, myPosition, moveDirection, 1, promotionRow);
        if (!moveRight.isEmpty()){
            moves.addAll(moveRight);
        }

        // move diagonally to the left, if possible
        Collection<ChessMove> moveLeft = getCaptureMovement(board, myPosition, moveDirection, -1, promotionRow);
        if (!moveLeft.isEmpty()){
            moves.addAll(moveLeft);
        }

        return moves;
    }

    private Collection<ChessMove> getForwardMovement(ChessBoard board, ChessPosition startingPosition, int moveDirection, int promotionRow) {
        Collection<ChessMove> newMoves = new ArrayList<>();
        int row = startingPosition.getRow();
        int col = startingPosition.getColumn();

        ChessPosition endPosition = new Position(row+moveDirection, col);

        if(!endPosition.isOutOfBounds() && board.getPiece(endPosition) == null){
            if(row+moveDirection == promotionRow) {
                newMoves.add(new Move(startingPosition, endPosition, QUEEN));
                newMoves.add(new Move(startingPosition, endPosition, ROOK));
                newMoves.add(new Move(startingPosition, endPosition, KNIGHT));
                newMoves.add(new Move(startingPosition, endPosition, BISHOP));
            }
            else{
                newMoves.add(new Move(startingPosition, endPosition, null));
            }
        }
        return newMoves;
    }

    private ChessMove getDoubleForwardMovement(ChessBoard board, ChessPosition startingPosition, int moveDirection) {
        ChessPosition endPosition = new Position(startingPosition.getRow()+moveDirection, startingPosition.getColumn());

        if(!endPosition.isOutOfBounds() && board.getPiece(endPosition) == null){
            Position newEndPosition = new Position(endPosition.getRow()+moveDirection, endPosition.getColumn());

            if(!newEndPosition.isOutOfBounds() && board.getPiece(newEndPosition) == null) {
                return new Move(startingPosition, newEndPosition, null);
            }
        }
        return null;
    }

    private Collection<ChessMove> getCaptureMovement(ChessBoard board, ChessPosition startingPosition, int moveDirectionY, int moveDirectionX, int promotionRow) {
        Collection<ChessMove> newMoves = new ArrayList<>();

        int row = startingPosition.getRow();
        int col = startingPosition.getColumn();

        Position endPosition = new Position(row+moveDirectionY, col+moveDirectionX);
        if(endPosition.isOutOfBounds()){
            return newMoves; // empty array
        }
        ChessPiece endPiece = board.getPiece(endPosition);

        if(endPiece == null || endPiece.getTeamColor() == color){
            return newMoves; // empty array
        }
        else {
            if(endPosition.getRow() == promotionRow) {
                newMoves.add(new Move(startingPosition, endPosition, QUEEN));
                newMoves.add(new Move(startingPosition, endPosition, ROOK));
                newMoves.add(new Move(startingPosition, endPosition, KNIGHT));
                newMoves.add(new Move(startingPosition, endPosition, BISHOP));
            }
            else{
                newMoves.add(new Move(startingPosition, endPosition, null));
            }
        }
        return newMoves;
    }
}
