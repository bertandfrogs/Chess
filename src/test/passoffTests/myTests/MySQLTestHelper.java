package passoffTests.myTests;

import server.ServerException;
import server.dataAccess.DatabaseSQL;
import server.models.AuthToken;
import server.models.UserData;

import java.sql.SQLException;
import java.util.UUID;

public class MySQLTestHelper extends DatabaseSQL {
    public MySQLTestHelper() {

    }

    @Override
    public UserData createUser(UserData user) {
        UUID uuid = UUID.randomUUID();
        String testDataUsername = "testData-" + user.getUsername() + "-" + uuid.toString().substring(0,7);
        return new UserData(testDataUsername, user.getPassword(), user.getEmail());
    }

    @Override
    public AuthToken createAuthToken(String username) {
        UUID uuid = UUID.randomUUID();
        String testDataToken = "testData-" + uuid.toString().substring(0,7);
        return new AuthToken(testDataToken, username);
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
