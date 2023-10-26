package server.services;

import server.ServerException;
import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;
import server.models.AuthToken;
import server.models.UserData;

public class UserService extends Service {
    DataAccess dataAccess;

    public UserService(DataAccess data) {
        dataAccess = data;
    }

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
