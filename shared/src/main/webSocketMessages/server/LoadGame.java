package webSocketMessages.server;

import chess.Move;
import models.GameData;

// Used by the server to send the current game state (and/or the move just made) to a client.
public class LoadGame extends ServerMessage {
    public GameData game;
    public Move moveMade; // can be null
    public LoadGame(GameData game, Move moveMade) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.moveMade = moveMade;
    }
}
