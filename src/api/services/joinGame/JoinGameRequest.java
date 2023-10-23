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

    /**
     * Constructor uses the parent class's constructor to set the HTTP method and the url.
     * @param authorization The user's AuthToken
     * @param playerColor The user's desired color
     * @param gameID The specific id of the game.
     */
    JoinGameRequest(AuthToken authorization, String playerColor, String gameID){
        super("PUT", "/game");
        this.authorization = authorization;
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
}
