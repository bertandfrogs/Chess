package server.services;

import server.ServerException;
import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;

/**
 * Manages the requests and responses to and from the server for the ClearApplication endpoint.
 */
public class AdminService {
    private final DataAccess dataAccess;

    public AdminService(DataAccess data) {
        dataAccess = data;
    }

    /**
     * Clears the database. Removes all users, games, and authTokens.
     */
    public void clearApplication() throws ServerException {
        try {
            dataAccess.clear();
        }
        catch (DataAccessException e) {
            throw new ServerException(500, "server error");
        }
    }
}
