package chess.pieces;

import chess.Move;
import chess.Position;
import chess.interfaces.ChessBoard;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import chess.interfaces.ChessPosition;

import java.util.Collection;

public abstract class Piece implements chess.interfaces.ChessPiece {
    // the Piece class is abstract, the specific Pawn, Rook, Bishop, etc. classes need to be instantiated

    ChessGame.TeamColor color;
    PieceType type;
    protected ChessBoard board;
    protected ChessPosition myPosition;

    // constructor is used by children constructors
    Piece(ChessGame.TeamColor c, PieceType t) {
        color = c;
        type = t;
    }

    @Override
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    // the pieceMoves method from ChessPiece is defined and implemented by its children

    @Override
    public String toString() {
        String result = switch (type) {
            case KING -> " K ";
            case QUEEN -> " Q ";
            case ROOK -> " R ";
            case BISHOP -> " B ";
            case KNIGHT -> " N ";
            case PAWN -> " P ";
        };

        if(color == ChessGame.TeamColor.BLACK) {
            result = result.toLowerCase();
        }

        return result;
    }

    public void setBoard(ChessBoard b) {
        board = b;
    }

    public void setMyPosition(ChessPosition myPos){
        myPosition = myPos;
    }

    public ChessPosition getMyPosition() {
        return myPosition;
    }

    // method used for Rook, Bishop, and Queen -
    // takes directionX and directionY to move in a straight line as long as it can
    public Collection<ChessMove> getMovesInLineOfSight(Collection<ChessMove> moves, ChessPosition currentPosition, int directionX, int directionY) {
        ChessPosition newPosition = new Position(currentPosition.getRow()+directionY, currentPosition.getColumn()+directionX);

        // out of bounds check
        if(newPosition.getRow() > 8 || newPosition.getRow() < 1 || newPosition.getColumn() > 8 || newPosition.getColumn() < 1) {
            return moves;
        }
        // piece at new position check
        else if (board.getPiece(newPosition) != null){
            if(board.getPiece(newPosition).getTeamColor() != color){
                // can capture enemy piece
                moves.add(new Move(myPosition, newPosition, null));
            }
            return moves; // stop recursion
        }
        else {
            // add to the list and recurse
            moves.add(new Move(myPosition, newPosition, null));
            moves = getMovesInLineOfSight(moves, newPosition, directionX, directionY);
        }
        return moves;
    }

    public void addNewMovesInLineOfSight(Collection<ChessMove> moves, int directionX, int directionY) {
        Collection<ChessMove> newMoves = getMovesInLineOfSight(moves, myPosition, directionX, directionY);
        if(!newMoves.isEmpty()) {
            moves.addAll(newMoves);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return color == piece.color
                && type == piece.type
                && myPosition.equals(piece.myPosition);
    }
}

