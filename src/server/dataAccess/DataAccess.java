package server.dataAccess;

import server.models.AuthToken;
import server.models.GameData;
import server.models.UserData;

import java.util.HashMap;
import java.util.Map;

/**
 * Memory representation of @DataAccessInterface
 */
public class DataAccess implements DataAccessInterface {
    final private Map<String, UserData> users = new HashMap<>();
    final private Map<String, AuthToken> authTokens = new HashMap<>();
    final private Map<String, GameData> games = new HashMap<>();

    /**
     * Clears all stored data in the database by calling clear() on all the maps.
     * Will not be available outside of testing.
     * @throws DataAccessException if it's not able to clear the database for some reason.
     */
    @Override
    public void clear() throws DataAccessException {
        users.clear();
        authTokens.clear();
        games.clear();
    }

    /**
     * Creates a new user and stores it in the users map. If the user or username already exist, don't add them again.
     * @param user The new user's data.
     * @return the new user
     */
    @Override
    public UserData createUser(UserData user) {
        return null;
    }

    /**
     * Gets a user from the users map and returns it.
     * @param username The username we're looking for.
     * @return The user if found in the database, returns null if not found.
     */
    @Override
    public UserData findUser(String username) {
        return null;
    }

    /**
     * Updates a user's information in the users map.
     * @param user The user's updated information (As a UserData object).
     * @return The updated UserData object.
     * @throws DataAccessException if not able to perform operation.
     */
    @Override
    public UserData updateUser(UserData user) throws DataAccessException {
        return null;
    }

    /**
     * Deletes a user from the users map.
     * @param user The user's information (As a UserData object).
     * @throws DataAccessException Throws an error if it's not possible to delete
     */
    @Override
    public void deleteUser(UserData user) throws DataAccessException {}

    /**
     * Creates a new game and stores it in the database. If the gameID already exists, don't add the game.
     * @param game The new game. (As a GameData object).
     * @return The GameData object that was stored in the database.
     */
    @Override
    public GameData createGame(GameData game) {
        return null;
    }

    /**
     * Gets a game from the games map and returns it
     * @param gameID The ID of the game we're looking for
     * @return The requested game if found in the database; null if not found.
     */
    @Override
    public GameData findGame(String gameID) {
        return null;
    }

    /**
     * Returns all games in the database (the games map).
     * @return The games map which stores all active games, the key is the gameID and value is the GameData.
     */
    @Override
    public Map<String, GameData> listGames() {
        return games;
    }

    /**
     * Updates a game in the games map.
     * @param game The new game data (As a GameData object)
     * @return The updated GameData object
     * @throws DataAccessException if not able to update game
     */
    @Override
    public GameData updateGame(GameData game) throws DataAccessException {
        return null;
    }

    /**
     * Deletes a game from the games map.
     * @param game The game to delete (As a GameData object)
     * @throws DataAccessException Throws an error if not possible to delete
     */
    @Override
    public void deleteGame(GameData game) throws DataAccessException {}

    /**
     * Generates a new AuthToken and stores it in the authTokens map.
     * @param username The username connected to new token.
     * @return the new AuthToken
     */
    @Override
    public AuthToken createAuthToken(String username) {
        return null;
    }

    /**
     * Finds an AuthToken in the authToken map.
     * @param token The AuthToken to find
     * @return The token if found in the database; null if not found.
     */
    @Override
    public AuthToken findAuthToken(AuthToken token) {
        return null;
    }

    /**
     * Updates an AuthToken in the authToken map
     * @param token The AuthToken to be changed in the database
     * @return The AuthToken that was updated
     * @throws DataAccessException Throws an error if not possible to update token
     */
    @Override
    public AuthToken updateAuthToken(AuthToken token) throws DataAccessException {
        return null;
    }

    /**
     * Deletes an AuthToken from the authToken map
     * @param token The AuthToken to delete
     * @throws DataAccessException Throws an error if something goes wrong
     */
    @Override
    public void deleteAuthToken(AuthToken token) throws DataAccessException {

    }
}
