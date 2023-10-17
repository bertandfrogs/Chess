package api.models;

import chess.Game;

/**
 * The GameData model is the way that games are stored in the database, and it contains all the information about a game that the server needs.
 */
public class GameData {
    int gameId;
    String whiteUsername;
    String blackUsername;
    String gameName;
    Game game;
}