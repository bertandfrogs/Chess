package chess;

import chess.interfaces.ChessBoard;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;
import chess.interfaces.ChessPiece;
import chess.pieces.Piece;

import java.util.Collection;

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

    public void nextTeamTurn() {
        if(teamTurn == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        }
        else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Piece piece = chessBoard.getPiece(startPosition);
        if(piece == null){
            return null;
        }
        else {
            return piece.pieceMoves(chessBoard, startPosition);
        }
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Piece movingPiece = chessBoard.getPiece(move.getStartPosition());
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if(movingPiece == null){
            throw new InvalidMoveException("No piece located at starting position");
        }
        else if(movingPiece.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("Piece at starting position is not the current team's color");
        }
        else if(validMoves != null && !validMoves.contains(move)){ //TODO: remove the null check once all pieces have valid moves
            throw new InvalidMoveException("Not a valid move");
        }

        chessBoard.movePiece(move);
        nextTeamTurn();
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
