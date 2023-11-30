package service;

import com.google.gson.Gson;
import models.GameData;

public class GameResponse {
    public int gameID;
    public String whiteUsername;
    public String blackUsername;
    public String gameName;

    public GameResponse(GameData game) {
        this.gameID = game.getGameId();
        this.gameName = game.getGameName();
        this.blackUsername = game.getBlackUsername();
        this.whiteUsername = game.getWhiteUsername();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
