package webSocketMessages.client;

import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import service.GameJoinRequest;

// Message that a client sends to the server to request to make a move in a game.
public class MakeMove extends GameCommand {
    ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken, CommandType.MAKE_MOVE, gameID);
        this.move = move;
    }
}
