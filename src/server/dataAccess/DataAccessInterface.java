package server.dataAccess;

import server.ServerException;
import server.models.AuthToken;
import server.models.GameData;
import server.models.UserData;

import java.util.Collection;
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
     * @throws DataAccessException Throw an error if something goes wrong (i.e. the user already exists in the database)
     */
    UserData createUser(UserData user) throws DataAccessException;

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
     * @throws DataAccessException Throws an error if not able to update user (i.e. user doesn't exist in database)
     */
    UserData updateUser(UserData user) throws DataAccessException;

    /**
     * Deletes a user from the database.
     * @param user The UserData object to be deleted.
     * @throws DataAccessException Throws an error if not possible to delete (i.e. the user doesn't exist, etc.)
     */
    void deleteUser(UserData user) throws DataAccessException;

    // Accessing Game Data
    /**
     * Creates a new game and stores it in the database. If the gameID already exists, don't add the game.
     * @param gameName The name of the new game.
     * @return The new GameData object.
     * @throws server.ServerException Throw an error if something goes wrong (i.e. game already exists, etc.)
     */
    GameData createGame(String gameName) throws ServerException;

    /**
     * Gets a game from the database and returns it.
     * @param gameID The ID of the game we're looking for.
     * @return the GameData object if found in the database, returns null if not found.
     */
    GameData findGameById(int gameID);

    /**
     * Finds and returns all games in the database.
     * @return All games as a Map, the key is the gameID and value is the GameData.
     */
    Collection<GameData> listGames();

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
     * Creates a new AuthToken for a given user and stores it in the database.
     * @param username The username connected to new token.
     * @return the new AuthToken.
     * @throws DataAccessException Throws an error if something goes wrong (user already has AuthToken, etc.)
     */
    AuthToken createAuthToken(String username) throws DataAccessException;

    /**
     * Finds an AuthToken in the database.
     * @param authToken The String AuthToken to find.
     * @return The AuthToken if found; returns null if not found.
     */
    AuthToken findAuthToken(String authToken);

    /**
     * Deletes an AuthToken from the database.
     * @param token the String AuthToken to delete.
     * @throws DataAccessException Throws an error if something goes wrong (i.e. the token doesn't exist in database).
     */
    void deleteAuthToken(String authToken) throws DataAccessException;
}
