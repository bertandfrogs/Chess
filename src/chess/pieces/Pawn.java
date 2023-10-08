package chess.pieces;

import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.Collection;

import static chess.interfaces.ChessPiece.PieceType.PAWN;

public class Pawn extends Piece {
    public Pawn(ChessGame.TeamColor c) {
        // uses the parent constructor to set color and type
        super(c, PAWN);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // specific moveset for pawns.

        // if in starting position, able to move two spaces forward (if empty)
        // else able to move one space forward (if empty)
        // only able to move diagonally one space forward if enemy piece is in that space (capture)
        return null;
    }
}
