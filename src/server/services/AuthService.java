package server.services;

import server.ServerException;
import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;
import server.dataAccess.DatabaseSQL;
import server.models.AuthToken;
import server.models.UserData;

public class AuthService extends Service {
    DatabaseSQL dataAccess;

    public AuthService(DatabaseSQL dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * Logs in a user, creating a new AuthToken in the database.
     * @param user The user, providing username and password.
     * @return An AuthToken if successfully logged in.
     * @throws ServerException Throws an error if the username or password are incorrect
     */
    public AuthToken login(UserData user) throws ServerException {
        String username = user.getUsername();
        String password = user.getPassword();

        UserData userInDb = dataAccess.findUser(username);

        if(userInDb != null && userInDb.getPassword().equals(password)) {
            return dataAccess.createAuthToken(username);
        }
        throw new ServerException(401, "invalid login credentials");
    }

    /**
     * Logs out the user represented by the AuthToken.
     * @param token The user's AuthToken to be removed.
     * @throws ServerException Throws an error if something goes wrong.
     */
    public void logout(String token) throws ServerException {
        dataAccess.deleteAuthToken(token);
    }

    /**
     * Gets an AuthToken object given a String AuthToken.
     * @param token The token as a string.
     * @return The full AuthToken object.
     */
    public AuthToken findToken(String token) throws ServerException {
        return dataAccess.findAuthToken(token);
    }
}
