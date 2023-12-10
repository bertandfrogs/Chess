package server.handlers;

import com.google.gson.Gson;
import models.AuthToken;
import models.GameData;
import models.UserData;
import server.ServerException;
import server.dataAccess.DatabaseSQL;
import server.services.AdminService;
import server.services.AuthService;
import server.services.GameService;
import server.services.UserService;
import service.GameJoinRequest;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class HttpHandler {
    DatabaseSQL databaseSQL;
    AdminService adminService;
    AuthService authService;
    GameService gameService;
    UserService userService;

    public HttpHandler(DatabaseSQL db) {
        databaseSQL = db;
        this.adminService = new AdminService(this.databaseSQL);
        this.gameService = new GameService(this.databaseSQL);
        this.authService = new AuthService(this.databaseSQL);
        this.userService = new UserService(this.databaseSQL);
    }

    /**
     * The clearApplication handler. Passes data from the server to the AdminService.
     * @param req The Spark request object
     * @param res The Spark response object
     * @return Only returns success (200) or fail (500).
     * @throws ServerException
     */
    public Object clearApplication(Request req, Response res) throws ServerException {
        adminService.clearApplication();
        return responseJSON();
    }

    /**
     * The registerUser handler gets the user data from the request body. Passes data from the server to the UserService.
     * @param req The Spark request object.
     * @param res The Spark response object.
     * @return If successful, returns the new user's username and their new authToken wrapped in a JSON object.
     * @throws ServerException
     */
    public Object registerUser(Request req, Response res) throws ServerException {
        UserData user = getBody(req, UserData.class);
        AuthToken token = userService.registerUser(user);
        return responseJSON("username", user.getUsername(), "authToken", token.getAuthToken());
    }


    /**
     * The login handler gets the user data from the request body. Passes data from the server to the AuthService.
     * @param req The Spark request object.
     * @param res The Spark response object.
     * @return If successful, returns the username and the authToken for the current session wrapped in a JSON object.
     * @throws ServerException
     */
    public Object login(Request req, Response res) throws Exception {
        UserData user = getBody(req, UserData.class);
        AuthToken token = authService.login(user);
        return responseJSON("username", user.getUsername(), "authToken", token.getAuthToken());
    }

    /**
     * The logout handler verifies the AuthToken of the request. It sends data from the server to the AuthService.
     * @param req The Spark request object.
     * @param res The Spark response object.
     * @return Only returns success (200) or fail (500).
     * @throws ServerException
     */
    public Object logout(Request req, Response res) throws ServerException {
        AuthToken token = AuthService.getAuthorization(req);
        authService.logout(token.getAuthToken());
        return responseJSON("username", token.getUsername(), "authToken", token.getAuthToken());
    }

    /**
     * The listGames handler verifies the AuthToken of the request. It sends data from the server to the AuthService.
     * @param req The Spark request object.
     * @param res The Spark response object.
     * @return A list of all games as a JSON object.
     * @throws ServerException
     */
    public Object listGames(Request req, Response res) throws ServerException {
        AuthService.getAuthorization(req);
        var gameList = gameService.listGames();
        return responseJSON("games", GameService.toList(gameList));
    }

    /**
     * The createGame handler verifies the AuthToken of the request, and gets the game data from the request body.
     * It sends data from the server to the AuthService.
     * @param req The Spark request object
     * @param res The Spark response object
     * @return The new gameID wrapped in a JSON object.
     * @throws ServerException
     */
    public Object createGame(Request req, Response res) throws ServerException {
        AuthService.getAuthorization(req);
        GameData game = getBody(req, GameData.class);
        game = gameService.createGame(game.getGameName());
        return responseJSON("gameID", game.getGameId());
    }

    public Object joinGame(Request req, Response res) throws ServerException {
        AuthToken token = AuthService.getAuthorization(req);
        GameJoinRequest gameJoinRequest = getBody(req, GameJoinRequest.class);
        gameService.joinGame(token.getUsername(), gameJoinRequest.playerColor, gameJoinRequest.gameID);
        return responseJSON();
    }

    /**
     * Takes an even number of Objects and serializes them into JSON.
     * For example, "responseJSON("name", this.name)" would return {"name": "jeffrey"}
     * @return The JSON object.
     */
    private static String responseJSON(Object... props) {
        Map<Object, Object> map = new HashMap<>();
        for (var i = 0; i+1 < props.length; i = i+2) {
            map.put(props[i], props[i+1]);
        }
        return new Gson().toJson(map);
    }

    /**
     * Gets the body from the HTTP request and deserializes it into a GSON object of a given type.
     * @param request The Spark Request object
     * @param classType The type of object to create and return.
     * @return The object created by deserializing the request.
     * @throws ServerException Throws an error if there is no Request body.
     */
    private static <T> T getBody(Request request, Class<T> classType) throws ServerException {
        var body = new Gson().fromJson(request.body(), classType);
        if (body == null) {
            throw new ServerException(400, "missing body");
        }
        return body;
    }
}
