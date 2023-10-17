package server.services.clearApplication;

import server.services.base.Service;

/**
 * FOR ADMIN AND TESTING USE ONLY.
 * Clears the database. Removes all users, games, and authTokens.
 * Receives a ClearApplicationRequest, parses it, and returns a ClearApplicationResponse.
 */
public class ClearApplicationService extends Service {
    /**
     * @param request - a specific clearApplicationRequest object.
     * @return response - the response from the server (clearApplicationResponse)
     */
    public ClearApplicationResponse clearApplication(ClearApplicationRequest request){
        return null;
    }
}
