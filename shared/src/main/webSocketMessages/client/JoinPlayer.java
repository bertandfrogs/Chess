package webSocketMessages.client;

import chess.ChessGame;

// Message that a client sends to the server to request to join a game.
public class JoinPlayer extends GameCommand {
    ChessGame.TeamColor playerColor;

    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken, CommandType.JOIN_PLAYER, gameID);
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
