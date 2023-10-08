package chess;

import chess.interfaces.ChessPosition;
import chess.interfaces.ChessPiece;

public class Move implements chess.interfaces.ChessMove {
    ChessPosition startPosition;
    ChessPosition endPosition;
    ChessPiece.PieceType promotionPiece;

    // constructor
    public Move(ChessPosition start, ChessPosition end, ChessPiece.PieceType type) {
        startPosition = start;
        endPosition = end;
        promotionPiece = type; // will be null most of the time
    }


    @Override
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    @Override
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    @Override
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

}
