package passoffTests.myTests;

import chess.Board;
import chess.Game;
import chess.Position;
import chess.interfaces.ChessGame;
import chess.interfaces.ChessPiece;
import chess.interfaces.ChessPosition;
import chess.pieces.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChessJSON {
    Game game;
    Board board;

    @BeforeEach
    public void setup() {
        game = new Game();
        board = (Board) game.getBoard();
    }

    @Test
    public void serializeBoardOnePiece() {
        addNewPieceToBoard(3, 1, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        game.setTeamTurn(ChessGame.TeamColor.WHITE);
        String json = game.toString();
        Assertions.assertEquals("{\"turn\":\"WHITE\",\"board\":{\"31\":{\"color\":\"WHITE\",\"type\":\"QUEEN\"}}}", json);
    }

    @Test
    public void deserializeOnePieceTest() throws Exception {
        addNewPieceToBoard(8, 3, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        game.setTeamTurn(ChessGame.TeamColor.WHITE);
        String json = game.toString();
        Game result = game.getGameFromJSON(json);
        Assertions.assertEquals(game, result);
    }

    @Test
    public void deserializeMultiplePiecesTest() throws Exception {
        addNewPieceToBoard(3, 4, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        addNewPieceToBoard(2, 5, ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        addNewPieceToBoard(1, 1, ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        addNewPieceToBoard(4, 4, ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        game.setTeamTurn(ChessGame.TeamColor.BLACK);
        String json = game.toString();
        Game result = game.getGameFromJSON(json);
        Assertions.assertEquals(game, result);
    }

    @Test
    public void deserializeFullGameTest() throws Exception {
        game.newGame();
        String json = game.toString();
        Game result = game.getGameFromJSON(json);
        Assertions.assertEquals(game, result);
    }

    // Test Helper Functions
    private void addNewPieceToBoard(int row, int col, ChessGame.TeamColor color, ChessPiece.PieceType type){
        Piece piece = switch (type) {
            case KING -> new King(color);
            case QUEEN -> new Queen(color);
            case KNIGHT -> new Knight(color);
            case PAWN -> new Pawn(color);
            case ROOK -> new Rook(color);
            case BISHOP -> new Bishop(color);
        };
        ChessPosition position = new Position(row, col);

        board.addPiece(position, piece);
        game.setBoard(board);
    }

    public void print() {
        for(int row = 0; row < 33; row++) {
            if (row % 4 == 0) {
                System.out.print("+");
                for (int j = 0; j < 8; j++) {
                    System.out.print("----------+");
                }
            }
            else {
                System.out.print("|");
                for (int j = 0; j < 8; j++) {
                    System.out.print("          |");
                }
            }
            System.out.print("\n");
        }
    }
}
