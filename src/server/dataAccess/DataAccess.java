package server.dataAccess;

import chess.Game;
import models.AuthToken;
import models.GameData;
import models.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory representation of @DataAccessInterface. Creates, reads, updates, and deletes data stored in memory.
 */
public class DataAccess implements DataAccessInterface {
    /**
     * A map containing all the users. Key: Username. Value: UserData object.
     */
    final private Map<String, UserData> users = new HashMap<>();

    /**
     * A map containing all the AuthTokens. Key: Username (String). Value: AuthToken object.
     */
    final private Map<String, AuthToken> authTokens = new HashMap<>();

    /**
     * A map containing all the games. Key: gameID. Value: GameData object.
     */
    final private Map<Integer, GameData> games = new HashMap<>();

    int newGameId = 1000;

    // Getters
    public Map<String, UserData> getUsers() {
        return users;
    }
    public Map<String, AuthToken> getAuthTokens() {
        return authTokens;
    }
    public Map<Integer, GameData> getGames() {
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
     * Stores a new game in the database. If the name already exists, don't add the game.
     * @param gameName The name of the new game.
     * @return The GameData object that was stored in the database.
     */
    @Override
    public GameData createGame(String gameName) {
        newGameId++;
        GameData game = new GameData(newGameId, null, null, gameName, Game.State.pregame, new Game());
        games.put(newGameId, game);
        return game;
    }

    /**
     * Gets a game from the games map and returns it.
     * @param gameID The ID of the game we're looking for.
     * @return The requested game if found in the database; null if not found.
     */
    @Override
    public GameData findGameById(int gameID) {
        return games.get(gameID);
    }

    /**
     * Returns all games in the database (the games map).
     * @return The games map which stores all active games, the key is the gameID and value is the GameData.
     */
    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    /**
     * Updates a game in the games map.
     * @param game The updated game (As a GameData object).
     * @return The updated GameData object.
     * @throws DataAccessException Throw an error if it's not able to update game.
     */
    @Override
    public GameData updateGame(GameData game) throws DataAccessException {
        int gameID = game.getGameId();
        if(findGameById(gameID) != null){
            games.replace(gameID, game);
        }
        else {
            throw new DataAccessException("Game not found.");
        }
        return game;
    }

    /**
     * Deletes a game from the games map.
     * @param game The game to delete (As a GameData object)
     * @throws DataAccessException Throws an error if not possible to delete.
     */
    @Override
    public void deleteGame(GameData game) throws DataAccessException {
        if(findGameById(game.getGameId()) != null) {
            games.remove(game.getGameId());
        }
        else {
            throw new DataAccessException("Couldn't delete game, not found in database.");
        }
    }

    /**
     * Creates a new AuthToken for a username and stores it in the authTokens map.
     * @param username The username connected to new token.
     * @return the new AuthToken
     */
    @Override
    public AuthToken createAuthToken(String username) {
        String newStringToken = UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken(newStringToken, username);
        authTokens.put(newStringToken, authToken);
        return authToken;
    }

    /**
     * Finds an AuthToken in the authToken map.
     * @param authToken The string token to find
     * @return The token if found in the database; null if not found.
     */
    @Override
    public AuthToken findAuthToken(String authToken) {
        return authTokens.get(authToken);
    }

    /**
     * Deletes an AuthToken from the authToken map
     * @param token The AuthToken to delete
     * @throws DataAccessException Throws an error if something goes wrong
     */
    @Override
    public void deleteAuthToken(String token) throws DataAccessException {
        if(findAuthToken(token) != null) {
            authTokens.remove(token);
        }
        else {
            throw new DataAccessException("Couldn't delete token, not found in database.");
        }
    }
}
