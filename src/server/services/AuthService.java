package server.services;

import server.ServerException;
import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;
import server.models.AuthToken;
import server.models.UserData;

public class AuthService extends Service {
    DataAccess dataAccess;

    public AuthService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthToken login(UserData user) throws ServerException {
        String username = user.getUsername();
        String password = user.getPassword();

        UserData userInDb = dataAccess.findUser(username);

        if(userInDb != null && userInDb.getPassword().equals(password)) {
            return dataAccess.createAuthToken(username);
        }
        throw new ServerException(401, "invalid login credentials");
    }

    public void logout(String token) throws ServerException {
        try {
            dataAccess.deleteAuthToken(token);
        }
        catch (DataAccessException e) {
            throw new ServerException(500, "server error");
        }
    }

    public AuthToken findToken(String token) {
        return dataAccess.findAuthToken(token);
    }
}
