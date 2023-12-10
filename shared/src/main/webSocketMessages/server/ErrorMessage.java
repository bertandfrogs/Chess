package webSocketMessages.server;

// This message is sent to a client when it sends an invalid command. The message must include the word Error.
public class ErrorMessage extends ServerMessage {
    public String errorMessage;
    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }
}
