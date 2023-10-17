package server.services.joinGame;

import server.services.base.Service;

/**
 * Verifies that the specified game exists, and, if a color is specified, adds the caller as the requested color to the game.
 * If no color is specified the user is joined as an observer. This request is idempotent.
 */
public class JoinGameService extends Service {
    JoinGameResponse joinGame(JoinGameRequest request){
        return null;
    }
}
