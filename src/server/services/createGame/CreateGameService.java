package server.services.createGame;

import server.services.base.Service;

/**
 * Creates a new game, generating a new gameID that will be used by the server.
 */
public class CreateGameService extends Service {
    /**
     * @param request - a CreateGameRequest object containing the authToken and gameName
     * @return the CreateGameResponse with the new gameID, or an error message
     */
    CreateGameResponse createGame(CreateGameRequest request){
        return null;
    }
}
