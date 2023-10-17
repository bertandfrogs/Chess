package api.services.createGame;

import api.services.base.Response;

/**
 * The CreateGameResponse object from the server.
 * On success, return [200] and the new gameID, otherwise return an error code and a message.
 */
public class CreateGameResponse extends Response {
    String gameID;

    /**
     * Constructor for a success response.
     * @param gameID - a unique id for the new game just created.
     */
    CreateGameResponse(String gameID){
        super(200);
        this.gameID = gameID;
    }

    /**
     * Constructor for a fail response (uses base class implementation)
     * @param code Error code.
     * @param description Optional - a specific error description (error code 500)
     */
    CreateGameResponse(int code, String description){
        super(code,description);
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
}
