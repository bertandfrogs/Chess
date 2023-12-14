package passoffTests;

import chess.*;
import chess.pieces.*;

/**
 * Used for testing your code
 * Add in code using your classes for each method for each FIXME
 */
public class TestFactory {
    //Chess Functions
    //------------------------------------------------------------------------------------------------------------------
    public static ChessBoard getNewBoard(){
        return new Board();
    }

    public static ChessGame getNewGame(){
        return new Game();
    }

    public static ChessPiece getNewPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type){
        return switch (type) {
            case PAWN -> new Pawn(pieceColor);
            case ROOK -> new Rook(pieceColor);
            case KNIGHT -> new Knight(pieceColor);
            case BISHOP -> new Bishop(pieceColor);
            case QUEEN -> new Queen(pieceColor);
            case KING -> new King(pieceColor);
        };
    }

    public static ChessPosition getNewPosition(Integer row, Integer col){
        return new Position(row, col);
    }

    public static ChessPosition getNewPositionFromChessNotation(char col, char row) {
        if (Character.isDefined(col) && Character.isDefined(row)){
            int colInt = Character.getNumericValue(col) - 9; // uses the numeric value of the letter "a" in Unicode (10) and subtracts 9 to get a = column 1.
            int rowInt = Character.getNumericValue(row);
            return new Position(colInt,rowInt);
        }
        return null;
    }

    public static ChessMove getNewMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece){
        return new Move(startPosition, endPosition, promotionPiece);
    }
    //------------------------------------------------------------------------------------------------------------------


    //Server API's
    //------------------------------------------------------------------------------------------------------------------
    public static String getServerPort(){
        return "8080";
    }
    //------------------------------------------------------------------------------------------------------------------


    //Websocket Tests
    //------------------------------------------------------------------------------------------------------------------
    public static Long getMessageTime(){
        /*
        Changing this will change how long tests will wait for the server to send messages.
        3000 Milliseconds (3 seconds) will be enough for most computers. Feel free to change as you see fit,
        just know increasing it can make tests take longer to run.
        (On the flip side, if you've got a good computer feel free to decrease it)
         */
        return 500L;
    }
    //------------------------------------------------------------------------------------------------------------------
}
