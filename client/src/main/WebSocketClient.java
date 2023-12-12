import chess.adapters.ChessAdapter;
import chess.interfaces.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;

import utils.ClientDisplay;
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
    ClientDisplay client;

    public WebSocketClient(String url, ClientDisplay clientDisplay) throws URISyntaxException, DeploymentException, IOException {
        URI webSocketURI = new URI(url);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        this.session = container.connectToServer(this, webSocketURI);
        this.client = clientDisplay;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message){
                try {
                    Gson gson = ChessAdapter.getGson();
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME -> {
                            LoadGame loadGame = gson.fromJson(message, LoadGame.class);
                            if(loadGame.moveMade != null) {
                                ChessMove move = loadGame.moveMade;
                                client.updateGameWithMove(move);
                            }
                            else {
                                client.updateGameData(loadGame.game);
                            }
                            client.showNotification("Server Message: Load Game");
                        }
                        case NOTIFICATION -> {
                            Notification notification = gson.fromJson(message, Notification.class);
                            client.showNotification(notification.message);
                        }
                        case ERROR -> {
                            ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                            client.showError(errorMessage.errorMessage);
                        }
                    }
                } catch (Exception e) {
                    ConsoleOutput.printError("Error inside WebSocketClient: Couldn't read server message.");
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
