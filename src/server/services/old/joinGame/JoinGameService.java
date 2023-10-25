package server.services.old.joinGame;

import server.services.old.base.Service;

/**
 * Manages the requests and responses to and from the server for the JoinGame endpoint.
 * Receives a JoinGameRequest, parses it, and returns a JoinGameResponse.
 * Verifies that the specified game exists, and, if a color is specified, adds the caller as the requested color to the game.
 * If no color is specified the user is joined as an observer. This request is idempotent.
 */
public class JoinGameService extends Service {
    /**
     * @param request A JoinGameRequest object containing the authToken, gameID, and playerColor.
     * @return The JoinGameResponse from the server.
     */
    JoinGameResponse joinGame(JoinGameRequest request){
        return null;
    }
}
