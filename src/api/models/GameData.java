package api.models;

import chess.Game;

/**
 * The GameData model is the way that games are stored in the database, and it contains all the information about a game that the server needs.
 */
public class GameData {
    /**
     * A unique ID. Used by the DataAccess class as the key to the users map.
     */
    int gameId;

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
}