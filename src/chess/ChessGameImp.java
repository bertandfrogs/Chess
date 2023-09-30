package chess;

import java.util.Collection;

// This class is the top-level management of the chess game. (main)

public class ChessGameImp implements ChessGame {
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard chessBoard;

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
        chessBoard = board;
    }

    @Override
    public ChessBoard getBoard() {
        return chessBoard;
    }
}
