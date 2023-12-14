package service;

import chess.ChessGame;
import com.google.gson.Gson;

/**
 * The purpose of this class is to provide a structure for the JSON serialization of the join game HTTP request.
 * Uses the enum TeamColor from the ChessGame interface.
 */
public class GameJoinRequest {
    public ChessGame.TeamColor playerColor;
    public int gameID;

    /**
     * @param playerColor The user's requested color.
     * @param gameID The ID of the game to join.
     */
    public GameJoinRequest(ChessGame.TeamColor playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
