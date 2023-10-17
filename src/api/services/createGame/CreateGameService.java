package api.services.createGame;

import api.services.base.Service;

/**
 * Manages the requests and responses to and from the server for the CreateGame endpoint.
 * Receives a CreateGameRequest, parses it, and returns a CreateGameResponse.
 * Creates a new game, generating a new gameID that will be used by the server.
 */
public class CreateGameService extends Service {
    /**
     * @param request A CreateGameRequest object containing the authToken and gameName
     * @return The CreateGameResponse with the new gameID, or an error message
     */
    CreateGameResponse createGame(CreateGameRequest request){
        return null;
    }
}
