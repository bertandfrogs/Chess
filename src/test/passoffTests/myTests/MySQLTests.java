package passoffTests.myTests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerException;
import server.dataAccess.DatabaseSQL;
import server.models.UserData;

public class MySQLTests {
    DatabaseSQL db;
    static MySQLTestHelper dbHelper;

    @BeforeEach
    void setup() throws ServerException {
        db = new DatabaseSQL();
        dbHelper = new MySQLTestHelper();
    }

    @AfterAll
    static void cleanup() throws ServerException {
        dbHelper.clearAllTestData();
    }

    @Test
    void testCreateUser() throws ServerException {
        UserData newUser = new UserData("testData-Bobby", "pass", "mail@mail.com");
        db.createUser(newUser);
    }
}
