package server;

import com.google.gson.Gson;
import server.dataAccess.DataAccess;
import server.models.AuthToken;
import server.models.GameData;
import server.services.GameJoinRequest;
import server.models.UserData;
import server.services.AdminService;
import server.services.AuthService;
import server.services.GameService;
import server.services.UserService;
import spark.*;

import java.util.*;

public class Server {
    DataAccess dataAccess;
    AdminService adminService;
    AuthService authService;
    GameService gameService;
    UserService userService;

    public static void main(String[] args) {
        Server server = new Server(new DataAccess());
        server.run();
    }

    public Server(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.adminService = new AdminService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.authService = new AuthService(dataAccess);
        this.userService = new UserService(dataAccess);
    }

    /**
     * Configures and runs the server. Specifies the server endpoints and HTTP methods.
     */
    private void run() {
        try {
            // Configure Spark
            Spark.port(8080);

            Spark.externalStaticFileLocation("web/");

            // Server Endpoints
            Spark.delete("/db", this::clearApplication);
            Spark.post("/user", this::registerUser);
            Spark.post("/session", this::login);
            Spark.delete("/session", this::logout);
            Spark.get("/game", this::listGames);
            Spark.post("/game", this::createGame);
            Spark.put("/game", this::joinGame);

            // Exceptions
            Spark.exception(ServerException.class, this::exceptionHandler); // handles ServerException errors
            Spark.exception(Exception.class, (e, req, res) -> exceptionHandler(new ServerException(500, e.getMessage()), req, res)); // handles Spark exceptions
            Spark.notFound((req, res) -> {
                var msg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
                return exceptionHandler(new ServerException(404, msg), req, res);
            });
        }
        catch (Exception e) {
            System.out.println("Server Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Handles all server exceptions and formats them.
     * @param e Any ServerException.
     * @param req The Spark request object.
     * @param res The Spark response object.
     * @return The error body.
     */
    public Object exceptionHandler(ServerException e, Request req, Response res) {
        String body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(e.getCode());
        res.body(body);
        return body;
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
    public Object login(Request req, Response res) throws ServerException {
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
        AuthToken token = getAuthorization(req);
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
        getAuthorization(req);
        var gameList = gameService.listGames();
        return responseJSON("games", gameList.toArray());
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
        getAuthorization(req);
        GameData game = getBody(req, GameData.class);
        game = gameService.createGame(game.getGameName());
        return responseJSON("gameID", game.getGameId());
    }

    public Object joinGame(Request req, Response res) throws ServerException {
        AuthToken token = getAuthorization(req);
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

    private static <T> T getBody(Request request, Class<T> classType) throws ServerException {
        var body = new Gson().fromJson(request.body(), classType);
        if (body == null) {
            throw new ServerException(400, "missing body");
        }
        return body;
    }

    // TODO: Maybe move it inside the AuthService instead of the Server class itself
    private AuthToken getAuthorization(Request req) throws ServerException {
        String authorization = req.headers("authorization");
        if(authorization != null){
            AuthToken token = dataAccess.findAuthToken(authorization);
            if(token != null) {
                return token;
            }
        }
        throw new ServerException(401, "not authorized");
    }
}
