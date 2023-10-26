package server.services;

import server.ServerException;
import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;

public class AdminService {
    private DataAccess dataAccess;

    public AdminService(DataAccess data) {
        dataAccess = data;
    }

    public void clearApplication() throws ServerException {
        try {
            dataAccess.clear();
        }
        catch (DataAccessException e) {
            throw new ServerException(500, "server error");
        }
    }
}
