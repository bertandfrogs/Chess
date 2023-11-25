package server.services;

import chess.interfaces.ChessGame;
import server.ServerException;
import server.dataAccess.DatabaseSQL;
import models.GameData;

import java.util.Collection;

/**
 * Manages the requests and responses for all the Game endpoints in the server.
 */
public class GameService extends Service {
    private final DatabaseSQL dataAccess;

    public GameService(DatabaseSQL data) {
        dataAccess = data;
    }

    /**
     * Gets a list of all active games.
     * @return The list of games as a collection of GameData objects.
     */
    public Collection<GameData> listGames() throws ServerException {
        return dataAccess.listGames();
    }

    /**
     * Creates a new game, generating a new gameID that will be used by the server.
     * @param gameName The user's requested name for the new game.
     * @return The newly created GameData object.
     */
    public GameData createGame(String gameName) throws ServerException {
        return dataAccess.createGame(gameName);
    }

    /**
     * Verifies that the specified game exists, and, if a color is specified, adds the caller as the requested color to the game.
     *  * If no color is specified the user is joined as an observer. This request is idempotent.
     * @param username The username of the player requesting to join.
     * @param color The requested user color (Optional). Accepted values: WHITE, BLACK, or null.
     * @param gameID Required to join the game. The unique game id.
     * @return The GameData object.
     * @throws ServerException Throws an error if the gameID is not found or is unknown, if the color requested is already taken, or if something else goes wrong.
     */
    public GameData joinGame(String username, ChessGame.TeamColor color, int gameID) throws ServerException {
        GameData game = dataAccess.findGameById(gameID);
        if (game == null) {
            throw new ServerException(400, "unknown gameID");
        }
        else if (color == null) {
            return game;
        }
        else {
            if(color == ChessGame.TeamColor.WHITE) {
                if (game.getWhiteUsername() == null || game.getWhiteUsername().equals(username)) {
                    game.setWhiteUsername(username);
                }
                else {
                    throw new ServerException(403, "color taken");
                }
            }
            else if(color == ChessGame.TeamColor.BLACK) {
                if (game.getBlackUsername() == null || game.getBlackUsername().equals(username)) {
                    game.setBlackUsername(username);
                }
                else {
                    throw new ServerException(403, "color taken");
                }
            }
        }
        dataAccess.updateGame(game);
        return game;
    }
}
