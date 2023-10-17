package server.services.joinGame;

import server.services.base.Request;
import server.models.AuthToken;

/**
 * Formats the JoinGame HTTP request
 */
public class JoinGameRequest extends Request {
    AuthToken authorization;
    String playerColor;
    String gameID;

    JoinGameRequest(AuthToken authorization, String playerColor, String gameID){
        super("PUT", "/game");
        this.authorization = authorization;
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
}
