package server.models;

import chess.Game;
import com.google.gson.Gson;

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
        return gameID == gameData.gameID && whiteUsername.equals(gameData.whiteUsername) && blackUsername.equals(gameData.blackUsername) && gameName.equals(gameData.gameName) && game.equals(gameData.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}