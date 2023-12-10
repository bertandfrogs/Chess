package webSocketMessages.client;

import chess.interfaces.ChessGame;
import service.GameJoinRequest;

// Message that a client sends to the server to request to join a game.
public class JoinPlayer extends GameCommand {
    ChessGame.TeamColor playerColor;

    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor color) {
        super(authToken, CommandType.JOIN_PLAYER, gameID);
        this.playerColor = color;
    }
}
