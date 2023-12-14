package webSocketMessages.server;
import chess.Game;

// This is a message meant to inform a player when another player made an action.
public class Notification extends ServerMessage {
    String message;
    Game.State state; // optional parameter, only updates the gameState if it is set

    public Notification(String message, Game.State state) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public Game.State getState() {
        return state;
    }
}
