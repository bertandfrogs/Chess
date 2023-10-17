package api.services.joinGame;

import api.services.base.Service;

/**
 * Manages the requests and responses to and from the server for the JoinGame endpoint.
 * Receives a JoinGameRequest, parses it, and returns a JoinGameResponse.
 * Verifies that the specified game exists, and, if a color is specified, adds the caller as the requested color to the game.
 * If no color is specified the user is joined as an observer. This request is idempotent.
 */
public class JoinGameService extends Service {
    JoinGameResponse joinGame(JoinGameRequest request){
        return null;
    }
}
