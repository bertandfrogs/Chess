package server.models;

import chess.Game;
import chess.interfaces.ChessGame;

public class GameData {
    int gameId;
    String whiteUsername;
    String blackUsername;
    String gameName;
    Game game;
}