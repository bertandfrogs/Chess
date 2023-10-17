package server.services.createGame;

import server.services.base.Request;
import server.models.AuthToken;

/**
 * Request to create a new game, needs an authToken and a game name.
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
