package webSocketMessages.server;

import models.GameData;

// Used by the server to send the current game state (and/or the move just made) to a client.
public class LoadGame extends ServerMessage {
    public GameData game;
    public LoadGame(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
