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

    private void run() {
        try {
            // Configure Spark
            Spark.port(8080);

            // Test Endpoint (Hello World)
            Spark.get("/hello", (request, response) -> "Hello World!");

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

    public Object exceptionHandler(ServerException e, Request req, Response res) {
        String body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(e.getCode());
        res.body(body);
        return body;
    }

    public Object clearApplication(Request req, Response res) throws ServerException {
        adminService.clearApplication();
        return responseJSON();
    }

    public Object registerUser(Request req, Response res) throws ServerException {
        UserData user = getBody(req, UserData.class);
        AuthToken token = userService.registerUser(user);
        return responseJSON("username", user.getUsername(), "authToken", token.getAuthToken());
    }

    public Object login(Request req, Response res) throws ServerException {
        UserData user = getBody(req, UserData.class);
        AuthToken token = authService.login(user);
        return responseJSON("username", user.getUsername(), "authToken", token.getAuthToken());
    }

    public Object logout(Request req, Response res) throws ServerException {
        AuthToken token = getAuthorization(req);
        authService.logout(token.getAuthToken());
        return responseJSON("username", token.getUsername(), "authToken", token.getAuthToken());
    }

    public Object listGames(Request req, Response res) throws ServerException {
        getAuthorization(req);
        var gameList = gameService.listGames();
        return responseJSON("games", gameList.toArray());
    }

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
