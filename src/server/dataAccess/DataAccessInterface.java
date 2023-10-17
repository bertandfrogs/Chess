package server.dataAccess;

import server.models.AuthToken;
import server.models.GameData;
import server.models.UserData;

import java.util.Map;

/**
 * Represents all the functions that can be called on the database.
 * For the most part, functions are CRUD operations: Create, Read, Update, Delete
 */
public interface DataAccessInterface {
    /**
     * Clears all stored data in the database: users, games, authTokens.
     * Will not be available outside of testing.
     * @throws DataAccessException Throws an error if it's not able to clear the database for some reason.
     */
    void clear() throws DataAccessException;

    // Accessing User Data
    /**
     * Creates a new user and stores it in the database. If the user or username already exist, don't add them again.
     * @param user The new user's data (As a UserData object).
     * @return The new UserData object.
     */
    UserData createUser(UserData user);

    /**
     * Gets a user from the database and returns it.
     * @param username The username we're looking for.
     * @return The UserData object if found in the database, returns null if not found.
     */
    UserData findUser(String username);

    /**
     * Updates a user's information in the database.
     * @param user The user's updated information (As a UserData object).
     * @return The updated UserData object.
     * @throws DataAccessException Throws an error if not able to perform operation.
     */
    UserData updateUser(UserData user) throws DataAccessException;

    /**
     * Deletes a user from the database.
     * @param user The UserData object to be updated.
     * @throws DataAccessException Throws an error if not possible to delete
     */
    void deleteUser(UserData user) throws DataAccessException;

    // Accessing Game Data
    /**
     * Creates a new game and stores it in the database. If the gameID already exists, don't add the game.
     * @param game The new GameData object.
     * @return The new GameData object
     */
    GameData createGame(GameData game);

    /**
     * Gets a game from the database and returns it.
     * @param gameID The ID of the game we're looking for.
     * @return the GameData object if found in the database, returns null if not found.
     */
    GameData findGame(String gameID);

    /**
     * Finds and returns all games in the database.
     * @return All games as a Map, the key is the gameID and value is the GameData.
     */
    Map<String, GameData> listGames();

    /**
     * Updates a game in the database.
     * @param game The new GameData object.
     * @return The game that was updated.
     * @throws DataAccessException Throws an error if not able to update game.
     */
    GameData updateGame(GameData game) throws DataAccessException;

    /**
     * Deletes a game from the database.
     * @param game The GameData object to delete.
     * @throws DataAccessException Throws an error if not possible to delete.
     */
    void deleteGame(GameData game) throws DataAccessException;

    /**
     * Creates a new AuthToken and stores it in the database.
     * @param username The username connected to new token.
     * @return the new AuthToken.
     */
    AuthToken createAuthToken(String username);

    /**
     * Finds an AuthToken in the database.
     * @param token The AuthToken to find.
     * @return The AuthToken if found; returns null if not found.
     */
    AuthToken findAuthToken(AuthToken token);

    /**
     * Updates an AuthToken in the database.
     * @param token The updated AuthToken to be changed in the database.
     * @return The updated AuthToken.
     * @throws DataAccessException Throws an error if not possible to update token.
     */
    AuthToken updateAuthToken(AuthToken token) throws DataAccessException;

    /**
     * Deletes an AuthToken from the database.
     * @param token the AuthToken to delete.
     * @throws DataAccessException Throws an error if something goes wrong.
     */
    void deleteAuthToken(AuthToken token) throws DataAccessException;
}
