package server.dataAccess;

import chess.Board;
import chess.Game;
import chess.Position;
import chess.interfaces.ChessGame;
import chess.pieces.*;
import server.ServerException;

import java.util.Objects;
import java.util.Scanner;

import static chess.interfaces.ChessGame.TeamColor.BLACK;
import static chess.interfaces.ChessGame.TeamColor.WHITE;

public class GameDeserializer {
    public Game deserialize(String json) throws ServerException {
        if(json.isEmpty()){
            return null;
        }
        Game game = new Game();
        Board board = new Board();
        Scanner scanner = new Scanner(json);
        scanner.useDelimiter("\"[:{},]*\"|\"}+$");

        scanner.next(); // skips over "turn"
        String teamTurn = scanner.next();
        if(teamTurn.equals("WHITE")) {
            game.setTeamTurn(WHITE);
        }
        else if(teamTurn.equals("BLACK")){
            game.setTeamTurn(BLACK);
        }
        else {
            throw new ServerException(500, "Invalid team turn: " + teamTurn);
        }

        scanner.next(); // skips over "gameState"
        String gameState = scanner.next();

        game.setState(Game.stringToState(gameState));

        scanner.next(); // skips over "board"

        while(scanner.hasNext()) {
            String hash = scanner.next();
            Position pos = parsePosition(hash);
            ChessGame.TeamColor color;
            Piece piece;

            scanner.next(); // skips over "color"
            String c = scanner.next();
            if (Objects.equals(c, "WHITE")) {
                color = WHITE;
            }
            else {
                color = BLACK;
            }

            scanner.next(); // skips over "type"
            String t = scanner.next();
            t = t.substring(0,3); // truncating it to get rid of the ending garbage
            piece = switch (t) {
                case "ROO" -> new Rook(color);
                case "BIS" -> new Bishop(color);
                case "KIN" -> new King(color);
                case "KNI" -> new Knight(color);
                case "QUE" -> new Queen(color);
                default -> new Pawn(color);
            };

            board.addPiece(pos, piece);
        }
        game.setBoard(board);
        return game;
    }

    private static Position parsePosition(String input){
        input = input.toLowerCase();
        char[] chars = input.toCharArray();
        if(chars.length != 2) {
            return null;
        }
        int row = Character.getNumericValue(chars[0]);
        int col = Character.getNumericValue(chars[1]);
        return new Position(row,col);
    }
}


