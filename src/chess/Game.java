package chess;

import chess.interfaces.ChessBoard;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;
import chess.interfaces.ChessPiece;
import chess.pieces.Piece;

import java.util.Collection;
import java.util.Map;

// This class is the top-level management of the chess game.

public class Game implements chess.interfaces.ChessGame {
    private TeamColor teamTurn = TeamColor.WHITE;
    private Board chessBoard = new Board();

    @Override
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    @Override
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return null;
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // TODO: check move logic - is in check, team color, right kind of piece movement, etc
        /*
        Position startPosition = (Position) move.getStartPosition();
        Position endPosition = (Position) move.getEndPosition();
        Piece movingPiece = getPiece(startPosition);

        // check bounds of position
        int startRow = startPosition.getRow();
        int endRow = endPosition.getRow();
        int startCol = startPosition.getColumn();
        int endCol = endPosition.getColumn();

        if(startRow < 1 || endRow < 1 || startCol < 1 || endCol < 1
            || startRow > 8 || endRow > 8 || startCol > 8 || endCol > 8){
            throw new InvalidMoveException("Position out of bounds");
        }

        if(movingPiece != null) {
            if(movingPiece.getTeamColor() != currentTeamColor){
                throw new InvalidMoveException("Piece being moved is the wrong team color");
            }
            Piece pieceAtEndPosition = getPiece(endPosition);
            if(pieceAtEndPosition != null){
                if(movingPiece.getTeamColor() != pieceAtEndPosition.getTeamColor()) {
                    // moving piece captures enemy piece

                }
                else {
                    // illegal move
                    throw new InvalidMoveException("Piece tried to capture a piece of the same color");
                }
            }
            else {
                // the piece moves to the empty space
            }
        }
        else {
            throw new InvalidMoveException("No piece found at start position");
        }

         */
        chessBoard.movePiece(move);
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        return false;
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        return false;
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
    }

    @Override
    public void setBoard(ChessBoard board) {
        chessBoard = (Board) board;
    }

    @Override
    public ChessBoard getBoard() {
        return chessBoard;
    }
}
