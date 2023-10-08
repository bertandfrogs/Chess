package chess.pieces;

import chess.Board;
import chess.Move;
import chess.Position;
import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.interfaces.ChessPiece.PieceType.*;

public class Pawn extends Piece {
    public Pawn(ChessGame.TeamColor c) {
        // uses the parent constructor to set color and type
        super(c, PAWN);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard chessBoard, ChessPosition myChessPosition) {
        Board board = (Board) chessBoard;
        Position myPosition = (Position) myChessPosition;
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
        if (moveForward != null){
            moves.addAll(moveForward);
        }

        // move diagonally to the right, if possible
        Collection<ChessMove> moveRight = getDiagonalMovement(board, myPosition, moveDirection, 1, promotionRow);
        if (moveRight != null){
            moves.addAll(moveRight);
        }

        // move diagonally to the left, if possible
        Collection<ChessMove> moveLeft = getDiagonalMovement(board, myPosition, moveDirection, -1, promotionRow);
        if (moveLeft != null){
            moves.addAll(moveLeft);
        }

        return moves;
    }

    private Collection<ChessMove> getForwardMovement(Board board, Position startingPosition, int moveDirection, int promotionRow) {
        Collection<ChessMove> newMoves = new ArrayList<>();
        int row = startingPosition.getRow();
        int col = startingPosition.getColumn();

        if(row+moveDirection > 8 || row+moveDirection < 1){
            return null; // out of bounds
        }

        Position endPosition = new Position(row+moveDirection, col);

        if(board.getPiece(endPosition.hashCode()) == null){
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

    private ChessMove getDoubleForwardMovement(Board board, Position startingPosition, int moveDirection) {
        Position endPosition = new Position(startingPosition.getRow()+moveDirection, startingPosition.getColumn());

        if(board.getPiece(endPosition.hashCode()) == null){
            Position newEndPosition = new Position(endPosition.getRow()+moveDirection, endPosition.getColumn());
            if(board.getPiece(newEndPosition.hashCode()) == null) {
                return new Move(startingPosition, newEndPosition, null);
            }
        }
        return null;
    }

    private Collection<ChessMove> getDiagonalMovement(Board board, Position startingPosition, int moveDirectionY, int moveDirectionX, int promotionRow) {
        Collection<ChessMove> newMoves = new ArrayList<>();

        int row = startingPosition.getRow();
        int col = startingPosition.getColumn();

        if(col+moveDirectionX > 8 || col+moveDirectionX < 1 || row+moveDirectionY > 8 || row+moveDirectionY < 1){
            return null; // out of bounds
        }

        Position endPosition = new Position(row+moveDirectionY, col+moveDirectionX);
        Piece endPiece = board.getPiece(endPosition.hashCode());

        if(endPiece == null || endPiece.getTeamColor() == color){
            return null;
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
