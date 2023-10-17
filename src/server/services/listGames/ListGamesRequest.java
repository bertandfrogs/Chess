package server.services.listGames;
import server.services.base.Request;
import server.models.AuthToken;

/**
 * The request to the ListGamesService, containing the user's AuthToken
 */
public class ListGamesRequest extends Request {
    AuthToken authorization;

    ListGamesRequest(AuthToken authorization){
        super("GET", "/game");
        this.authorization = authorization;
    }

    public AuthToken getAuthorization() {
        return authorization;
    }

    public void setAuthorization(AuthToken authorization) {
        this.authorization = authorization;
    }
}
