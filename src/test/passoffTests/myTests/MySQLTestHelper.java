package passoffTests.myTests;

import server.ServerException;
import server.dataAccess.DatabaseSQL;
import server.models.AuthToken;
import server.models.GameData;
import server.models.UserData;

import java.sql.SQLException;
import java.util.UUID;

/**
 * This class is to be used to easily clean up test data while testing database functionality. Uses the DatabaseSQL methods but alters data passed in
 */
public class MySQLTestHelper extends DatabaseSQL {
    public String testString(String str) {
        if(str.contains("testData")){
            return str;
        }
        return "testData-" + str;
    }

    public void clearAllTestData() throws ServerException {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("""
            DELETE FROM users WHERE username LIKE 'testData%'
            """)) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("""
            DELETE FROM games WHERE gameName LIKE 'testData%'
            """)) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("""
            DELETE FROM sessions WHERE username LIKE 'testData%'
            """)) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new ServerException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public void clear() throws ServerException {
        clearAllTestData();
    }

    @Override
    public UserData createUser(UserData user) throws ServerException {
        return super.createUser(new UserData(testString(user.getUsername()), user.getPassword(), user.getEmail()));
    }

    @Override
    public UserData findUser(String username) throws ServerException {
        return super.findUser(testString(username));
    }

    @Override
    public UserData updateUser(UserData user) throws ServerException {
        return super.updateUser(new UserData(testString(user.getUsername()), user.getPassword(), user.getEmail()));
    }

    @Override
    public void deleteUser(UserData user) throws ServerException {
        super.deleteUser(new UserData(testString(user.getUsername()), user.getPassword(), user.getEmail()));
    }

    @Override
    public AuthToken createAuthToken(String username) throws ServerException {
        return super.createAuthToken(testString(username));
    }

    @Override
    public GameData createGame(String gameName) throws ServerException {
        return super.createGame(testString(gameName));
    }

    @Override
    public GameData updateGame(GameData game) throws ServerException {
        return super.updateGame(new GameData(game.getGameId(), game.getWhiteUsername(), game.getBlackUsername(), testString(game.getGameName()), game.getGame()));
    }
}
