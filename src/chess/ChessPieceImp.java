package chess;

import java.util.Collection;

public class ChessPieceImp implements ChessPiece {
    ChessGame.TeamColor color;
    PieceType type;

    public ChessPieceImp(ChessGame.TeamColor c, PieceType t) {
        color = c;
        type = t;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    @Override
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return null;
    }
}
