package passoffTests;

import chess.*;

/**
 * Used for testing your code
 * Add in code using your classes for each method for each FIXME
 */
public class TestFactory {
    private static ChessGame chessGame;
    private static ChessBoard chessBoard;
    private static ChessPiece chessPiece;
    private static ChessPosition chessPosition;
    private static ChessMove chessMove;

    //Chess Functions
    //------------------------------------------------------------------------------------------------------------------
    public static ChessBoard getNewBoard(){
        chessBoard = new ChessBoardImp();
		return chessBoard;
    }

    public static ChessGame getNewGame(){
        chessGame = new ChessGameImp();
		return chessGame;
    }

    public static ChessPiece getNewPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type){
        chessPiece = new ChessPieceImp(pieceColor, type);
		return chessPiece;
    }

    public static ChessPosition getNewPosition(Integer row, Integer col){
        chessPosition = new ChessPositionImp(row, col);
		return chessPosition;
    }

    public static ChessMove getNewMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece){
        chessMove = new ChessMoveImp(startPosition, endPosition, promotionPiece);
        return chessMove;
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
        return 3000L;
    }
    //------------------------------------------------------------------------------------------------------------------
}
