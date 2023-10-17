package api.services.createGame;

import api.services.base.Request;
import api.models.AuthToken;

/**
 * The CreateGameRequest to the server, passing it the authToken and a game name.
 */
public class CreateGameRequest extends Request {
    AuthToken authorization;
    String gameName;

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
