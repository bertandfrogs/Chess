package passoffTests.myTests;

import org.junit.jupiter.api.*;
import server.ServerException;
import server.dataAccess.DatabaseSQL;
import server.models.AuthToken;
import server.models.GameData;
import server.models.UserData;

import java.util.Collection;

public class MySQLTests {
    static DatabaseSQL db;
    static MySQLTestHelper dbHelper;

    UserData user1;
    UserData user2;
    UserData existingUser;
    static int initialSizeUsers;
    static int initialSizeGames;
    static int initialSizeAuths;

    @BeforeAll
    static void mainSetup() throws ServerException {
        db = new DatabaseSQL();
        dbHelper = new MySQLTestHelper();
        initialSizeUsers = dbHelper.getUsers().size();
        initialSizeGames = dbHelper.getGames().size();
        initialSizeAuths = dbHelper.getSessions().size();
    }

    @BeforeEach
    void setup() throws ServerException {
        dbHelper.clearAllTestData();
        user1 = new UserData("beans88", "12345", "beans88@gmail.com");
        user2 = new UserData("testData-ducky", "quack", "duckyduck@gmail.com");
        existingUser = new UserData("regularGuy", "pass", "mail@mail.com");
    }

    @AfterAll
    static void cleanup() throws ServerException {
        dbHelper.clearAllTestData();
    }

    @Test
    void findUserTest() throws ServerException {
        UserData userInDB = dbHelper.createUser(existingUser);
        Assertions.assertEquals(userInDB, dbHelper.findUser(existingUser.getUsername()));
    }

    @Test
    void createUserTests() throws ServerException {
        // Test that the user isn't found in db yet
        Assertions.assertNull(dbHelper.findUser(user1.getUsername()));

        UserData userInDB = dbHelper.createUser(user1);

        Assertions.assertEquals(userInDB, dbHelper.findUser(user1.getUsername()));

        // now test that it throws an exception when we try to add the same user again, or a different user with same username
        Assertions.assertThrows(ServerException.class, () -> dbHelper.createUser(user1));

        UserData sameUsername = new UserData(user1.getUsername(), "notthesame", "imposter@gmail.com");

        Assertions.assertThrows(ServerException.class, () -> dbHelper.createUser(sameUsername));

        // double check that it still only has one user added
        Assertions.assertEquals(initialSizeUsers + 1, dbHelper.getUsers().size());

        // add another user to the database
        UserData otherUserInDB = dbHelper.createUser(user2);

        Assertions.assertEquals(otherUserInDB, dbHelper.findUser(user2.getUsername()));

        // check that the number of users added is equal to two
        Assertions.assertEquals(initialSizeUsers + 2, dbHelper.getUsers().size());
    }

    @Test
    void updateUserTests() throws ServerException {
        UserData newUser1 = new UserData("testData-beans88", "12345", "beans88@gmail.com");

        // trying to update user that hasn't been added yet
        Assertions.assertThrows(ServerException.class, () -> db.updateUser(newUser1));

        Assertions.assertDoesNotThrow(() -> {
            db.createUser(newUser1);
        });

        newUser1.setPassword("MuchBetterAndMoreSecurePassword");

        Assertions.assertDoesNotThrow(() -> {
            db.updateUser(newUser1);
        });

        // verify that it's changed in the db
        Assertions.assertEquals("MuchBetterAndMoreSecurePassword", db.findUser("testData-beans88").getPassword());

        newUser1.setEmail("newEmail@future.com");
        Assertions.assertDoesNotThrow(() -> {
            db.updateUser(newUser1);
        });

        Assertions.assertEquals("newEmail@future.com", db.findUser("testData-beans88").getEmail());
    }

    @Test
    void deleteUserTests() throws ServerException  {
        UserData newUser1 = new UserData("testData-beans88", "12345", "beans88@gmail.com");

        // trying to delete user that hasn't been added yet
        Assertions.assertThrows(ServerException.class, () -> db.updateUser(newUser1));

        Assertions.assertDoesNotThrow(() -> {
            db.createUser(newUser1);
        });

        Assertions.assertEquals(newUser1, db.findUser("testData-beans88"));

        Assertions.assertDoesNotThrow(() -> db.deleteUser(newUser1));

        Assertions.assertNull(db.findUser("testData-beans88"));

        Assertions.assertEquals(0, db.getUsers().size());
    }

