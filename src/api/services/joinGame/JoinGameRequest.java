package api.services.joinGame;

import api.services.base.Request;
import api.models.AuthToken;

/**
 * The JoinGameRequest to the server. It contains an AuthToken, the gameID, and playerColor.
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
