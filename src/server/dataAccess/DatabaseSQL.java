package server.dataAccess;
import server.ServerException;
import server.models.AuthToken;
import server.models.GameData;
import server.models.UserData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseSQL implements DataAccessInterface {
    int newGameId = 1000;
    String user = "chess";
    String pass = "jaquemate";

    protected Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", user, pass);
        connection.setCatalog("chess");
        return connection;
    }

    public void configureDatabase() throws SQLException {
        try (var conn = getConnection()) {
            var createDbStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS chess");
            createDbStatement.executeUpdate();

            conn.setCatalog("chess");

            var createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    PRIMARY KEY (username)
                )""";

            var createGamesTable = """
                CREATE TABLE IF NOT EXISTS games (
                    gameID INT NOT NULL,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255),
                    game VARCHAR(4000),
                    PRIMARY KEY (gameID)
                )""";

            var createSessionsTable = """
                CREATE TABLE IF NOT EXISTS sessions (
                    authToken VARCHAR(36) NOT NULL,
                    username VARCHAR(255) NOT NULL,
                    PRIMARY KEY (authToken)
                )""";

            try (var createTableStatement = conn.prepareStatement(createUsersTable)) {
                createTableStatement.executeUpdate();
            }
            try (var createTableStatement = conn.prepareStatement(createGamesTable)) {
                createTableStatement.executeUpdate();
            }
            try (var createTableStatement = conn.prepareStatement(createSessionsTable)) {
                createTableStatement.executeUpdate();
            }
        }
    }

    public Map<String, UserData> getUsers() throws ServerException {
        Map<String, UserData> users = new HashMap<>();
        try (var conn = getConnection()) {
            var selectUsers = """
                 SELECT * from users
                 """;
            try (var preparedStatement = conn.prepareStatement(selectUsers)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        var username = resultSet.getString("username");
                        var password = resultSet.getString("password");
                        var email = resultSet.getString("email");
                        users.put(username, new UserData(username, password, email));
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
        return users;
    }

    public Map<String, AuthToken> getSessions() throws ServerException {
        Map<String, AuthToken> sessions = new HashMap<>();
        try (var conn = getConnection()) {
            var selectSessions = """
                 SELECT * from sessions
                 """;
            try (var preparedStatement = conn.prepareStatement(selectSessions)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        var authToken = resultSet.getString("authToken");
                        var username = resultSet.getString("username");
                        sessions.put(authToken, new AuthToken(authToken, username));
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
        return sessions;
    }

    public Map<Integer, GameData> getGames() throws ServerException {
        Map<Integer, GameData> games = new HashMap<>();
        try (var conn = getConnection()) {
            var selectGames = """
                 SELECT * from games
                 """;
            try (var preparedStatement = conn.prepareStatement(selectGames)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        var gameID = resultSet.getInt("gameID");
                        var whiteUsername = resultSet.getString("whiteUsername");
                        var blackUsername = resultSet.getString("blackUsername");
                        var gameName = resultSet.getString("gameName");
                        var gameJSON = resultSet.getString("game");
                        GameDeserializer gd = new GameDeserializer();
                        games.put(gameID, new GameData(gameID, whiteUsername, blackUsername, gameName, gd.deserialize(gameJSON)));
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
        return games;
    }

    @Override
    public void clear() throws ServerException {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("""
            DELETE from users
            """)) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("""
            DELETE from games
            """)) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("""
            DELETE from sessions
            """)) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public UserData createUser(UserData user) throws ServerException {
        try (var conn = getConnection()) {
            if(findUser(user.getUsername()) != null){
                throw new ServerException(403, "User already exists in database.");
            }

            try (var preparedStatement = conn.prepareStatement("""
                INSERT INTO users (username, password, email) VALUES (?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getEmail());

                preparedStatement.executeUpdate();

                var result = preparedStatement.getGeneratedKeys();
                String createdUsername = "";
                if (result.next()) {
                    createdUsername = result.getString(1);
                }
                return new UserData(createdUsername, user.getPassword(), user.getEmail());
            }
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public UserData findUser(String username) throws ServerException {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("""
                 SELECT * from users WHERE username=?
                 """)) {
                preparedStatement.setString(1, username);

                try (var resultSet = preparedStatement.executeQuery()) {
                    UserData foundUser = null;
                    if (resultSet.next()) {
                        var password = resultSet.getString("password");
                        var email = resultSet.getString("email");
                        foundUser = new UserData(username, password, email);
                    }
                    return foundUser;
                }
            }
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public UserData updateUser(UserData user) throws ServerException {
        UserData userInDb = findUser(user.getUsername());
        if(userInDb != null){
            try (var conn = getConnection()) {
                try (var preparedStatement = conn.prepareStatement("""
                     UPDATE users
                     SET password=?, email=?
                     WHERE username=?
                     """)) {
                    preparedStatement.setString(1, user.getPassword());
                    preparedStatement.setString(2, user.getEmail());
                    preparedStatement.setString(3, user.getUsername());

                    preparedStatement.executeUpdate();

                    return user;
                }
            }
            catch (SQLException e) {
                throw new ServerException(e.getErrorCode(), e.getMessage());
            }
        }
        else {
            throw new ServerException(500, "Couldn't update user, not found in database.");
        }
    }

    @Override
    public void deleteUser(UserData user) throws ServerException {
        try (var conn = getConnection()) {
            if(findUser(user.getUsername()) != null){
                try (var preparedStatement = conn.prepareStatement("""
                DELETE from users WHERE username=?
                """)) {
                    preparedStatement.setString(1, user.getUsername());
                    preparedStatement.executeUpdate();
                }
            }
            else {
                throw new ServerException(400, "bad request");
            }
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public AuthToken createAuthToken(String username) throws ServerException {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("""
            INSERT INTO sessions (authToken, username) VALUES (?, ?)
            """)) {
                String newStringToken = UUID.randomUUID().toString();
                preparedStatement.setString(1, newStringToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
                return new AuthToken(newStringToken, username);
            }
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public AuthToken findAuthToken(String authToken) throws ServerException {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("""
                 SELECT * from sessions WHERE authToken=?
                 """)) {
                preparedStatement.setString(1, authToken);

                try (var resultSet = preparedStatement.executeQuery()) {
                    AuthToken foundToken = null;
                    if (resultSet.next()) {
                        var username = resultSet.getString("username");
                        foundToken = new AuthToken(authToken, username);
                    }
                    return foundToken;
                }
            }
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public void deleteAuthToken(String authToken) throws ServerException {
        try (var conn = getConnection()) {
            if(findAuthToken(authToken) != null){
                try (var preparedStatement = conn.prepareStatement("""
                DELETE from sessions WHERE authToken=?
                """)) {
                    preparedStatement.setString(1, authToken);
                    preparedStatement.executeUpdate();
                }
            }
            else {
                throw new ServerException(400, "bad request");
            }
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public GameData createGame(String gameName) throws ServerException {
        try (var conn = getConnection()) {
            
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
        return null;
    }

    @Override
    public GameData findGameById(int gameID) throws ServerException {
        try (var conn = getConnection()) {

        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws ServerException {
        try (var conn = getConnection()) {

        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
        return null;
    }

    @Override
    public GameData updateGame(GameData game) throws ServerException {
        try (var conn = getConnection()) {

        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteGame(GameData game) throws ServerException {
        try (var conn = getConnection()) {

        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
    }


//    public static void main(String[] args) throws Exception {
//        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chess", "chess", "jaquemate")) {
////            var catalogs = conn.getMetaData().getCatalogs();
////            while (catalogs.next()) {
////                System.out.println(catalogs.getString(1));
////            }
//
//            conn.setCatalog("chess");
//
//            try (var preparedStatement = conn.prepareStatement("SELECT username, password, email from users")) {
//                try (var rs = preparedStatement.executeQuery()) {
//                    while (rs.next()) {
//                        var username = rs.getString("username");
//                        var password = rs.getString("password");
//                        var email = rs.getString("email");
//
//                        System.out.printf("username: %s, password: %s, email: %s%n", username, password, email);
//                    }
//                }
//            }
//        }
//    }
}
