package chess.interfaces;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;

/**
 * Represents a single chess piece
 * 
 * Note: You can add to this interface, but you should not alter the existing
 * methods.
 */
public interface ChessPiece {

    /**
     * The various different chess piece options
     */
    enum PieceType {
        @SerializedName("KING") KING,
        @SerializedName("QUEEN") QUEEN,
        @SerializedName("BISHOP") BISHOP,
        @SerializedName("KNIGHT") KNIGHT,
        @SerializedName("ROOK") ROOK,
        @SerializedName("PAWN") PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    ChessGame.TeamColor getTeamColor();

    /**
     * @return which type of chess piece this piece is
     */
    PieceType getPieceType();

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     * 
     * @return Collection of valid moves
     */
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    @Override
    boolean equals(Object o);
}
