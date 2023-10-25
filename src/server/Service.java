package server;

import com.google.gson.Gson;
import server.models.AuthToken;
import server.models.UserData;
import spark.*;

import server.dataAccess.DataAccess;
import server.dataAccess.DataAccessException;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Object clearApplication(Request req, Response res) throws DataAccessException {
        dataAccess.clear();
        return responseJSON(res);
    }

    private static <T> T getBody(Request request, Class<T> classType) throws DataAccessException {
        var body = new Gson().fromJson(request.body(), classType);
        if (body == null) {
            throw new DataAccessException("Missing body");
        }
        return body;
    }

    public Object registerUser(Request req, Response res) throws DataAccessException {
        UserData user = getBody(req, UserData.class);
        return null; //FIXME
    }

    public Object login(Request req, Response res) throws DataAccessException {
        UserData user = getBody(req, UserData.class);
        return null; //FIXME
    }

    public Object logout(Request req, Response res) throws DataAccessException {
        UserData user = getBody(req, UserData.class);
        return null; //FIXME
    }

    public Object listGames(Request req, Response res) {
        return null; //FIXME
    }

    public Object createGame(Request req, Response res) {
        return null; //FIXME
    }

    public Object joinGame(Request req, Response res) {
        return null; //FIXME
    }

    private static String responseJSON(Object object) {
        return new Gson().toJson(object);
    }
}
