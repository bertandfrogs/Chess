package server.services.old.clearApplication;

import server.services.old.base.Service;

/**
 * Manages the requests and responses to and from the server for the ClearApplication endpoint.
 * Receives a ClearApplicationRequest, parses it, and returns a ClearApplicationResponse.
 * Clears the database. Removes all users, games, and authTokens.
 */
public class ClearApplicationService extends Service {
    /**
     * @param request A specific clearApplicationRequest object.
     * @return response The response from the server (clearApplicationResponse)
     */
    public ClearApplicationResponse clearApplication(ClearApplicationRequest request){
        return null;
    }
}
