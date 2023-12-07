package service;

import com.google.gson.Gson;
import models.GameData;

public class GameResponse {
    public int gameID;
    public String whiteUsername;
    public String blackUsername;
    public String gameName;
    public String gameState;

    public GameResponse(GameData game) {
        this.gameID = game.getGameId();
        this.gameName = game.getGameName();
        this.blackUsername = game.getBlackUsername();
        this.whiteUsername = game.getWhiteUsername();
        this.gameState = game.getGameState().name();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
