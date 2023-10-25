package server;

import server.dataAccess.DataAccess;
import spark.*;
import static spark.Spark.*;

public class Server {
    public static void main(String[] args) {

    }

    private void run() {
        try {
            // Configure Spark
            Spark.port(2222);
            DataAccess data = new DataAccess();
            Service service = new Service(data);

            Spark.delete("/db", service::clearApplication);
            Spark.post("/user", service::registerUser);
            Spark.post("/session", service::login);
            Spark.delete("/session", service::logout);
            Spark.get("/game", service::listGames);
            Spark.post("/game", service::createGame);
            Spark.put("/game", service::joinGame);
        }
        catch (Exception e) {
            System.out.println("Server Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
