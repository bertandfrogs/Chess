package server.services;

import chess.interfaces.ChessGame;

public class GameJoinRequest {
    public ChessGame.TeamColor playerColor;
    public int gameID;

    public GameJoinRequest(ChessGame.TeamColor playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
}
