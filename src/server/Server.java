package server;

import com.google.gson.Gson;
import server.dataAccess.DatabaseSQL;
import server.handlers.HttpHandler;
import server.handlers.WebSocketHandler;
import spark.*;

import java.sql.SQLException;
import java.util.*;

public class Server {
    DatabaseSQL databaseSQL;
    WebSocketHandler webSocketHandler;
    HttpHandler httpHandler;

    public static void main(String[] args) throws SQLException {
        Server server = new Server(new DatabaseSQL());
        server.run();
    }

    public Server(DatabaseSQL databaseSQL) throws SQLException {
        this.databaseSQL = databaseSQL;
        this.webSocketHandler = new WebSocketHandler(this.databaseSQL);
        this.httpHandler = new HttpHandler(this.databaseSQL);
    }

    /**
     * Configures and runs the server. Specifies the server endpoints and HTTP methods.
     */
    private void run() {
        try {
            // Configure MySQL database
            databaseSQL.configureDatabase();

            // Configure Spark
            Spark.port(8080);

            // Configure WebSocket Connection
            Spark.webSocket("/connect", webSocketHandler);

            Spark.externalStaticFileLocation("web/");

            // Server Endpoints
            Spark.delete("/db", (req, res) -> httpHandler.clearApplication(req, res));
            Spark.post("/user", (req, res) -> httpHandler.registerUser(req, res));
            Spark.post("/session", (req, res) -> httpHandler.login(req, res));
            Spark.delete("/session", (req, res) -> httpHandler.logout(req, res));
            Spark.get("/game", (req, res) -> httpHandler.listGames(req, res));
            Spark.post("/game", (req, res) -> httpHandler.createGame(req, res));
            Spark.put("/game", (req, res) -> httpHandler.joinGame(req, res));

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
}
