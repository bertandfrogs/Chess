package webSocketMessages.server;

// This is a message meant to inform a player when another player made an action.
public class Notification extends ServerMessage {
    public String message;
    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}
