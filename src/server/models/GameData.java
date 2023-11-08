package server.models;

import chess.Game;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The GameData model is the way that games are stored in the database, and it contains all the information about a game that the server needs.
 */
public class GameData {
    /**
     * A unique ID. Used by the DataAccess class as the key to the users map.
     */
    int gameID;

    /**
     * The username of the user who is playing as white.
     */
    String whiteUsername;

    /**
     * The username of the user who is playing as black.
     */
    String blackUsername;

    /**
     * The name of the game, as defined by the user.
     */
    String gameName;

    /**
     * The instantiation of the chess game.
     */
    Game game;

    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, Game game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public int getGameId() {
        return gameID;
    }

    public void setGameId(int gameId) {
        this.gameID = gameId;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameData gameData = (GameData) o;

        if ((whiteUsername == null ^ gameData.whiteUsername == null)
                || (blackUsername == null ^ gameData.blackUsername == null)
                || (gameName == null ^ gameData.gameName == null)
                || (whiteUsername != null && !whiteUsername.equals(gameData.whiteUsername))
                || (blackUsername != null && !blackUsername.equals(gameData.blackUsername))
                || (gameName != null && !gameName.equals(gameData.gameName))) {
            return false;
        }

        return gameID == gameData.gameID && game.equals(gameData.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public String toString() {
        Map<Object, Object> map = new HashMap<>();
        map.put("gameID", gameID);
        map.put("whiteUsername", whiteUsername);
        map.put("blackUsername", blackUsername);
        map.put("gameName", gameName);
        JsonObject gameJson = JsonParser.parseString(game.toString()).getAsJsonObject();
        map.put("game", gameJson);
        return new Gson().toJson(map);
    }
}