package server.handlers;

import chess.interfaces.ChessGame;
import chess.interfaces.ChessMove;
import com.google.gson.Gson;
import models.AuthToken;
import models.GameData;
import models.UserData;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import server.ServerException;
import server.dataAccess.DataAccessException;
import server.dataAccess.DatabaseSQL;
import webSocketMessages.client.GameCommand;
import webSocketMessages.client.JoinPlayer;
import webSocketMessages.client.MakeMove;
import webSocketMessages.server.ErrorMessage;
import webSocketMessages.server.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The WebSocketHandler class is used by the server to handle WebSocket messages.
 * The client(s) can send WebSocket messages to the server when playing or observing a game.
 * The server sends WebSocket messages to the clients (players and/or observers)
 */
@WebSocket
public class WebSocketHandler {
    private final DatabaseSQL database;

    public WebSocketHandler(DatabaseSQL databaseSQL) {
        this.database = databaseSQL;
    }

    protected static class Connection {
        public UserData user;
        public GameData game;
        public Session session;

        public Connection(UserData user, Session session) {
            this.user = user;
            this.session = session;
        }

        private void send(String msg) throws Exception {
            System.out.printf("Sending message to %s: %s%n", user.getUsername(), msg);
            session.getRemote().sendString(msg);
        }

        private void sendError(String msg) throws Exception {
            sendError(session.getRemote(), msg);
        }

        private static void sendError(RemoteEndpoint endpoint, String message) throws Exception {
            var errMsg = (new ErrorMessage("Error: " + message)).toString();
            System.out.println(errMsg);
            endpoint.sendString(errMsg);
        }
    }

    protected static class ConnectionManager {
        public final HashMap<String, Connection> connections = new HashMap<>();

        public void add(String username, Connection connection) {
            connections.put(username, connection);
        }

        public Connection get(String username) {
            return connections.get(username);
        }

        public void remove(Session session) {
            Connection removeConnection = null;
            for (var c : connections.values()) {
                if (c.session.equals(session)) {
                    removeConnection = c;
                    break;
                }
            }

            if (removeConnection != null) {
                connections.remove(removeConnection.user.getUsername());
            }
        }
        public void broadcast(int gameID, String excludeUsername, String msg) throws Exception {
            var removeList = new ArrayList<Connection>();
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    if (c.game.getGameId() == gameID && !c.user.getUsername().equals(excludeUsername)) {
                        c.send(msg);
                    }
                } else {
                    removeList.add(c);
                }
            }

            // Clean up any connections that were left open.
            for (var c : removeList) {
                connections.remove(c.user.getUsername());
            }
        }

        @Override
        public String toString() {
            var sb = new StringBuilder("[\n");
            for (var c : connections.values()) {
                sb.append(String.format("  {'game':%d, 'user': %s}%n", c.game.getGameId(), c.user));
            }
            sb.append("]");
            return sb.toString();
        }
    }

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connection opened: " + session.getRemoteAddress().toString());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        connections.remove(session);
        System.out.println("Connection closed with status [" + statusCode + "]: " + reason);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            var command = readJson(message, GameCommand.class);
            var connection = getConnection(command.getAuthString(), session);
            if (connection != null) {
                switch (command.getCommandType()) {
                    case JOIN_PLAYER -> {
                        JoinPlayer joinPlayerCommand = readJson(message, JoinPlayer.class);
                        joinPlayer(connection, joinPlayerCommand);
                    }
                    case JOIN_OBSERVER -> {
                        joinObserver(connection, command);
                    }
                    case MAKE_MOVE -> {
                        MakeMove makeMoveCommand = readJson(message, MakeMove.class);
                        makeMove(connection, makeMoveCommand);
                    }
                    case LEAVE -> {
                        leave(connection, command);
                    }
                    case RESIGN -> {
                        resign(connection, command);
                    }
                }
            } else {
                Connection.sendError(session.getRemote(), "unknown user");
            }
        } catch (Exception e) {
            Connection.sendError(session.getRemote(), e.getMessage());
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error){
        System.out.println("Connection error" + ((error.getMessage() == null) ? "" : ": " + error.getMessage()));
    }

    private static <T> T readJson(String json, Class<T> classType) throws IOException {
        T obj = new Gson().fromJson(json, classType);
        if (obj == null) {
            throw new IOException("Invalid JSON");
        }
        return obj;
    }

    private Connection getConnection(String id, Session session) throws Exception {
        Connection connection = null;
        var authToken = getAuthToken(id);
        if (authToken != null) {
            connection = connections.get(authToken.getUsername());
            if (connection == null) {
                var user = database.findUser(authToken.getUsername());
                connection = new Connection(user, session);
                connections.add(authToken.getUsername(), connection);
            }
        }
        return connection;
    }

    public AuthToken getAuthToken(String token) throws ServerException {
        if (token != null) {
            return database.findAuthToken(token);
        }
        return null;
    }

    // WebSocket Endpoints
    // JOIN_PLAYER -- Server sends WebSocket message to other players that a player has joined.
    private void joinPlayer(Connection connection, JoinPlayer joinPlayerCommand) throws Exception {
        connection.game = database.findGameById(joinPlayerCommand.getGameID());
        String player = connection.user.getUsername();
        if(player != null){
            String joinMessage = player + " joined game as " + joinPlayerCommand.getPlayerColor();
            Notification notificationMsg = new Notification(joinMessage);
            connections.broadcast(joinPlayerCommand.getGameID(), "", notificationMsg.toString()); // TODO: sends message to everyone besides the user who joined
        }
    }

    // JOIN_OBSERVER -- Server sends WebSocket message to other players that an observer has joined.
    private void joinObserver(Connection connection, GameCommand command) throws Exception {
        String player = connection.user.getUsername();
        if(player != null){
            String joinMessage = player + " joined game as an observer.";
            Notification notificationMsg = new Notification(joinMessage);
            connections.broadcast(command.getGameID(), "", notificationMsg.toString()); // TODO: sends message to everyone besides the user who joined
        }
    }

    // MAKE_MOVE 	Integer gameID, ChessMove move 	Used to request to make a move in a game.
    private void makeMove(Connection connection, MakeMove makeMoveCommand){

    }

    // LEAVE 	Integer gameID 	Tells the server you are leaving the game so it will stop sending you notifications.
    private void leave(Connection connection, GameCommand command) {

    }

    // RESIGN 	Integer gameID 	Forfeits the match and ends the game (no more moves can be made).
    private void resign(Connection connection, GameCommand command){

    }

    // loadGame

    // sendMessage

    // getConnection
}
