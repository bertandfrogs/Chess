package server.dataAccess;

import server.models.AuthToken;
import server.models.GameData;
import server.models.UserData;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory representation of @DataAccessInterface. Creates, reads, updates, and deletes data stored in memory.
 */
public class DataAccess implements DataAccessInterface {
    /**
     * A map containing all the users. Key: Username. Value: UserData object.
     */
    final private Map<String, UserData> users = new HashMap<>();

    /**
     * A map containing all the AuthTokens. Key: authToken (String). Value: AuthToken object.
     */
    final private Map<String, AuthToken> authTokens = new HashMap<>();

    /**
     * A map containing all the games. Key: gameID. Value: GameData object.
     */
    final private Map<String, GameData> games = new HashMap<>();

    // Getters
    public Map<String, UserData> getUsers() {
        return users;
    }
    public Map<String, AuthToken> getAuthTokens() {
        return authTokens;
    }
    public Map<String, GameData> getGames() {
        return games;
    }

    /**
     * Clears all stored data in the database by calling clear() on all the maps.
     * Will not be available outside of testing.
     * @throws DataAccessException Throws an error if it's not able to clear the database for some reason.
     */
    @Override
    public void clear() throws DataAccessException {
        try {
            users.clear();
            authTokens.clear();
            games.clear();
        }
        catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Creates a new user and stores it in the users map. If the user or username already exist, don't add them again.
     * @param user The new user's data.
     * @return The new UserData object.
     * @throws DataAccessException Throw an error if something goes wrong (i.e. the user already exists in the database)
     */
    @Override
    public UserData createUser(UserData user) throws DataAccessException {
        if(findUser(user.getUsername()) == null) {
            users.put(user.getUsername(), user);
        }
        else {
            throw new DataAccessException("User already exists in database.");
        }
        return user;
    }

    /**
     * Gets a user from the users map and returns it.
     * @param username The username we're looking for.
     * @return The UserData object if found in the database, returns null if not found.
     */
    @Override
    public UserData findUser(String username) {
        return users.get(username);
    }

    /**
     * Updates a user's information in the users map.
     * @param user The user's updated information (As a UserData object).
     * @return The updated UserData object.
     * @throws DataAccessException Throws an error if not able to perform operation.
     */
    @Override
    public UserData updateUser(UserData user) throws DataAccessException {
        if(findUser(user.getUsername()) != null) {
            users.replace(user.getUsername(), user);
        }
        else {
            throw new DataAccessException("Couldn't update user, not found in database.");
        }
        return user;
    }

    /**
     * Deletes a user from the users map.
     * @param user The user's information (As a UserData object).
     * @throws DataAccessException Throws an error if it's not possible to delete
     */
    @Override
    public void deleteUser(UserData user) throws DataAccessException {
        if(findUser(user.getUsername()) != null) {
            users.remove(user.getUsername());
        }
        else {
            throw new DataAccessException("Couldn't delete user, not found in database.");
        }
    }

    /**
     * Creates a new game and stores it in the database. If the gameID already exists, don't add the game.
     * @param game The new game (as a GameData object).
     * @return The GameData object that was stored in the database.
     * @throws DataAccessException Throw an error if something goes wrong (i.e. the game already exists in the database)
     */
    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        return null;
    }

    /**
     * Gets a game from the games map and returns it.
     * @param gameID The ID of the game we're looking for.
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
     * @param game The updated game (As a GameData object).
     * @return The updated GameData object.
     * @throws DataAccessException Throw an error if it's not able to update game.
     */
    @Override
    public GameData updateGame(GameData game) throws DataAccessException {
        return null;
    }

    /**
     * Deletes a game from the games map.
     * @param game The game to delete (As a GameData object)
     * @throws DataAccessException Throws an error if not possible to delete.
     */
    @Override
    public void deleteGame(GameData game) throws DataAccessException {}

    /**
     * Generates a new AuthToken and stores it in the authTokens map.
     * @param username The username connected to new token.
     * @return the new AuthToken
     * @throws DataAccessException Throw an error if something goes wrong (i.e. the AuthToken already exists in the database)
     */
    @Override
    public AuthToken createAuthToken(String username) throws DataAccessException {
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