    @Test
    void allUserOperationsTests() throws ServerException  {
        // create 10 users
        Assertions.assertDoesNotThrow(() -> {
            db.createUser(new UserData("testData-test1", "pass", "sbemail"));
            db.createUser(new UserData("testData-test2", "pass", "sbemail"));
            db.createUser(new UserData("testData-test3", "pass", "sbemail"));
            db.createUser(new UserData("testData-test4", "pass", "sbemail"));
            db.createUser(new UserData("testData-test5", "pass", "sbemail"));
            db.createUser(new UserData("testData-test6", "pass", "sbemail"));
            db.createUser(new UserData("testData-test7", "pass", "sbemail"));
            db.createUser(new UserData("testData-test8", "pass", "sbemail"));
            db.createUser(new UserData("testData-test9", "pass", "sbemail"));
            db.createUser(new UserData("testData-test10", "pass", "sbemail"));
        });

        Assertions.assertEquals(10, db.getUsers().size());

        // try to find nonexistent user with similar username
        Assertions.assertNull(db.findUser("testData-test"));

        // try to find other nonexistent user
        Assertions.assertNull(db.findUser("testData-coolDog23456"));

        // throws an exception when we try to add the same user again
        Assertions.assertThrows(ServerException.class, () -> db.createUser(new UserData("testData-test3", "pass", "sbemail")));

        // throws an exception when we try to add a user with duplicate username
        Assertions.assertThrows(ServerException.class, () -> db.createUser(new UserData("testData-test10", "something", "mail@mail.com")));

        Assertions.assertEquals(10, db.getUsers().size());

        // trying to update user that doesn't exist
        Assertions.assertThrows(ServerException.class, () -> db.updateUser(new UserData("testData-fake2", "pass", "sbemail")));

        // update various users
        Assertions.assertDoesNotThrow(() -> {
            db.updateUser(new UserData("testData-test5", "robin", "email"));
            db.updateUser(new UserData("testData-test1", "chickadee", "email"));
            db.updateUser(new UserData("testData-test10", "duck", "email"));
            db.updateUser(new UserData("testData-test6", "goose", "email"));
        });

        // double check db size
        Assertions.assertEquals(10, db.getUsers().size());

        // make sure the user is updated in db
        UserData updated10 = new UserData("testData-test10", "duck", "email");
        Assertions.assertEquals(updated10, db.findUser("testData-test10"));

        // delete some users
        Assertions.assertDoesNotThrow(() -> {
            db.deleteUser(new UserData("testData-test1", "pass", "sbemail"));
            db.deleteUser(new UserData("testData-test2", "pass", "sbemail"));
            db.deleteUser(new UserData("testData-test3", "pass", "sbemail"));
            db.deleteUser(new UserData("testData-test4", "pass", "sbemail"));
            db.deleteUser(new UserData("testData-test5", "pass", "sbemail"));
        });

        // double check db size is updated
        Assertions.assertEquals(5, db.getUsers().size());

        // make sure users no longer exist
        Assertions.assertNull(db.findUser("testData-test1"));
        Assertions.assertNull(db.findUser("testData-test2"));
        Assertions.assertNull(db.findUser("testData-test3"));
        Assertions.assertNull(db.findUser("testData-test4"));
        Assertions.assertNull(db.findUser("testData-test5"));

        // create more users
        Assertions.assertDoesNotThrow(() -> {
            db.createUser(new UserData("testData-test11", "pass", "sbemail"));
            db.createUser(new UserData("testData-test12", "pass", "sbemail"));
            db.createUser(new UserData("testData-test13", "pass", "sbemail"));
            db.createUser(new UserData("testData-test14", "pass", "sbemail"));
            db.createUser(new UserData("testData-test15", "pass", "sbemail"));
        });

        // double check db size is correct
        Assertions.assertEquals(10, db.getUsers().size());

        // CLEAR DATABASE
        Assertions.assertDoesNotThrow(() -> db.clear());

        // make sure users no longer exist
        Assertions.assertNull(db.findUser("testData-test11"));

        // double check db size is correct
        Assertions.assertEquals(0, db.getUsers().size());
    }

    @Test
    void createAuthTokenTests() throws ServerException  {
        AuthToken newAuthToken = db.createAuthToken("testData-beans");
        String tokenStr = newAuthToken.getAuthToken();

        // trying to find nonexistent authToken
        Assertions.assertNull(db.findAuthToken("NOT_REAL"));

        Assertions.assertEquals(newAuthToken, db.findAuthToken(tokenStr));
    }

    @Test
    void deleteAuthTokenTests() throws ServerException {
        AuthToken newAuthToken = db.createAuthToken("testData-birdy");
        String tokenStr = newAuthToken.getAuthToken();

        Assertions.assertEquals(newAuthToken, db.findAuthToken(tokenStr));

        // Normal AuthToken Deletion
        Assertions.assertDoesNotThrow(() -> db.deleteAuthToken(tokenStr));

        Assertions.assertNull(db.findAuthToken(tokenStr));
        Assertions.assertEquals(0, db.getSessions().size());

        // trying to delete it again, should throw an error
        Assertions.assertThrows(ServerException.class, () -> db.deleteAuthToken(tokenStr));
    }

    @Test
    void createGameTests() throws ServerException  {
        GameData newGame = db.createGame("testData-showdown");
        Assertions.assertNotNull(db.getGames());
        Assertions.assertTrue(newGame.getGameId() > 0);
        Assertions.assertEquals(newGame, db.findGameById(newGame.getGameId()));
    }

    @Test
    void testListOneGame() throws ServerException {
        GameData newGame = db.createGame("testData-game1");
        Collection<GameData> list = db.listGames();
    }

    @Test
    void testListManyGames() throws ServerException {
        GameData newGame1 = db.createGame("testData-game1");
        GameData newGame2 = db.createGame("testData-game2");
        GameData newGame3 = db.createGame("testData-game3");
        GameData newGame4 = db.createGame("testData-game4");
        GameData newGame5 = db.createGame("testData-game5");
        Collection<GameData> list = db.listGames();
    }
}

