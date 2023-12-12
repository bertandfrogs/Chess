import com.google.gson.Gson;
import jakarta.websocket.*;

import webSocketMessages.client.GameCommand;
import webSocketMessages.server.ErrorMessage;
import webSocketMessages.server.LoadGame;
import webSocketMessages.server.Notification;
import webSocketMessages.server.ServerMessage;
import ui.ConsoleOutput;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient extends Endpoint {
    Session session;

    public WebSocketClient(String url) throws URISyntaxException, DeploymentException, IOException {
        URI webSocketURI = new URI(url);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        this.session = container.connectToServer(this, webSocketURI);

        // TODO: implement the message handler
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message){
                try {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME -> {
                            LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
                            System.out.println("Server Message: Load Game");
                        }
                        case NOTIFICATION -> {
                            Notification notification = new Gson().fromJson(message, Notification.class);
                            ConsoleOutput.printActionSuccess("**" + notification.message + "**");
                        }
                        case ERROR -> {
                            ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                            ConsoleOutput.printError("**" + errorMessage.errorMessage + "**");
                        }
                    }
                } catch (Exception e) {
                    ConsoleOutput.printError("Error inside WebSocketFacade: Couldn't read server message.");
                    ConsoleOutput.printError(e.getMessage());
                }
            }
        });
    }

    public void sendCommand(GameCommand command) throws IOException {
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    // required to have this method in here to extend Endpoint
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
