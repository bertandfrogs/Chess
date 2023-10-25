package server.services.old.listGames;
import server.services.old.base.Request;
import server.models.AuthToken;

/**
 * The request to the ListGamesService, containing the user's AuthToken.
 */
public class ListGamesRequest extends Request {
    /**
     * The user's AuthToken to verify that the user has permissions to see all games.
     */
    AuthToken authorization;

    /**
     * Constructor uses the parent class's constructor to set the HTTP method and the url.
     * @param authorization The user's AuthToken to verify that the user has permissions to see all games.
     */
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
