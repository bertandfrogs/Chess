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
    @Override
    public UserData createUser(UserData user) throws ServerException {
        String testDataUsername = "testData-" + user.getUsername();
        return super.createUser(new UserData(testDataUsername, user.getPassword(), user.getEmail()));
    }

    @Override
    public AuthToken createAuthToken(String username) throws ServerException {
        String testDataUsername = "testData-" + username;
        return super.createAuthToken(testDataUsername);
    }

    @Override
    public GameData createGame(String gameName) throws ServerException {
        String testDataGameName = "testData-" + gameName;
        return super.createGame(testDataGameName);
    }

    @Override
    public UserData findUser(String username) throws ServerException {
        String testDataUsername = "testData-" + username;
        return super.findUser(testDataUsername);
    }

    @Override
    public AuthToken findAuthToken(String authToken) throws ServerException {
        String testDataToken = "testData-" + authToken;
        return super.findAuthToken(testDataToken);
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
}
