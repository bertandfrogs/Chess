package server.services;

import server.ServerException;
import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;
import server.models.AuthToken;
import server.models.UserData;

/**
 * Manages the requests and responses to and from the server for the User related endpoints (Register User)
 */
public class UserService extends Service {
    DataAccess dataAccess;

    public UserService(DataAccess data) {
        dataAccess = data;
    }

    /**
     * Registers a new user. Must check if a user exists in the database, and if not, add the user.
     * @param user The user to register in the database.
     * @return The AuthToken of the newly created (and logged in) user.
     * @throws ServerException Throws an error if the username is already taken.
     */
    public AuthToken registerUser(UserData user) throws ServerException {
        if(user.getUsername() == null) {
            throw new ServerException(400, "missing username");
        }
        if(user.getPassword() == null) {
            throw new ServerException(400, "missing password");
        }

        try {
            user = dataAccess.createUser(user);
            return dataAccess.createAuthToken(user.getUsername());
        }
        catch (DataAccessException e) {
            throw new ServerException(403, e.getMessage());
        }
    }
}
