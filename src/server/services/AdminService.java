package server.services;

import server.ServerException;
import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;
import server.dataAccess.DatabaseSQL;

/**
 * Manages the requests and responses to and from the server for the ClearApplication endpoint.
 */
public class AdminService {
    private final DatabaseSQL databaseSQL;

    // constructor for MySQL database
    public AdminService(DatabaseSQL data) {
        databaseSQL = data;
    }

    /**
     * Clears the database. Removes all users, games, and authTokens.
     */
    public void clearApplication() throws ServerException {
        databaseSQL.clear();
    }
}
