package chess;

import chess.interfaces.ChessPosition;
import chess.interfaces.ChessPiece;

public class Move implements chess.interfaces.ChessMove {
    Position startPosition;
    Position endPosition;
    ChessPiece.PieceType promotionPiece;

    // constructor
    public Move(ChessPosition start, ChessPosition end, ChessPiece.PieceType type) {
        startPosition = (Position) start;
        endPosition = (Position) end;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;

        return hashCode() == move.hashCode();
    }

    @Override
    public int hashCode() {
        int pp = 0;
        if(getPromotionPiece() != null){
            pp = switch(getPromotionPiece()){
                case QUEEN -> 10000;
                case KING -> 20000;
                case BISHOP -> 30000;
                case KNIGHT -> 40000;
                case ROOK -> 50000;
                case PAWN -> 60000;
            };
        }
        return pp+(startPosition.getRow()*1000)+(startPosition.getColumn()*100)+(endPosition.getRow()*10)+(endPosition.getColumn());
    }
}
