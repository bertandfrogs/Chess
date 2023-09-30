package chess;

public class ChessMoveImp implements ChessMove {
    ChessPosition startPosition;
    ChessPosition endPosition;
    ChessPiece.PieceType pieceType;

    // constructor
    public ChessMoveImp(ChessPosition start, ChessPosition end, ChessPiece.PieceType type) {
        startPosition = start;
        endPosition = end;
        pieceType = type;
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
        return pieceType;
    }
}
