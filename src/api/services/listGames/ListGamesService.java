package api.services.listGames;

import api.services.base.Service;

/**
 * Manages the requests and responses to and from the server for the ListGames endpoint.
 * Gets a list of all active games.
 * Receives a ListGamesRequest, parses it, and returns a ListGamesResponse.
 */
public class ListGamesService extends Service {
    /**
     * @param request A ListGamesRequest object to be sent to the server.
     * @return The ListGamesResponse from the server.
     */
    ListGamesResponse listGames(ListGamesRequest request){
        return null;
    }
}
