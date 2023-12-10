package server.handlers;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.Session;
import server.dataAccess.DatabaseSQL;

@WebSocket
public class WebSocketHandler {
    final DatabaseSQL database;

    public WebSocketHandler(DatabaseSQL databaseSQL) {
        this.database = databaseSQL;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connection opened");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Connection closed");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String text) {
        System.out.println("Connection sent message" + text);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error){
        System.out.println("Connection error");
    }

    // WebSocket Endpoints
        // JOIN_PLAYER 	Integer gameID, ChessGame.TeamColor playerColor 	Used for a user to request to join a game.
        // JOIN_OBSERVER 	Integer gameID 	Used to request to start observing a game.
        // MAKE_MOVE 	Integer gameID, ChessMove move 	Used to request to make a move in a game.
        // LEAVE 	Integer gameID 	Tells the server you are leaving the game so it will stop sending you notifications.
        // RESIGN 	Integer gameID 	Forfeits the match and ends the game (no more moves can be made).

    // loadGame

    // sendMessage

    // getConnection
}
