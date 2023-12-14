package server.handlers;

import chess.Game;
import chess.Move;
import chess.adapters.ChessAdapter;
import chess.ChessGame;
import com.google.gson.Gson;
import models.AuthToken;
import models.GameData;
import models.UserData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import server.dataAccess.DatabaseSQL;
import webSocketMessages.client.GameCommand;
import webSocketMessages.client.JoinPlayer;
import webSocketMessages.client.MakeMove;
import webSocketMessages.server.ErrorMessage;
import webSocketMessages.server.LoadGame;
import webSocketMessages.server.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
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
        public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

        public void addConnection(String username, Connection connection) {
            connections.put(username, connection);
        }

        public void removeConnection(Session session) {
            Connection toBeRemoved = null;
            for (var c : connections.values()) {
                if (c.session.equals(session)) {
                    toBeRemoved = c;
                    break;
                }
            }

            if (toBeRemoved != null) {
                connections.remove(toBeRemoved.user.getUsername());
            }
        }

        public void broadcast(int gameID, String excludeUsername, String msg) throws Exception {
            var removeList = new ArrayList<Connection>();
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    if (c.game != null && c.game.getGameId() == gameID && !c.user.getUsername().equals(excludeUsername)) {
                        c.send(msg);
                    }
                } else {
                    removeList.add(c);
                }
            }
            for (var c : removeList) {
                connections.remove(c.user.getUsername());
            }
        }

        public boolean gameHasConnections(int gameID) {
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    if (c.game != null && c.game.getGameId() == gameID) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public String toString() {
            var sb = new StringBuilder("[\n");
            for (var c : connections.values()) {
                sb.append(String.format("  {'game':%d, 'user': %s}%n", (c.game != null) ? c.game.getGameId() : null, c.user));
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
        connections.removeConnection(session);
        System.out.println("Connection closed with status [" + statusCode + "]: " + reason);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            GameCommand command = readJson(message, GameCommand.class);
            Connection connection = getConnection(command.getAuthString(), session);
            GameData gameData = getGameData(command);
            if (connection != null && gameData != null) {
                connection.game = gameData; // updating the connection's gameData
                String player = connection.user.getUsername();
                switch (command.getCommandType()) {
                    case JOIN_PLAYER -> {
                        JoinPlayer joinPlayerCommand = readJson(message, JoinPlayer.class);
                        joinPlayer(connection, joinPlayerCommand, gameData, player);
                    }
                    case JOIN_OBSERVER -> {
                        joinObserver(connection, command, gameData, player);
                    }
                    case MAKE_MOVE -> {
                        MakeMove makeMoveCommand = readJson(message, MakeMove.class);
                        makeMove(connection, makeMoveCommand, gameData, player);
                    }
                    case LEAVE -> leave(connection, command, gameData, player);
                    case RESIGN -> resign(connection, command, gameData, player);
                }
            } else {
                Connection.sendError(session.getRemote(), "Invalid game command.");
            }
        } catch (Exception e) {
            Connection.sendError(session.getRemote(), e.getMessage());
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error){
        System.out.println("WebSocket Error" + ((error.getMessage() != null) ? (": " + error.getMessage()) : ""));
    }

    private static <T> T readJson(String json, Class<T> classType) throws IOException {
        Gson gson = ChessAdapter.getGson();
        T obj = gson.fromJson(json, classType);
        if (obj == null) {
            throw new IOException("Invalid JSON");
        }
        return obj;
    }

    private Connection getConnection(String tokenStr, Session session) throws Exception {
        Connection connection = null;
        AuthToken authToken = database.findAuthToken(tokenStr);
        if (authToken != null) {
            String username = authToken.getUsername();
            connection = connections.connections.get(username);
            if (connection == null) {
                UserData user = database.findUser(authToken.getUsername());
                connection = new Connection(user, session);
                connections.addConnection(authToken.getUsername(), connection);
            }
        }
        return connection;
    }

    // Validate commands that are sent to the server (returns the GameData object if found, null if not found)
    private GameData getGameData(GameCommand command) {
        if(command == null || command.getGameID() == null) {
            return null;
        }
        try {
            GameData gameData = database.findGameById(command.getGameID());
            return gameData;
        }
        catch (Exception e) {
            return null;
        }
    }

    private boolean validateJoinPlayer(JoinPlayer command, GameData gameData, String username) {
        if(username == null || command.getPlayerColor() == null || gameData.getGame() == null || gameData.getGameState() == Game.State.finished) {
            return false;
        }

        // protect against color stealing, this also checks if a player can rejoin a paused game
        return switch (command.getPlayerColor()) {
            case WHITE -> Objects.equals(gameData.getWhiteUsername(), username);
            case BLACK -> Objects.equals(gameData.getBlackUsername(), username);
        };
    }

    // catches invalid Move syntax and user permissions
    private boolean validateMakeMove (MakeMove makeMove, GameData gameData, String username) {
        // validate that the move itself is defined and not out of bounds
        if(makeMove.getMove() == null
            || makeMove.getMove().getStartPosition() == null
            || makeMove.getMove().getStartPosition().isOutOfBounds()
            || makeMove.getMove().getEndPosition() == null
            || makeMove.getMove().getEndPosition().isOutOfBounds()) {
            return false;
        }

        // validate user's ability to move
        try {
            ChessGame.TeamColor teamTurn = gameData.getGame().getTeamTurn();

            // validate that the game is active
            if (gameData.getGameState() != Game.State.active) return false;

            // validate that it's the user's turn
            return switch (teamTurn) {
                case BLACK -> Objects.equals(gameData.getBlackUsername(), username);
                case WHITE -> Objects.equals(gameData.getWhiteUsername(), username);
            };
        } catch (Exception e) {
            return false;
        }
    }

    // WebSocket Endpoints

    // builds upon the information stored in the database by the join player HTTP request / response
    // sets up the game websocket
    private void joinPlayer(Connection connection, JoinPlayer joinPlayerCommand, GameData game, String player) throws Exception {
        if(validateJoinPlayer(joinPlayerCommand, game, player)){
            if (game.getWhiteUsername() != null && game.getBlackUsername() != null) {
                game.setGameState(Game.State.active);
                database.updateGame(game); // update the database
                connection.game = game; // update the connection
            } else if (game.getGameState() == Game.State.paused) {
                // implement rejoining a paused game
            }

            // only the new player needs to have their game loaded
            LoadGame loadGame = new LoadGame(game);
            connection.send(loadGame.toString());

            // send notification to all others in the game
            String joinMessage = player + " joined game as " + joinPlayerCommand.getPlayerColor();
            Notification notificationMsg = new Notification(joinMessage, connection.game.getGameState());
            connections.broadcast(joinPlayerCommand.getGameID(), player, notificationMsg.toString());
        }
        else {
            connection.sendError("Invalid join game command.");
        }
    }

    // JOIN_OBSERVER -- Server sends WebSocket message to other players that an observer has joined.
    private void joinObserver(Connection connection, GameCommand command, GameData gameData, String player) throws Exception {
        if(player != null){
            // only the observer needs to have their game loaded
            LoadGame loadGame = new LoadGame(gameData);
            connection.send(loadGame.toString());

            Notification notificationMsg = new Notification(player + " joined game as an observer.", null);
            connections.broadcast(command.getGameID(), player, notificationMsg.toString());
        }
    }

    // MAKE_MOVE 	Integer gameID, ChessMove move 	Used to request to make a move in a game.
    private void makeMove(Connection connection, MakeMove makeMoveCommand, GameData gameData, String player) throws Exception {
        if(validateMakeMove(makeMoveCommand, gameData, player)){
            Move move = makeMoveCommand.getMove();

            // check if the move is valid from that position
            if(gameData.getGame().validMoves(move.getStartPosition()).contains(move)){
                try {
                    // make the move, if it's invalid it will throw an exception.
                    gameData.getGame().makeMove(move);

                    // check if the move resulted in check or checkmate, getCheckMessage will be null if not.
                    String checkMessage = getCheckMessage(gameData);
                    if(checkMessage != null) {
                        if(checkMessage.contains("checkmate") || checkMessage.contains("stalemate")) {
                            gameData.setGameState(Game.State.finished);
                        }
                    }

                    database.updateGame(gameData); // update the database

                    connection.game = gameData; // update the connection

                    // send a load game message to everyone in the game
                    LoadGame loadGame = new LoadGame(connection.game);
                    connections.broadcast(makeMoveCommand.getGameID(), "", loadGame.toString());

                    // send a notification to everyone besides the moving player
                    Notification notification = new Notification(player + " made move " + move.toChessNotation(), connection.game.getGameState());
                    connections.broadcast(makeMoveCommand.getGameID(), player, notification.toString());

                    // if necessary, send a notification about check or checkmate to everyone in the game
                    if(checkMessage != null) {
                        Notification checkNotification = new Notification(checkMessage, connection.game.getGameState());
                        connections.broadcast(makeMoveCommand.getGameID(), "", checkNotification.toString());
                    }
                }
                catch (Exception e) {
                    connection.sendError("Couldn't make move.");
                }
            }
            else {
                connection.sendError("Invalid move.");
            }
        }
        else {
            connection.sendError("Invalid game command.");
        }
    }

    private String getCheckMessage(GameData gameData) {
        Game game = gameData.getGame();
        String black = gameData.getBlackUsername();
        String white = gameData.getWhiteUsername();
        if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            return black + " is in checkmate!";
        }
        else if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            return white + " is in checkmate!";
        }
        else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            return black + " is in check!";
        }
        else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            return white + " is in check!";
        }
        else if (game.isInStalemate(ChessGame.TeamColor.BLACK) || game.isInStalemate(ChessGame.TeamColor.WHITE)) {
            return "The game resulted in a stalemate!";
        }

        else return null;
    }

    // LEAVE 	Integer gameID 	Tells the server you are leaving the game so it will stop sending you notifications.
    private void leave(Connection connection, GameCommand command, GameData gameData, String player) throws Exception {
        String leavingRole = getPlayerRole(connection, player);
        if(player != null) {
            switch (leavingRole) {
                case "OBSERVER" -> {
                    // they are an observer, nothing needs to change in the database
                }
                case "BLACK", "WHITE" -> {
                    if(gameData.getGameState() == Game.State.active) {
                        gameData.setGameState(Game.State.paused);
                        database.updateGame(gameData);
                    }
                }
                default -> throw new Exception("Trying to send a leave message without being in a game");
            }

            Notification notification = new Notification(player + " left the game.", gameData.getGameState());
            connections.broadcast(command.getGameID(), player, notification.toString());
            connection.game = null; // update the game in the connection

            // clean up database
            if(!connections.gameHasConnections(gameData.getGameId()) && gameData.getGameState() == Game.State.finished) {
                database.deleteGame(gameData);
            }
        }
    }

    // RESIGN 	Integer gameID 	Forfeits the match and ends the game (no more moves can be made).
    private void resign(Connection connection, GameCommand command, GameData gameData, String player) throws Exception {
        if(player != null){
            if(getPlayerRole(connection, player).equals("BLACK") || getPlayerRole(connection, player).equals("WHITE")) {
                if(gameData.getGameState() != Game.State.finished) {
                    gameData.setGameState(Game.State.finished);
                    database.updateGame(gameData); // updates the database game
                    connection.game = gameData; // updates the connection game

                    // send a message to everyone
                    Notification notification = new Notification(player + " resigned.", gameData.getGameState());
                    connections.broadcast(command.getGameID(), "", notification.toString());
                }
                else {
                    connection.sendError("Can't resign, the game is already over");
                }
            }
            else {
                connection.sendError("Not allowed to resign.");
            }
        }
    }

    private String getPlayerRole(Connection connection, String username) {
        if(connection.game != null && connection.user != null) {
            if(username.equals(connection.game.getBlackUsername())) {
                return "BLACK";
            }
            else if (username.equals(connection.game.getWhiteUsername())) {
                return "WHITE";
            }
            else {
                return "OBSERVER";
            }
        }
        return "ERROR";
    }
}
