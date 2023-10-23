package api.services.createGame;

import api.services.base.Request;
import api.models.AuthToken;

/**
 * The CreateGameRequest to the server, passing it the authToken and a game name.
 */
public class CreateGameRequest extends Request {
    /**
     * The AuthToken that's connected to the user. Required to have a valid AuthToken for game creation.
     */
    AuthToken authorization;
    /**
     * What the user wants to name the game.
     */
    String gameName;

    /**
     * Constructor uses the parent class's constructor to set the HTTP method and the url.
     * @param authorization The user's AuthToken.
     * @param gameName The user's desired game name.
     */
    CreateGameRequest(AuthToken authorization, String gameName){
        super("POST", "/game");
        this.authorization = authorization;
        this.gameName = gameName;
    }

    public AuthToken getAuthorization() {
        return authorization;
    }

    public void setAuthorization(AuthToken authorization) {
        this.authorization = authorization;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
